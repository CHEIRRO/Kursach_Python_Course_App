package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.api.RetrofitClientWriteCode
import com.example.kursach_course.databinding.FragmentSolveAssignmentBinding
import com.example.kursach_course.models.GlotFile
import com.example.kursach_course.models.GlotRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SolveAssignmentFragment : Fragment() {

    private var _binding: FragmentSolveAssignmentBinding? = null
    private val binding get() = _binding!!

    private var assignmentId: Int = -1
    private var correctAnswer: String = ""
    private lateinit var repository: AssignmentRepository
    private lateinit var email: String
    private var isAssignmentLoaded = false

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
        assignmentId = arguments?.getInt("assignment_id", -1) ?: -1
        val title = arguments?.getString("assignment_title") ?: ""
        val content = arguments?.getString("assignment_content") ?: ""
        correctAnswer = arguments?.getString("correct_answer") ?: ""

        Log.d("SolveAssignment", "Arguments: assignmentId=$assignmentId, title=$title, content=$content, correctAnswer=$correctAnswer")

        // Получаем email пользователя
        email = getUserEmail()
        Log.d("SolveAssignment", "User email: $email")

        // Инициализация репозитория
        val api = KtorRetrofitClient.authService
        repository = AssignmentRepository(api)

        // Если передан assignment_id, загружаем задание с сервера
        if (assignmentId != -1) {
            // Блокируем кнопку компиляции до загрузки задания
            binding.executeButton.isEnabled = false
            loadAssignmentFromServer(assignmentId)
            binding.executeButton.isEnabled = false
        } else {
            // Используем переданные данные (для обратной совместимости)
            binding.taskTitle.text = title
            binding.taskDescription.text = content
            this.correctAnswer = correctAnswer.trim()
            isAssignmentLoaded = true
            binding.executeButton.isEnabled = true
            Log.d("SolveAssignment", "Using passed data, correctAnswer: '${this.correctAnswer}'")
        }

        // Кнопка "Назад"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Кнопка компиляции
        binding.executeButton.setOnClickListener {
            if (!isAssignmentLoaded) {
                Toast.makeText(requireContext(), "Задание ещё загружается...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val code = binding.codeInput.text.toString()
            if (code.isBlank()) {
                Toast.makeText(requireContext(), "Введите код", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            executeCode(code)
        }
    }

    private fun loadAssignmentFromServer(assignmentId: Int) {
        lifecycleScope.launch {
            try {
                Log.d("SolveAssignment", "Loading assignment from server, id=$assignmentId")
                val assignment = repository.getAssignmentById(assignmentId)
                withContext(Dispatchers.Main) {
                    binding.taskTitle.text = assignment.title
                    binding.taskDescription.text = assignment.content
                    correctAnswer = assignment.correctAnswer?.trim() ?: ""
                    isAssignmentLoaded = true
                    binding.executeButton.isEnabled = true
                    Log.d("SolveAssignment", "Loaded assignment: title=${assignment.title}, correctAnswer='$correctAnswer'")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("SolveAssignment", "Error loading assignment", e)
                    Toast.makeText(requireContext(), "Ошибка загрузки задания", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun executeCode(code: String) {
        binding.executeButton.isEnabled = false
        binding.resultText.text = "Выполнение..."
        binding.resultVerdict.visibility = View.GONE
        binding.resultText.setBackgroundResource(R.drawable.bg_result)

        lifecycleScope.launch(Dispatchers.IO) {
            // ВРЕМЕННЫЙ ХАРДКОД ДЛЯ ЗАДАНИЯ 2
            if (assignmentId == 2) {
                correctAnswer = "Hello, World!"
                Log.d("SolveAssignment", "HARDCODED correctAnswer = '$correctAnswer'")
            }
            try {
                val result = compileCodeOnGlot(code)
                withContext(Dispatchers.Main) {
                    binding.executeButton.isEnabled = true
                    if (!result.error.isNullOrEmpty()) {
                        binding.resultText.text = result.error
                        updateResultUi(isCorrect = false, expectedOutput = null, output = result.error)
                        reportProgress(false, code)
                    } else {
                        val outputRaw = result.output ?: ""
                        // Нормализация: trim, удаление \r, удаление кавычек
                        val output = outputRaw.trim().replace("\r", "").trim('"')
                        val expected = correctAnswer.trim().replace("\r", "").trim('"')

                        // Логирование для отладки
                        Log.d("SolveAssignment", "=== DEBUG COMPARE ===")
                        Log.d("SolveAssignment", "Output raw: '$outputRaw'")
                        Log.d("SolveAssignment", "Output normalized: '$output'")
                        Log.d("SolveAssignment", "Expected normalized: '$expected'")
                        Log.d("SolveAssignment", "Output length: ${output.length}, Expected length: ${expected.length}")
                        Log.d("SolveAssignment", "Output chars: ${output.map { it.code }}")
                        Log.d("SolveAssignment", "Expected chars: ${expected.map { it.code }}")

                        binding.resultText.text = outputRaw
                        val isCorrect = output.equals(expected, ignoreCase = true)
                        Log.d("SolveAssignment", "Is correct: $isCorrect")
                        Log.d("SolveAssignment", "=====================")

                        updateResultUi(isCorrect, expected, output)
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
                    updateResultUi(isCorrect = false, expectedOutput = null, output = e.message)
                    Toast.makeText(requireContext(), "Ошибка соединения с компилятором", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Обновляет UI результата: цвет фона, текст вердикта.
     */
    private fun updateResultUi(isCorrect: Boolean, expectedOutput: String?, output: String?) {
        val verdictText = if (isCorrect) {
            binding.resultText.setBackgroundResource(R.drawable.bg_result_correct)
            "✅ Правильно!"
        } else {
            binding.resultText.setBackgroundResource(R.drawable.bg_result_incorrect)
            if (!expectedOutput.isNullOrEmpty()) {
                "❌ Неправильно. Ожидалось: \"$expectedOutput\"\nВаш вывод: \"${output ?: ""}\""
            } else {
                "❌ Неправильно"
            }
        }
        binding.resultVerdict.text = verdictText
        binding.resultVerdict.visibility = View.VISIBLE
    }

    private suspend fun compileCodeOnGlot(code: String): CompilationResult {
        return withContext(Dispatchers.IO) {
            try {
                val token = "218d751e-c337-40fa-892f-05b23ab55082"
                val request = GlotRequest(files = listOf(GlotFile("main.py", code)))
                val response = RetrofitClientWriteCode.instance.runPythonCode(token, request).execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    val stdout = body?.stdout
                    val stderr = body?.stderr
                    // Если stderr пустой или null, считаем, что ошибки нет
                    val error = if (stderr.isNullOrEmpty()) null else stderr
                    Log.d("SolveAssignment", "Glot response stdout: $stdout, stderr: $stderr")
                    CompilationResult(output = stdout, error = error)
                } else {
                    CompilationResult(error = "Ошибка HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SolveAssignment", "Glot compilation error", e)
                CompilationResult(error = e.message)
            }
        }
    }

    private suspend fun reportProgress(success: Boolean, code: String?) {
        try {
            repository.updateProgress(email, assignmentId, success, code)
            Log.d("SolveAssignment", "Progress reported: success=$success for assignment $assignmentId")
        } catch (e: Exception) {
            Log.e("SolveAssignment", "Error reporting progress", e)
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
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()  // ← просто закрываем диалог, остаёмся на экране
                        }
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Нет дополнительных заданий", Toast.LENGTH_SHORT).show()
                    // Не закрываем фрагмент, остаёмся на текущем экране
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