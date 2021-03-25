package com.ecomm.catalogservice.exception

class BadRequestException (override val message: String?) : Exception(message)

class ProductAlreadyExistsException (override val message: String?) : Exception(message)

class ProductNotFoundException (override val message: String?) : Exception(message)

class ForbiddenException (override val message: String?) : Exception(message)

class UnauthorizedException (override val message: String?) : Exception(message)

class ServiceUnavailableException (override val message: String?) : Exception(message)