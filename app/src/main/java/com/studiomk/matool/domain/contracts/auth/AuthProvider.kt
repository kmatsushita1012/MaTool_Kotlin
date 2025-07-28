package com.studiomk.matool.domain.contracts.auth

import com.studiomk.matool.domain.entities.shared.*

interface AuthProvider {
    fun initialize(): Unit
    suspend fun signIn(username: String, password: String): SignInResponse
    suspend fun confirmSignIn(newPassword: String): Result<String, AuthError>
    suspend fun getUserRole(): Result<UserRole, AuthError>
    suspend fun getTokens(): Result<String?, AuthError>
    suspend fun signOut(): Result<Boolean, AuthError>
    suspend fun changePassword(current: String, new: String): Result<Unit, AuthError>
    suspend fun resetPassword(username: String): Result<Unit, AuthError>
    suspend fun confirmResetPassword(username: String, newPassword: String, code: String): Result<Unit, AuthError>
    suspend fun updateEmail(newEmail: String): UpdateEmailResult
    suspend fun confirmUpdateEmail(code: String): Result<Unit, AuthError>
}


// アクセストークンストア
class AwsCognitoAccessTokenStore {
    var value: String? = null
}