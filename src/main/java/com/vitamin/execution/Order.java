package com.vitamin.execution;

import java.util.Objects;

public class Order {
    public enum Status{
        BEGIN,
        UPDATE,
        CLOSE
    }

    private final String id; // Unique id
    private final Status status;
    private final long executionTime; // To simulate processing time

    public Order(String id, Status status, long executionTime) {
        this.id = id;
        this.status = status;
        this.executionTime = executionTime;
    }

    // For convenience
    public static Order create(String id, Status status, long executionTime){
        return new Order(id, status, executionTime);
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
