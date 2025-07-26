package com.studiomk.matool.domain.contracts.auth

import com.studiomk.matool.domain.entities.shared.AuthError

sealed class UpdateEmailResult {
    object Completed : UpdateEmailResult()
    data class VerificationRequired(val destination: String) : UpdateEmailResult()
    data class Failure(val error: AuthError) : UpdateEmailResult()
}
