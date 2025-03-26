package com.example.kursach_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kursach_course.api.RetrofitClient
import com.example.kursach_course.databinding.FragmentWriteCodeBinding
import com.example.kursach_course.models.GlotFile
import com.example.kursach_course.models.GlotRequest
import com.example.kursach_course.models.GlotResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteCode : Fragment() {

    private lateinit var binding: FragmentWriteCodeBinding

    private val API_TOKEN = "218d751e-c337-40fa-892f-05b23ab55082"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.executeButton.setOnClickListener {
            val code = binding.codeInput.text.toString().trim()
            if (code.isNotEmpty()) {
                executePythonCode(code)
            } else {
                Toast.makeText(context, "Введите код перед выполнением", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun executePythonCode(code: String) {
        val request = GlotRequest(
            files = listOf(
                GlotFile(
                    name = "main.py",
                    content = code
                )
            )
        )

        RetrofitClient.instance.runPythonCode(API_TOKEN, request)
            .enqueue(object : Callback<GlotResponse> {
                override fun onResponse(call: Call<GlotResponse>, response: Response<GlotResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        binding.resultText.text = result?.stdout ?: result?.stderr ?: "Нет данных"
                    } else {
                        binding.resultText.text = "Ошибка выполнения кода. Код: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<GlotResponse>, t: Throwable) {
                    binding.resultText.text = "Ошибка: ${t.message}"
                }
            })
    }
}
