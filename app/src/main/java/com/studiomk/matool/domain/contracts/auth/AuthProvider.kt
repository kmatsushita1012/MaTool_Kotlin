package com.studiomk.matool.domain.contracts.auth

import com.studiomk.matool.domain.entities.shared.*

interface AuthProvider {
    suspend fun initialize(): Result<String, AuthError>
    suspend fun signIn(username: String, password: String): SignInResponse
    suspend fun confirmSignIn(newPassword: String): Result<String, AuthError>
    suspend fun getUserRole(): Result<UserRole, AuthError>
    suspend fun getTokens(): Result<String?, AuthError>
    suspend fun signOut(): Result<Boolean, AuthError>
}


// アクセストークンストア
class AwsCognitoAccessTokenStore {
    var value: String? = null
}