package com.example.kursach_course

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentNewPasswordCreateBinding
import com.example.kursach_course.models.NewPasswordRequest
import com.example.kursach_course.models.NewPasswordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordCreate : Fragment() {
    private lateinit var binding: FragmentNewPasswordCreateBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val api = KtorRetrofitClient.authService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPasswordCreateBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createAcc.setOnClickListener {
            val password = binding.passwordField.text.toString()
            val repeatPassword = binding.repeatPasswordField.text.toString()
            val email = arguments?.getString("email") ?: ""

            if (!validatePasswords(password, repeatPassword)) return@setOnClickListener

            api.confirmPasswordReset(NewPasswordRequest(email, password))
                .enqueue(object : Callback<NewPasswordResponse> {
                    override fun onResponse(
                        call: Call<NewPasswordResponse>,
                        response: Response<NewPasswordResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { saveUserData(it) }
                            navigateToHome()
                        } else {
                            showError("Ошибка при изменении пароля: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<NewPasswordResponse>, t: Throwable) {
                        showError("Ошибка сети: ${t.message}")
                    }
                })
        }
    }

    private fun validatePasswords(password: String, repeatPassword: String): Boolean {
        return when {
            password.isEmpty() || repeatPassword.isEmpty() -> {
                showError("Заполните все поля")
                false
            }
            password != repeatPassword -> {
                showError("Пароли не совпадают")
                false
            }
            else -> true
        }
    }

    private fun saveUserData(response: NewPasswordResponse) {
        with(sharedPreferences.edit()) {
            putString("user_token", response.token)
            putString("user_name", response.name)
            putString("user_email", response.email)
            apply()
        }
    }

    private fun navigateToHome() {
        Toast.makeText(requireContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_newPasswordCreate_to_mainPrograms)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}