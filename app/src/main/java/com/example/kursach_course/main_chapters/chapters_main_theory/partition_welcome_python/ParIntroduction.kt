package com.example.kursach_course.main_chapters.chapters_main_theory.partition_welcome_python

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.AssignmentRepository
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentParIntroductionBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class ParIntroduction : Fragment() {

    private var _binding: FragmentParIntroductionBinding? = null
    private val binding get() = _binding!!

    private var startY = 0f
    private val SWIPE_THRESHOLD = 100

    private var correctAnswer: String = ""
    private lateinit var repository: AssignmentRepository
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = AssignmentRepository(KtorRetrofitClient.authService)

        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        email = prefs.getString("USER_EMAIL", "") ?: ""

        val subtopicId = arguments?.getInt("subtopic_id", -1) ?: -1
        val subtopicTitle = arguments?.getString("subtopic_title") ?: ""

        binding.headerTitle.text = subtopicTitle

        // Показываем теорию по subtopicId
        binding.theoryText.text = getTheoryText(subtopicId)

        // Загружаем тест с сервера
        if (subtopicId != -1) {
            loadAssignment(subtopicId)
        }

        // Свайп вверх → показать тест
        binding.bottomDetector.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startY = event.rawY
                MotionEvent.ACTION_UP -> {
                    if (startY - event.rawY > SWIPE_THRESHOLD) showTest()
                }
            }
            true
        }

        binding.btnSubmit.setOnClickListener {
            checkAnswer(subtopicId)
        }

        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadAssignment(subtopicId: Int) {
        lifecycleScope.launch {
            try {
                val assignment = repository.getAssignmentBySubtopic(subtopicId)
                if (assignment != null) {
                    correctAnswer = assignment.correctAnswer ?: ""
                    parseAndShowQuestion(assignment.content)
                    Log.d("ParIntroduction", "Loaded: correctAnswer='$correctAnswer'")
                } else {
                    Log.e("ParIntroduction", "Assignment is null for subtopicId=$subtopicId")
                }
            } catch (e: Exception) {
                Log.e("ParIntroduction", "Error loading assignment for subtopicId=$subtopicId", e)
            }
        }
    }

    private fun parseAndShowQuestion(content: String) {
        try {
            val json = JSONObject(content)
            val question = json.getString("question")
            val options = json.getJSONArray("options")

            binding.questionText.text = question
            binding.rgOptions.removeAllViews()

            for (i in 0 until options.length()) {
                val rb = RadioButton(requireContext()).apply {
                    text = options.getString(i)
                    textSize = 16f
                    setTextColor(requireContext().getColor(android.R.color.black))
                }
                binding.rgOptions.addView(rb)
            }
        } catch (e: Exception) {
            Log.e("ParIntroduction", "JSON parse error: ${e.message}, content='$content'")
        }
    }

    private fun checkAnswer(subtopicId: Int) {
        val checkedId = binding.rgOptions.checkedRadioButtonId
        if (checkedId == -1) {
            Toast.makeText(requireContext(), "Выберите ответ", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRb = binding.rgOptions.findViewById<RadioButton>(checkedId)
        val selectedText = selectedRb.text.toString()

        if (selectedText.equals(correctAnswer.trim(), ignoreCase = true)) {
            // Отмечаем подтему как пройденную
            lifecycleScope.launch {
                try {
                    repository.completeSubtopic(email, subtopicId)
                } catch (e: Exception) { }
            }
            Toast.makeText(requireContext(), "✅ Правильно!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(
                requireContext(),
                "❌ Неверно. Правильный ответ: $correctAnswer",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showTest() {
        if (correctAnswer.isEmpty()) {
            Toast.makeText(requireContext(), "Тест ещё загружается...", Toast.LENGTH_SHORT).show()
            return
        }
        binding.scrollView.visibility = View.GONE
        binding.testContainer.visibility = View.VISIBLE
    }

    // Хардкод теории по subtopicId
    private fun getTheoryText(subtopicId: Int): String = when (subtopicId) {
        1 -> """
            Python был создан Гвидо ван Россумом в 1991 году. Это один из самых популярных языков программирования в мире.
            
            Переменная — это именованная область памяти, в которой хранится значение. В Python не нужно объявлять тип переменной заранее.
            
            Примеры:
            name = "Иван"
            age = 25
            pi = 3.14
            
            Python автоматически определяет тип по значению которое вы присваиваете.
        """.trimIndent()

        2 -> """
            В Python есть несколько основных типов данных:
            
            int — целые числа: 1, 42, -7
            float — числа с плавающей точкой: 3.14, -0.5
            str — строки текста: "Привет", "Python"
            bool — логические значения: True, False
            
            Строки создаются в кавычках — одинарных или двойных:
            s1 = 'Привет'
            s2 = "Мир"
            
            Числа можно складывать, вычитать, умножать и делить стандартными операторами.
        """.trimIndent()

        3 -> """
            Условный оператор if позволяет выполнять код только при определённом условии.
            
            Синтаксис:
            if условие:
                # код если True
            else:
                # код если False
            
            Пример:
            x = 10
            if x > 5:
                print("Больше пяти")
            else:
                print("Пять или меньше")
            
            Отступы в Python обязательны — они определяют блок кода.
        """.trimIndent()

        4 -> """
            Цикл for используется для перебора элементов последовательности.
            
            Синтаксис:
            for переменная in последовательность:
                # код
            
            Функция range() генерирует последовательность чисел:
            range(5) → 0, 1, 2, 3, 4
            range(1, 6) → 1, 2, 3, 4, 5
            
            Пример:
            for i in range(3):
                print(i)
            
            Выведет: 0, 1, 2
        """.trimIndent()

        else -> "Теория для этой подтемы скоро появится."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}