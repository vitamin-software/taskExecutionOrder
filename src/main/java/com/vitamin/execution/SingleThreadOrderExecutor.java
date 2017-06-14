package com.vitamin.execution;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 *  This version acts similar to Serial executor - Guaranteed fair and dependency order except
 *    * It is not going to execute Orders with the Thread calling executeSignal
 *    * executeSignal and stopSignal can be called by different threads.
 *    * Allows new Orders to be added while execution in progress.
 */
public class SingleThreadOrderExecutor implements OrderExecutor{

    private final BlockingQueue<Order> orders;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private volatile boolean shouldStop;
    private volatile Future<List<Order>> executionOrderFuture;

    public SingleThreadOrderExecutor(BlockingQueue<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void executeSignal() {
        executionOrderFuture = executorService.submit(() -> {
            List<Order> executionOrder = new LinkedList<>();
            while (!shouldStop){
               try {
                   Order order = orders.poll(5, TimeUnit.MILLISECONDS);
                   if(order != null){
                       executionOrder.add(new OrderWorker(order).get());
                   }
               }catch (InterruptedException ie){
                   Thread.interrupted();
                   shouldStop = true;
               }
            }

            /*Gracefully stopping so all submitted Orders will be processed.*/
            Order order;
            while ( (order = orders.poll()) != null){
                executionOrder.add(new OrderWorker(order).get());
            }

            return executionOrder;
        });
    }

    @Override
    public List<Order> stopSignal() {
        this.shouldStop = true;
        executorService.shutdown();
        try {
            return executionOrderFuture.get();
        }catch (InterruptedException | ExecutionException e){
            // Ignoring these exceptions
            return Collections.emptyList();
        }
    }
}
