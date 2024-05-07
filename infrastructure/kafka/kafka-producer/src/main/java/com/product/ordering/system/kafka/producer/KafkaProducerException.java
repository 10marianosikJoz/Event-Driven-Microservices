package com.product.ordering.system.kafka.producer;

class KafkaProducerException extends RuntimeException {

    KafkaProducerException(String message) {
        super(message);
    }
}
