package com.example.kursach_course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentLoginBinding
import com.example.kursach_course.models.LoginRequest
import com.example.kursach_course.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.loginInAcc.setOnClickListener {
            val email = binding.emailField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            KtorRetrofitClient.authService.login(request).enqueue(object : Callback<LoginResponse> {

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        Toast.makeText(requireContext(),"Добро пожаловать, ${loginResponse?.name}", Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.action_login_to_mainPrograms)

                    } else { Toast.makeText(requireContext(),"Неверный email или пароль", Toast.LENGTH_SHORT).show() }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(requireContext(),"Ошибка подключения: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.createAccountbt.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registrationFragment)
        }

        binding.changePassbt.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_changeEmail)
        }
    }
}
