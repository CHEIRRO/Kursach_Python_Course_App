package com.example.kursach_course.main_chapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.R
import com.example.kursach_course.databinding.FragmentMainPracticeBinding
import com.example.kursach_course.AssignmentRepository
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.TopicWithPracticeResponse
import kotlinx.coroutines.launch
import androidx.appcompat.widget.AppCompatButton
import android.view.Gravity

class MainPractice : Fragment() {
    private lateinit var binding: FragmentMainPracticeBinding
    private lateinit var repository: AssignmentRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Навигация на другие экраны (оставляем как есть)
        binding.programbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainCheats_to_mainPrograms)
        }
        binding.descriptionbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainCheats_to_mainDescription)
        }
        binding.profileBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainCheats_to_profile)
        }
        binding.codeBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainCheats_to_writeCodeFragment)
        }

        // Инициализация репозитория
        val api = KtorRetrofitClient.authService
        repository = AssignmentRepository(api)

        // Загружаем темы с практическими заданиями
        lifecycleScope.launch {
            try {
                val topics = repository.getTopicsWithPractice()
                createTopicButtons(topics)
            } catch (e: Exception) {
                // Обработка ошибки – можно показать Toast
                e.printStackTrace()
            }
        }

        // ----- Применение темы (светлая/тёмная) – как было, но теперь кнопки создаются динамически,
        // поэтому нужно будет применить стиль после создания. Сделаем это в createTopicButtons.
        applyTheme()
    }

    private fun createTopicButtons(topics: List<TopicWithPracticeResponse>) {
        binding.linearLayout2.removeAllViews()

        val settings = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDark = settings.getBoolean("isDarkTheme", false)
        val btnTextColor = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        val btnBg = if (isDark) R.drawable.button_background_dark else R.drawable.button_background

        for (topic in topics) {
            val button = AppCompatButton(requireContext()).apply {
                text = topic.topicTitle
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
                setBackgroundResource(btnBg)
                setTextColor(btnTextColor)
                // Иконка стрелки справа
                setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right), null)
                compoundDrawablePadding = 24
                // Гравитация: текст слева, иконка справа, всё по вертикали по центру
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                // Минимальная высота для удобства нажатия
                minHeight = 72
                // Отступы внутри кнопки
                setPadding(32, 0, 32, 0)
                id = View.generateViewId()
                setOnClickListener {
                    navigateToPracticeTask(topic.practiceAssignmentId, topic.topicTitle)
                }
            }
            binding.linearLayout2.addView(button)
        }
    }

    private fun applyTheme() {
        val settings = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDark = settings.getBoolean("isDarkTheme", false)

        val bgRoot = if (isDark) R.color.gray_back else R.color.background_light
        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), bgRoot))

        val iconTint = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        binding.profileBt.setColorFilter(iconTint)
        binding.codeBt.setColorFilter(iconTint)
        binding.titleText.setTextColor(iconTint)

        val columnBg = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.gray_back else R.color.background_light
        )
        binding.linearLayout2.setBackgroundColor(columnBg)
    }

    private fun navigateToPracticeTask(assignmentId: Int, title: String) {
        val bundle = Bundle().apply {
            putInt("assignment_id", assignmentId)
            putString("assignment_title", title)
        }
        findNavController().navigate(R.id.action_mainCheats_to_solveAssignmentFragment, bundle)
    }
}