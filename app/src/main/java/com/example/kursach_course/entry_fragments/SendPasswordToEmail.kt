package com.example.kursach_course.entry_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.R
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentChangeEmailBinding
import com.example.kursach_course.models.ResetCodeRequest
import com.example.kursach_course.models.VerifyCodeRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendPasswordToEmail : Fragment() {
    private lateinit var binding: FragmentChangeEmailBinding
    private val api = KtorRetrofitClient.authService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentChangeEmailBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSendEmail.setOnClickListener {
            val email = binding.nameField.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Введите email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            api.requestResetCode(ResetCodeRequest(email)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when {
                        response.isSuccessful -> {
                            showCodeFields()
                            Toast.makeText(requireContext(), "Код отправлен на email", Toast.LENGTH_SHORT).show()
                        }
                        response.code() == 404 -> {
                            Toast.makeText(requireContext(), "Пользователь с таким email не найден", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Ошибка сервера: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.btCodeConfirm.setOnClickListener {
            val email = binding.nameField.text.toString().trim()
            val code = binding.codeField.text.toString().trim()

            if (code.isEmpty()) {
                Toast.makeText(requireContext(), "Введите код", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            api.verifyResetCode(VerifyCodeRequest(email, code)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when {
                        response.isSuccessful -> {
                            val email = binding.nameField.text.toString().trim()
                            val bundle = Bundle().apply {
                                putString("email", email)
                            }
                            findNavController().navigate(R.id.action_changeEmail_to_newPasswordCreate, bundle)

                        }
                        response.code() == 400 -> {
                            Toast.makeText(requireContext(), "Неверный код подтверждения", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Ошибка: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showCodeFields() {
        binding.codeLabel.visibility = View.VISIBLE
        binding.codeField.visibility = View.VISIBLE
        binding.btCodeConfirm.visibility = View.VISIBLE
        binding.btSendEmail.text = "Отправить снова"
    }
}