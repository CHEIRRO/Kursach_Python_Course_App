import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(val token: String)

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class LoginResponse(val token: String)