package com.ecomm.catalogservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@ControllerAdvice
class ControllerAdviceRequestError : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [(ProductAlreadyExistsException::class)])
    fun handleProductAlreadyExists(ex: ProductAlreadyExistsException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Validation Failed",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(BadRequestException::class)])
    fun handleBadRequest(ex: BadRequestException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Request Failed",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(ProductNotFoundException::class)])
    fun handleProductNotFound(ex: ProductNotFoundException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Product Not Found",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [(ForbiddenException::class)])
    fun handleForbiddenException(ex: ForbiddenException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Forbidden request",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(value = [(UnauthorizedException::class)])
    fun handleUnauthorizedException(ex: UnauthorizedException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Unauthorized request",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(value = [(ServiceUnavailableException::class)])
    fun handleServiceUnavailableException(ex: ServiceUnavailableException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Service not available",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.SERVICE_UNAVAILABLE)
    }
}