package com.example.kursach_course

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentRegistrationBinding
import com.example.kursach_course.models.RegisterRequest
import com.example.kursach_course.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        binding.createAcc.setOnClickListener {
            val name = binding.nameField.text.toString().trim()
            val email = binding.emailField.text.toString().trim()
            val pass = binding.passwordField.text.toString()
            val passRepeat = binding.repeatPasswordField.text.toString()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pass != passRepeat) {
                Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(name, email, pass)

            KtorRetrofitClient.authService.register(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()!!
                        requireContext()
                            .getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putString("token", body.token)
                            .apply()

                        Toast.makeText(requireContext(), "Добро пожаловать, ${body.name}!", Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.action_registrationFragment_to_mainPrograms)

                    } else {
                        Toast.makeText(requireContext(),"Ошибка регистрации: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(requireContext(),"Сбой сети: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.alreadyhaveAcc.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
