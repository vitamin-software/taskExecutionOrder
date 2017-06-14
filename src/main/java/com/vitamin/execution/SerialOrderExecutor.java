package com.vitamin.execution;


import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 *  Serial execution of orders - Guaranteed fair and dependency order
 *
 *  It is capable of executing orders available at construction.
 *
 */
public class SerialOrderExecutor implements OrderExecutor {

    private final Queue<Order> orders;
    private List<Order> executionOrder;

    public SerialOrderExecutor(Queue<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void executeSignal() {
        // It can be replaced with for-each loop.
        executionOrder = orders.stream()
                .map(OrderWorker::new)
                .map(OrderWorker::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> stopSignal() {
        return executionOrder;
    }
}
