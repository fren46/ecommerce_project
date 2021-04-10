package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.UserDTO
import com.ecomm.catalogservice.repo.UserRepository
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController (
    private val userRepository: UserRepository
        ) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns all the users")
    fun getUsers(): List<UserDTO> {
        val users = userRepository.findAll()
        return users.map { user ->
            UserDTO(
                user.id,
                user.name,
                user.surname,
                user.email,
                user.deliveryAddress,
                user.roles
            )
        }
    }
}