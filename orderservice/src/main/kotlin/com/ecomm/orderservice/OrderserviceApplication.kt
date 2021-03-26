package com.ecomm.orderservice

import com.ecomm.commons.OrderDTO
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.bytebuddy.jar.asm.TypeReference
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


@SpringBootApplication
class OrderserviceApplication

fun main(args: Array<String>) {

    runApplication<OrderserviceApplication>(*args)
        /*addInitializers(
            beans {
                bean<KafkaTemplate<String, Any>> {
                    val producerFactory = DefaultKafkaProducerFactory<String, Any>(mapOf(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to
                                "org.apache.kafka.common.serialization.StringSerializer",
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to
                                "org.springframework.kafka.support.serializer.JsonSerializer"))
                    KafkaTemplate<String, Any>(producerFactory);
                }
                bean("OrderJsonListener"){

                    val config = mapOf(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"
                    )
                    val consumerFactory = DefaultKafkaConsumerFactory(
                        config, StringDeserializer(), JsonDeserializer(OrderDTO::class.java)
                    )
                    val factory = ConcurrentKafkaListenerContainerFactory<String,
                            OrderDTO>()
                    factory.consumerFactory = consumerFactory;
                    factory
                }
            }
        )*/
}
