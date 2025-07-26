package com.studiomk.matool.data.auth.aws_cognito

import androidx.compose.runtime.key
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthUpdateUserAttributeOptions
import com.amplifyframework.auth.result.step.AuthNextUpdateAttributeStep
import com.amplifyframework.auth.result.step.AuthUpdateAttributeStep
import com.amplifyframework.core.Amplify
import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.contracts.auth.AuthProvider
import com.studiomk.matool.domain.contracts.auth.SignInResponse
import com.studiomk.matool.domain.contracts.auth.UpdateEmailResult
import com.studiomk.matool.domain.entities.shared.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AwsCognitoProvider : AuthProvider {
    override suspend fun initialize(): Result<String, AuthError> =
        suspendCancellableCoroutine { cont ->
            try {
                cont.resume(Result.Success("Success"))
            } catch (e: Exception) {
                cont.resume(Result.Failure(AuthError.Unknown("init ${e.localizedMessage}")))
            }
        }

    override suspend fun signIn(username: String, password: String): SignInResponse =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.signIn(
                username,
                password,
                { result ->
                    if (result.isSignedIn) {
                        cont.resume(SignInResponse.Success)
                    } else if (result.nextStep.signInStep.toString()
                            .contains("CONFIRM_SIGN_IN_WITH_NEW_PASSWORD")
                    ) {
                        cont.resume(SignInResponse.NewPasswordRequired)
                    } else {
                        cont.resume(SignInResponse.Failure(AuthError.Unknown("Sign-in state: ${result.nextStep.signInStep}")))
                    }
                },
                { error ->
                    cont.resume(SignInResponse.Failure(AuthError.Unknown("signIn ${error.localizedMessage}")))
                }
            )
        }

    override suspend fun confirmSignIn(newPassword: String): Result<String, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.confirmSignIn(
            newPassword,
            { result ->
                if (result.isSignedIn) {
                    cont.resume(Result.Success("Success"))
                } else {
                    cont.resume(
                        Result.Failure(
                            AuthError.Unknown("Sign-in not complete: ${result.nextStep.signInStep}")
                        )
                    )
                }
            },
            { error ->
                cont.resume(
                    Result.Failure(
                        AuthError.Unknown("confirmSignIn error: ${error.localizedMessage}")
                    )
                )
            }
        )
    }



    override suspend fun getUserRole(): Result<UserRole, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.fetchUserAttributes(
            { attributes ->
                val role = attributes.firstOrNull { it.key == AuthUserAttributeKey.custom("custom:role") }?.value
                val username = Amplify.Auth.getCurrentUser(
                    { user ->
                        var username: String = user.username
                        val userRole = when (role) {
                            "region" -> UserRole.Region(username)
                            "district" -> UserRole.District(username)
                            else -> UserRole.Guest
                        }
                        cont.resume(Result.Success(userRole))
                    },
                    {
                        cont.resume(Result.Failure(AuthError.Unknown("user ${it.localizedMessage}")))
                    }
                )
            },
            {
                cont.resume(Result.Failure(AuthError.Unknown("role ${it.localizedMessage}")))
            }
        )
    }

    override suspend fun getTokens(): Result<String?, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.fetchAuthSession(
            { session ->
                if (session.isSignedIn) {
                    val accessToken = (session as? AWSCognitoAuthSession)?.accessToken
                    if (accessToken != null) {
                        cont.resume(Result.Success(accessToken))
                    } else {
                        cont.resume(Result.Failure(AuthError.Unknown("noAccessToken")))
                    }
                }else{
                    cont.resume(Result.Success(null))
                }
            },
            { error ->
                cont.resume(Result.Failure(AuthError.Unknown(error.localizedMessage ?: "getTokens error")))
            }
        )
    }

    override suspend fun signOut(): Result<Boolean, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.signOut { result ->
            cont.resume(Result.Success(true))
        }
    }

    override suspend fun changePassword(current: String, new: String): Result<Unit, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.updatePassword(
            current,
            new,
            {
                cont.resume(Result.Success(Unit))
            },
            { error ->
                cont.resume(Result.Failure(AuthError.Unknown("changePassword: ${error.localizedMessage}")))
            }
        )
    }

    override suspend fun resetPassword(username: String): Result<Unit, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.resetPassword(
            username,
            {
                cont.resume(Result.Success(Unit))
            },
            { error ->
                cont.resume(Result.Failure(AuthError.Unknown("resetPassword: ${error.localizedMessage}")))
            }
        )
    }

    override suspend fun confirmResetPassword(username: String, newPassword: String, code: String): Result<Unit, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.confirmResetPassword(
            username,
            newPassword,
            code,
            {
                cont.resume(Result.Success(Unit))
            },
            { error ->
                cont.resume(Result.Failure(AuthError.Unknown("confirmResetPassword: ${error.localizedMessage}")))
            }
        )
    }

    override suspend fun updateEmail(newEmail: String): UpdateEmailResult = suspendCancellableCoroutine { cont ->
        Amplify.Auth.updateUserAttribute(
            AuthUserAttribute(AuthUserAttributeKey.email(), newEmail),
            AuthUpdateUserAttributeOptions.defaults(),
            { result ->
                when (result.nextStep.updateAttributeStep) {
                    AuthUpdateAttributeStep.DONE ->
                        cont.resume(UpdateEmailResult.Completed)
                    AuthUpdateAttributeStep.CONFIRM_ATTRIBUTE_WITH_CODE -> {
                        val destination = result.nextStep.codeDeliveryDetails?.destination ?: ""
                        cont.resume(UpdateEmailResult.VerificationRequired(destination))
                    }
                }
            },
            { error ->
                cont.resume(UpdateEmailResult.Failure(AuthError.Unknown("updateEmail: ${error.localizedMessage}")))
            }
        )
    }

    override suspend fun confirmUpdateEmail(code: String): Result<Unit, AuthError> = suspendCancellableCoroutine { cont ->
        Amplify.Auth.confirmUserAttribute(
            AuthUserAttributeKey.email(),
            code,
            {
                cont.resume(Result.Success(Unit))
            },
            { error ->
                cont.resume(Result.Failure(AuthError.Unknown("confirmUpdateEmail: ${error.localizedMessage}")))
            }
        )
    }
}
