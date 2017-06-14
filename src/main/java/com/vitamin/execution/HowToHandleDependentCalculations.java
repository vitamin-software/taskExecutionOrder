package com.vitamin.execution;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HowToHandleDependentCalculations {

    private static Supplier<String> foo(String value, long calcTime){
        return () ->{
            try{
                if(calcTime < 100)
                    throw new RuntimeException("Something went wrong");
                Thread.sleep(calcTime);
                return value;
            }catch (InterruptedException ie){
                Thread.interrupted();
                throw new RuntimeException(ie);
            }
        };
    }

    public static void main(String args[]) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(3);
        try {
            Calc<String> A = new Calc<>(foo("A", 1000));
            Calc<String> B = new Calc<>(foo("B", 500));

            Calc<String> C = new Calc<>(()-> "C" + A.get()+ B.get(), A, B);

            Calc<String> D = new Calc<>(foo("D", 2000));
            Calc<String> E = new Calc<>(foo("E", 3000));

            Calc<String> F = new Calc<>(()-> "F" + E.get()+D.get()+C.get(), E, D, C);

            long start = System.currentTimeMillis();

            String result = F.calculateWith(es).get();
            long end = System.currentTimeMillis();
            System.out.printf("\nResult:%s Took:%d ms", result, (end - start));
        }finally {
            es.shutdownNow();
        }
    }

    /* This class is here to represent a calculation work. */
    private static class Calc<T> implements Supplier<T> {
        private final Supplier<T> tSupplier;
        private final List<Calc<T>> dependecies;

        private CompletableFuture<T> result;
        private boolean calculated = false;

        private ReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final Lock readLock = rwLock.readLock();
        private final Lock writeLock = rwLock.writeLock();


        public Calc(Supplier<T> tSupplier) {
            this(tSupplier, null);
        }

        public Calc(Supplier<T> tSupplier, Calc<T>... dependecies) {
            this.tSupplier = tSupplier;
            this.dependecies = (dependecies != null) ? Arrays.asList(dependecies) : Collections.emptyList();
        }

        private Optional<CompletableFuture<?>> chainAll(ExecutorService executorService) {
            if (dependecies.size() == 0)
                return Optional.empty();

            List<CompletableFuture<T>> executing = this.dependecies.stream()
                    .map(calc -> calc.calculateWith(executorService))
                    .collect(Collectors.toList());

            return Optional.of(CompletableFuture.allOf(executing.toArray(new CompletableFuture<?>[executing.size()])));
        }

        @Override
        public T get(){
            CompletableFuture<T> future = calculateWith(ForkJoinPool.commonPool());
            try {
                return future.get();
            }catch (InterruptedException | ExecutionException e){
                throw new RuntimeException(e);
            }
        }


        public CompletableFuture<T> calculateWith(ExecutorService executorService) {
            CompletableFuture<T> cachedResult = null;
            try {
                readLock.lockInterruptibly();
                if (!calculated) {
                    readLock.unlock();
                    writeLock.lockInterruptibly();
                    try {
                        if (!calculated) {
                            calculated = true;
                            Optional<CompletableFuture<?>> chain = chainAll(executorService);
                            result = chain.map(c -> c.thenApplyAsync((___) -> this.tSupplier.get(), executorService))
                                    .orElseGet(() -> CompletableFuture.supplyAsync(tSupplier, executorService));
                        }
                        readLock.lockInterruptibly();
                    } finally {
                        writeLock.unlock();
                    }
                }
                cachedResult = result;
            } catch (InterruptedException ie) {
                Thread.interrupted();
            } finally {
                readLock.unlock();
            }
            return cachedResult;
        }
    }
}
