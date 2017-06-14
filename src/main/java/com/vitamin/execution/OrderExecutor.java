package com.vitamin.execution;

import java.util.List;

public interface OrderExecutor {

    /**
     *  Execution should begin with this signal
     */
    void executeSignal();

    /**
     *  Execution should stop gracefully
     *  @return execution order of Orders
     */
    List<Order> stopSignal();
}
