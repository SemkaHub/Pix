package com.example.pix.ui.utils

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.pix.R
import com.example.pix.domain.error.DomainError

fun DomainError.getFriendlyMessage(context: Context): String = when (this) {
    is DomainError.Network -> getString(context, R.string.error_network)
    is DomainError.Database -> getString(context, R.string.error_database)
    is DomainError.Server.BadRequest -> getString(context, R.string.error_bad_request)
    is DomainError.Server.NotFound -> getString(context, R.string.error_not_found)
    is DomainError.Server.Generic -> getString(context, R.string.error_generic)
    is DomainError.Unknown ->
        "${getString(context, R.string.error_unknown)}: ${this.error.message}"
}