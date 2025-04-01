package com.example.pix.domain.error

sealed class DomainError: Throwable() {
    data object Network : DomainError() {
        private fun readResolve(): Any = Network
    }

    data object Database : DomainError() {
        private fun readResolve(): Any = Database
    }

    sealed class Server : DomainError() {
        // 400
        data object BadRequest : Server() {
            private fun readResolve(): Any = BadRequest
        }

        // 404
        data object NotFound : Server() {
            private fun readResolve(): Any = NotFound
        }

        data class Generic(val code: Int, val errorMessage: String?) : Server()
    }

    data class Unknown(val error: Throwable) : DomainError()
}