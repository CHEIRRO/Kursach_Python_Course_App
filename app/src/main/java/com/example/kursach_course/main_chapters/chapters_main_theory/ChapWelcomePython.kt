package com.example.kursach_course.main_chapters.chapters_main_theory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.AssignmentRepository
import com.example.kursach_course.R
import com.example.kursach_course.SubtopicResponse
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentChapWelcomePythonBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class ChapWelcomePython : Fragment() {

    private lateinit var binding: FragmentChapWelcomePythonBinding
    private lateinit var repository: AssignmentRepository
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChapWelcomePythonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = AssignmentRepository(KtorRetrofitClient.authService)

        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        email = prefs.getString("USER_EMAIL", "") ?: ""

        // topicId и заголовок передаются из предыдущего экрана
        val topicId = arguments?.getInt("topic_id", -1) ?: -1
        val topicTitle = arguments?.getString("topic_title") ?: "Тема"
        binding.profileTitle.text = topicTitle

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        if (topicId == -1) {
            Toast.makeText(requireContext(), "Ошибка: тема не найдена", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        loadSubtopics(topicId)
    }

    private fun loadSubtopics(topicId: Int) {
        lifecycleScope.launch {
            try {
                val subtopics = repository.getSubtopicsByTopic(topicId, email)
                buildSubtopicCards(subtopics, topicId)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки подтем", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildSubtopicCards(subtopics: List<SubtopicResponse>, topicId: Int) {
        binding.subtopicsContainer.removeAllViews()

        val randomMinutes = listOf(4, 6, 8, 9, 10, 12)

        subtopics.forEachIndexed { index, subtopic ->
            val isFirst = index == 0
            val prevCompleted = if (index > 0) subtopics[index - 1].isCompleted else true
            val isUnlocked = isFirst || prevCompleted

            val card = MaterialCardView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (90 * resources.displayMetrics.density).toInt()
                ).apply { setMargins(0, 0, 0, (12 * resources.displayMetrics.density).toInt()) }
                radius = 16 * resources.displayMetrics.density
                cardElevation = 4 * resources.displayMetrics.density
                isClickable = isUnlocked
                isFocusable = isUnlocked

                // Первая карточка тёмно-фиолетовая, остальные светлее
                setCardBackgroundColor(
                    when {
                        subtopic.isCompleted -> requireContext().getColor(R.color.purple_700)
                        index == 0           -> requireContext().getColor(R.color.purple_700)
                        else                 -> requireContext().getColor(R.color.purple_200)
                    }
                )
            }

            val inner = layoutInflater.inflate(R.layout.item_subtopic_card, card, false)
            inner.findViewById<TextView>(R.id.subtopicTitle).text = "${index + 1}. ${subtopic.title}"
            inner.findViewById<TextView>(R.id.subtopicMinutes).text =
                "⏱ ${randomMinutes.random()} мин"

            inner.findViewById<ImageView>(R.id.checkIcon).visibility =
                if (subtopic.isCompleted) View.VISIBLE else View.GONE
            inner.findViewById<ImageView>(R.id.lockIcon).visibility =
                if (!isUnlocked) View.VISIBLE else View.GONE

            card.addView(inner)

            if (isUnlocked) {
                card.setOnClickListener {
                    val bundle = Bundle().apply {
                        putInt("subtopic_id", subtopic.subtopicId)
                        putString("subtopic_title", subtopic.title)
                        putInt("topic_id", topicId)
                    }
                    findNavController().navigate(
                        R.id.action_ChapWelcomePython_to_parIntroduction,
                        bundle
                    )
                }
            }

            binding.subtopicsContainer.addView(card)
        }
    }
}