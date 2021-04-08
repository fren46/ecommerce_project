package com.ecomm.walletservice
import org.springframework.kafka.support.serializer.JsonDeserializer
import com.ecomm.commons.OrderDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.apache.kafka.common.serialization.StringDeserializer


@SpringBootApplication
class WalletserviceApplication

fun main(args: Array<String>) {
    runApplication<WalletserviceApplication>(*args)
}
