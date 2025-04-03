package com.example.pix.data.error

import android.database.sqlite.SQLiteException
import com.example.pix.domain.error.DomainError
import com.example.pix.domain.error.ErrorHandler
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ErrorHandlerImpl @Inject constructor() : ErrorHandler {

    override fun handleError(error: Throwable): DomainError {
        return when (error) {
            is IOException -> DomainError.Network

            is SQLiteException -> DomainError.Database

            is HttpException -> {
                when (error.code()) {
                    400 -> DomainError.Server.BadRequest
                    404 -> DomainError.Server.NotFound
                    else -> DomainError.Server.Generic(error.code(), error.message())
                }
            }

            else -> DomainError.Unknown(error)
        }
    }
}