package com.vitamin.execution;


import java.util.function.Supplier;

/*
    Execution of Work
 */
public class OrderWorker implements Supplier<Order> {
    private final Order order;

    public OrderWorker(Order order) {
        this.order = order;
    }

    @Override
    public Order get() {
        System.out.printf("%s is executing %s with status %s. \n", Thread.currentThread().getId(), order.getId(), order.getStatus().name());
        try {
            Thread.sleep(order.getExecutionTime());
        }catch (InterruptedException ie){Thread.currentThread().interrupt();}
        System.out.printf("%s has executed %s with status %s. \n", Thread.currentThread().getId(), order.getId(), order.getStatus().name());
        return this.order;
    }
}
