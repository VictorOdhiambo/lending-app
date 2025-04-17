package com.lending.customer_service.config;

import com.lending.customer_service.shared.CustomerCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class StreamConfig {
    private final Sinks.Many<CustomerCreatedEvent> customerCreatedSink =
            Sinks.many().multicast().onBackpressureBuffer();

    @Bean
    public Sinks.Many<CustomerCreatedEvent> customerCreatedSink() {
        return customerCreatedSink;
    }

    @Bean
    public Supplier<CustomerCreatedEvent> customerCreatedSupplier() {
        return () -> customerCreatedSink.asFlux().blockFirst();
    }
}
