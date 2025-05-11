package com.example.kursach_course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentChangeEmailBinding

class ChangeEmail : Fragment() {
    private lateinit var binding: FragmentChangeEmailBinding
    private var isCodeSent = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeEmailBinding.inflate(inflater, container, false)

        binding.btSendEmail.setOnClickListener {
            if (!isCodeSent) {

                val email = binding.emailField.text.toString().trim()

                if (email.isNotEmpty()) {
                    binding.codeLabel.visibility = View.VISIBLE
                    binding.codeField.visibility = View.VISIBLE
                    binding.btCodeConfirm.visibility = View.VISIBLE

                    binding.btSendEmail.text = "Отправить код повторно"
                    isCodeSent = true

                    Toast.makeText(requireContext(), "Код отправлен на $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Введите почту", Toast.LENGTH_SHORT).show()
                }
            } else {
                val email = binding.emailField.text.toString().trim()
                if (email.isNotEmpty()) {

                    Toast.makeText(requireContext(), "Код отправлен повторно", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btCodeConfirm.setOnClickListener {
            val code = binding.codeField.text.toString().trim()

            if (code.isNotEmpty()) {
                // Логика подтверждения кода (например, отправка на сервер)
                Toast.makeText(requireContext(), "Код подтвержден: $code", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Введите код", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btCodeConfirm.setOnClickListener {
            findNavController().navigate(R.id.action_changeEmail_to_newPasswordCreate)
        }
        return binding.root
    }
}
