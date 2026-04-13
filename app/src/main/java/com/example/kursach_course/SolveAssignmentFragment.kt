package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.api.RetrofitClientWriteCode
import com.example.kursach_course.databinding.FragmentSolveAssignmentBinding
import com.example.kursach_course.models.GlotFile
import com.example.kursach_course.models.GlotRequest
import com.example.kursach_course.models.GlotResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SolveAssignmentFragment : Fragment() {

    private var _binding: FragmentSolveAssignmentBinding? = null
    private val binding get() = _binding!!

    // Используем var без lateinit (с дефолтными значениями)
    private var assignmentId: Int = -1
    private var correctAnswer: String = ""
    private lateinit var repository: AssignmentRepository
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSolveAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем аргументы
        assignmentId = arguments?.getInt("assignment_id") ?: -1
        val title = arguments?.getString("assignment_title") ?: "Задание"
        val content = arguments?.getString("assignment_content") ?: ""
        correctAnswer = arguments?.getString("correct_answer") ?: ""

        binding.taskTitle.text = title
        binding.taskDescription.text = content

        // Получаем email пользователя
        email = getUserEmail()

        // Инициализация репозитория
        val api = KtorRetrofitClient.authService
        repository = AssignmentRepository(api)

        // Кнопка "Назад"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Кнопка компиляции
        binding.executeButton.setOnClickListener {
            val code = binding.codeInput.text.toString()
            if (code.isBlank()) {
                Toast.makeText(requireContext(), "Введите код", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            executeCode(code)
        }
    }

    private fun executeCode(code: String) {
        binding.executeButton.isEnabled = false
        binding.resultText.text = "Выполнение..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = compileCodeOnGlot(code)
                withContext(Dispatchers.Main) {
                    binding.executeButton.isEnabled = true
                    if (result.error != null) {
                        binding.resultText.text = result.error
                        reportProgress(false, code)
                    } else {
                        val output = result.output?.trim() ?: ""
                        binding.resultText.text = output
                        val isCorrect = output.equals(correctAnswer.trim(), ignoreCase = true)
                        if (isCorrect) {
                            reportProgress(true, code)
                            showExtraAssignmentDialog()
                        } else {
                            reportProgress(false, code)
                            Toast.makeText(requireContext(), "Неверный вывод. Попробуйте ещё раз.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.executeButton.isEnabled = true
                    binding.resultText.text = "Ошибка: ${e.message}"
                    Toast.makeText(requireContext(), "Ошибка соединения с компилятором", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Используем существующий RetrofitClientWriteCode
    private suspend fun compileCodeOnGlot(code: String): CompilationResult {
        return withContext(Dispatchers.IO) {
            try {
                val token = "218d751e-c337-40fa-892f-05b23ab55082"  // ваш токен
                val request = GlotRequest(files = listOf(GlotFile("main.py", code)))
                val call = RetrofitClientWriteCode.instance.runPythonCode(token, request)
                val response = call.execute()  // синхронный вызов, но в IO потоке
                if (response.isSuccessful) {
                    val body = response.body()
                    CompilationResult(output = body?.stdout, error = body?.stderr)
                } else {
                    CompilationResult(error = "Ошибка HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                CompilationResult(error = e.message)
            }
        }
    }

    private suspend fun reportProgress(success: Boolean, code: String?) {
        try {
            repository.updateProgress(email, assignmentId, success, code)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showExtraAssignmentDialog() {
        lifecycleScope.launch {
            val extra = repository.getExtraAssignment(email, assignmentId)
            withContext(Dispatchers.Main) {
                if (extra != null) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Отлично!")
                        .setMessage("Задание выполнено. Хотите получить дополнительное упражнение по этой теме?")
                        .setPositiveButton("Да") { _, _ ->
                            val bundle = Bundle().apply {
                                putInt("assignment_id", extra.assignmentId)
                                putString("assignment_title", extra.title)
                                putString("assignment_content", extra.content)
                                putString("correct_answer", extra.correctAnswer)
                            }
                            findNavController().navigate(R.id.action_to_extraAssignment, bundle)
                        }
                        .setNegativeButton("Нет", null)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Нет дополнительных заданий", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun getUserEmail(): String {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getString("USER_EMAIL", "") ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class CompilationResult(val output: String? = null, val error: String? = null)
}