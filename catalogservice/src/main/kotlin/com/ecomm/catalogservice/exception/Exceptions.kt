package com.ecomm.catalogservice.exception

class BadRequestException (override val message: String?) : Exception(message)

class WarehouseNotFoundException (override val message: String?) : Exception(message)

class WalletNotFoundException (override val message: String?) : Exception(message)

class ProductAlreadyExistsException (override val message: String?) : Exception(message)

class ProductNotFoundException (override val message: String?) : Exception(message)

class ForbiddenException (override val message: String?) : Exception(message)

class UnauthorizedException (override val message: String?) : Exception(message)

class ServiceUnavailableException (override val message: String?) : Exception(message)

class OrderListNotFoundException (override val message: String?) : Exception(message)

class OrderNotFoundException (override val message: String?) : Exception(message)

class BadRequestDeletionOrderException (override val message: String?) : Exception(message)

class NewStatusOrderException (override val message: String?) : Exception(message)

class NotModifiedWarningException (override val message: String?) : Exception(message)