import com.studiomk.matool.domain.contracts.auth.AuthError
import com.studiomk.matool.domain.entities.shared.UserRole

sealed class SignInResult {
    data class Success(val userRole:UserRole): SignInResult()
    object NewPasswordRequired : SignInResult()
    data class Failure(val error: AuthError) : SignInResult()
}

