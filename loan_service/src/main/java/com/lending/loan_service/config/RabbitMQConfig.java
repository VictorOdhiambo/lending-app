package com.lending.loan_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.loan-processing}")
    private String loanProcessingQueue;

    @Value("${rabbitmq.exchange.loan}")
    private String loanExchange;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routing-key.process-loan}")
    private String processLoanRoutingKey;

    @Value("${rabbitmq.queue.loan-approval}")
    private String loanApprovalQueue;

    @Value("${rabbitmq.queue.loan-status}")
    private String loanStatusQueue;

    @Value("${rabbitmq.routing-key.approve-loan}")
    private String approveLoanRoutingKey;

    @Value("${rabbitmq.queue.loan-disbursement}")
    private String loanDisbursementQueue;

    @Value("${rabbitmq.routing-key.disburse-loan}")
    private String disburseLoanRoutingKey;

    @Value("${rabbitmq.routing-key.loan-status}")
    private String loanStatusRoutingKey;


    @Bean
    public Queue loanProcessingQueue() {
        return new Queue(loanProcessingQueue, true);
    }

    @Bean
    public Queue loanApprovalQueue() {
        return new Queue(loanApprovalQueue, true);
    }

    @Bean
    public Queue loanDisbursementQueue() {
        return new Queue(loanDisbursementQueue, true);
    }

    @Bean
    public Queue loanStatusQueue() {
        return new Queue(loanStatusQueue, true);
    }

    @Bean
    public TopicExchange loanExchange() {
        return new TopicExchange(loanExchange);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchange);
    }

    @Bean
    public Binding bindLoanProcessing() {
        return BindingBuilder.bind(loanProcessingQueue())
                .to(loanExchange())
                .with(processLoanRoutingKey);
    }

    @Bean
    public Binding bindLoanApproval() {
        return BindingBuilder.bind(loanApprovalQueue())
                .to(loanExchange())
                .with(approveLoanRoutingKey);
    }

    @Bean
    public Binding bindLoanDisbursement() {
        return BindingBuilder.bind(loanDisbursementQueue())
                .to(loanExchange())
                .with(disburseLoanRoutingKey);
    }

    @Bean
    public Binding bindLoanStatus() {
        return BindingBuilder.bind(loanStatusQueue())
                .to(notificationExchange())
                .with(loanStatusRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
