package com.ecomm.catalogservice

import com.ecomm.catalogservice.dto.ProductDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.support.beans
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@SpringBootApplication
@EnableScheduling
@EnableCaching
class CatalogserviceApplication()

fun main(args: Array<String>) {
    runApplication<CatalogserviceApplication>(*args){
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
                bean("ProductJsonListener"){
                    val config = mapOf(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"
                    )
                    val consumerFactory = DefaultKafkaConsumerFactory(
                        config, StringDeserializer(), JsonDeserializer(ProductDTO::class.java)
                    )

                    val factory = ConcurrentKafkaListenerContainerFactory<String,
                            ProductDTO>()
                    factory.consumerFactory = consumerFactory;
                    factory
                }
                bean {
				    fun user(user: String, pw: String, vararg roles: String) =
					    User.withDefaultPasswordEncoder()
						    .username(user).password(pw).roles(*roles).build()

				    InMemoryUserDetailsManager(
					    user("user", "password", "USER"),
					    user("admin", "password", "USER", "ADMIN"))
			    }
            }
        )*/
    }
}
