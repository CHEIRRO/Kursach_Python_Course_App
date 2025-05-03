package com.example.kursach_course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Модель данных для отправки на сервер
data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String
)

// Модель данных ответа от сервера
data class RegisterResponse(
    val token: String
)

// Интерфейс для API регистрации
interface AuthApi {
    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}

class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // Замените на адрес вашего Ktor сервера
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btNext.setOnClickListener {
            val name = binding.emailField.text.toString()
            val email = binding.passwordField.text.toString()
            val password = binding.codeField1.text.toString()
            val repeatPassword = binding.codeField2.text.toString()

            if (password != repeatPassword) {
                Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }

        binding.myButton.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_login)
        }
    }

    private fun registerUser(login: String, email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.register(RegisterRequest(login, email, password))
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Регистрация успешна! Токен: ${response.token}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registrationFragment_to_login)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка регистрации: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}