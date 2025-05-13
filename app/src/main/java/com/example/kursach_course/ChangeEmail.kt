package com.example.kursach_course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentChangeEmailBinding
import com.example.kursach_course.models.ResetCodeRequest
import com.example.kursach_course.models.SimpleResponse
import com.example.kursach_course.models.VerifyCodeRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeEmail : Fragment() {
    private lateinit var binding: FragmentChangeEmailBinding
    private val api = KtorRetrofitClient.authService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        FragmentChangeEmailBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Отправка кода
        binding.btSendEmail.setOnClickListener {
            val email = binding.nameField.text.toString()
            api.requestResetCode(ResetCodeRequest(email)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        showCodeFields()
                        Toast.makeText(requireContext(), "Код отправлен", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Ошибка отправки", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка сети: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }


        // Подтверждение кода
        binding.btCodeConfirm.setOnClickListener {
            val email = binding.nameField.text.toString()
            val code = binding.codeField.text.toString()

            api.verifyResetCode(VerifyCodeRequest(email, code)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        findNavController().navigate(R.id.action_changeEmail_to_newPasswordCreate)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Неверный код: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Ошибка сети: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
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
