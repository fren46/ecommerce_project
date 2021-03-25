package com.ecomm.warehouseservice

import com.mongodb.client.MongoClients
import com.mongodb.MongoClientSettings
import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {
    override fun getDatabaseName(): String {
        return "warehouse"
    }

    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://localhost:27017/warehouse")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings)
    }

    public override fun getMappingBasePackages(): MutableCollection<String> {
        return mutableListOf("com.example")
    }
}