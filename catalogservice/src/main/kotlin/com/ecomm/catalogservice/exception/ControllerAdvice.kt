package com.ecomm.catalogservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
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

    @ExceptionHandler(value = [(OrderListNotFoundException::class)])
    fun handleOrderListNotFound(ex: OrderListNotFoundException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "List of order not found",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [(OrderNotFoundException::class)])
    fun handleOrderNotFound(ex: OrderNotFoundException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Order not found",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [(BadRequestDeletionOrderException::class)])
    fun BadRequestDeletionOrderException(ex: BadRequestDeletionOrderException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Cannot delete an Order already sent",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(NewStatusOrderException::class)])
    fun NewStatusOrderException(ex: NewStatusOrderException, request: WebRequest): ResponseEntity<ErrorsDetails> {
        val errorDetails = ErrorsDetails(
            Date(),
            "Cannot handle the request",
            ex.message!!
        )
        return ResponseEntity(errorDetails, HttpStatus.SERVICE_UNAVAILABLE)
    }

    // serve per gestire l'errore che si verifica se faccio una post passando un valore sbagliato ad un campo
    // es. metto una categoria del prodotto che non esiste.
    // però questo handler non funziona.
//    @ExceptionHandler(value = [(HttpMessageNotReadableException::class)])
//    fun HttpMessageNotReadable(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<ErrorsDetails> {
//        val errorDetails = ErrorsDetails(
//            Date(),
//            "Error in the body of the request",
//            ex.message!!
//        )
//        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
//    }

}