package com.studiomk.matool.application.service

import SignInResult

import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.contracts.auth.AuthProvider
import com.studiomk.matool.domain.contracts.auth.SignInResponse
import com.studiomk.matool.domain.contracts.auth.UpdateEmailResult
import com.studiomk.matool.domain.entities.shared.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthService : KoinComponent {

    private val authProvider: AuthProvider by inject()

    private val mutex = Mutex()

    suspend fun initialize(): Result<UserRole, AuthError> = mutex.withLock {
        val initializeResult = authProvider.initialize()
        if (initializeResult is Result.Failure) {
            return Result.Failure(initializeResult.error)
        }
        val userRoleResult = authProvider.getUserRole()
        return when (userRoleResult) {
            is Result.Success -> Result.Success(userRoleResult.value)
            is Result.Failure -> {
                Result.Failure(userRoleResult.error)
            }
        }
    }

    suspend fun signIn(username: String, password: String): SignInResult = mutex.withLock {
        val signInResponse = authProvider.signIn(username, password)
        when (signInResponse) {
            is SignInResponse.Failure -> {
                SignInResult.Failure(signInResponse.error)
            }
            is SignInResponse.NewPasswordRequired -> SignInResult.NewPasswordRequired
            is SignInResponse.Success -> {
                val userRoleResult = authProvider.getUserRole()
                when (userRoleResult) {
                    is Result.Success -> SignInResult.Success(userRoleResult.value)
                    is Result.Failure -> {
                        SignInResult.Failure(userRoleResult.error)
                    }
                }
            }
        }
    }

    suspend fun confirmSignIn(password: String): Result<UserRole, AuthError> = mutex.withLock {
        val confirmSignInResult = authProvider.confirmSignIn(password)
        if (confirmSignInResult is Result.Failure) {
            return Result.Failure(confirmSignInResult.error)
        }
        val userRoleResult = authProvider.getUserRole()
        return when (userRoleResult) {
            is Result.Success -> Result.Success(userRoleResult.value)
            is Result.Failure -> {
                Result.Failure(userRoleResult.error)
            }
        }
    }

    suspend fun signOut(): Result<UserRole, AuthError> = mutex.withLock {
        val signOutResult = authProvider.signOut()
        if (signOutResult is Result.Failure) {
            return Result.Failure(signOutResult.error)
        }
        return Result.Success(UserRole.Guest)
    }

    suspend fun getAccessToken(): String? = mutex.withLock {
        val tokenResult = authProvider.getTokens()
        when (tokenResult){
            is Result.Success -> tokenResult.value
            is Result.Failure -> null
        }
    }

    suspend fun changePassword(current: String, new: String): Result<Unit, AuthError> = mutex.withLock {
        authProvider.changePassword(current, new)
    }

    suspend fun resetPassword(username: String): Result<Unit, AuthError> = mutex.withLock {
        authProvider.resetPassword(username)
    }

    suspend fun confirmResetPassword(username: String, newPassword: String, code: String): Result<Unit, AuthError> = mutex.withLock {
        authProvider.confirmResetPassword(username, newPassword, code)
    }

    suspend fun updateEmail(newEmail: String): UpdateEmailResult = mutex.withLock {
        authProvider.updateEmail(newEmail)
    }

    suspend fun confirmUpdateEmail(code: String): Result<Unit, AuthError> = mutex.withLock {
        authProvider.confirmUpdateEmail(code)
    }

    fun isValidPassword(password: String): Boolean {
        val lengthRule = password.length >= 8
        val hasNumber = password.contains(Regex("[0-9]"))
        val hasUppercase = password.contains(Regex("[A-Z]"))
        val hasLowercase = password.contains(Regex("[a-z]"))
        return lengthRule && hasNumber && hasUppercase && hasLowercase
    }
}

