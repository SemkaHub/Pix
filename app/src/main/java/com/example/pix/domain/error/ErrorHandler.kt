package com.example.pix.domain.error

interface ErrorHandler {
    fun handleError(error: Throwable): DomainError
}