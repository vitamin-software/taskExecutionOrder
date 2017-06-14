package com.vitamin.execution;


import java.util.List;
import java.util.concurrent.*;

/**
 *   The idea here to process independent orders concurrently.
 */
public class MultiThreadOrderExecutor implements OrderExecutor{

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

    private final BlockingQueue<Order> orders;

    public MultiThreadOrderExecutor(BlockingQueue<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void executeSignal() {

    }

    @Override
    public List<Order> stopSignal() {
        return null;
    }
}
