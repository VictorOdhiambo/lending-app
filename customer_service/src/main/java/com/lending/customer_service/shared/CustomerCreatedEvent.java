package com.lending.customer_service.shared;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CustomerCreatedEvent {
    // Getters and Setters
    private String id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public CustomerCreatedEvent() {
    }

    public CustomerCreatedEvent(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

}
