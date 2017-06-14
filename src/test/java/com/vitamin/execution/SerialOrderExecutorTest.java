package com.vitamin.execution;


import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.vitamin.execution.Order.create;

public class SerialOrderExecutorTest {

    private Order[] orderItems = {
            create("A", Order.Status.BEGIN, 60),
            create("B", Order.Status.BEGIN, 50),
            create("A", Order.Status.UPDATE, 20),
            create("C", Order.Status.BEGIN, 50),
            create("D", Order.Status.BEGIN, 50),
            create("E", Order.Status.BEGIN, 50),
            create("F", Order.Status.BEGIN, 50),
            create("G", Order.Status.BEGIN, 50)};

    @Test
    public void testExecution() throws Exception {
        Queue<Order> orders = new LinkedList<>(Arrays.asList(orderItems));
        OrderExecutor executor = new SerialOrderExecutor(orders);
        executor.executeSignal();
        List<Order> executionOrder = executor.stopSignal();
        Assert.assertEquals(orders, executionOrder);
    }
}
