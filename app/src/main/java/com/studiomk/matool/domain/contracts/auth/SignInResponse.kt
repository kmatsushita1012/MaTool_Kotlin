package com.studiomk.matool.domain.contracts.auth

sealed class SignInResponse{
    object Success : SignInResponse()
    object NewPasswordRequired : SignInResponse()
    data class Failure(val error: AuthError) : SignInResponse()
}