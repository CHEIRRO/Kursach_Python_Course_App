package com.example.kursach_course.main_chapters

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.R
import com.example.kursach_course.Topic
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentMainTheoryBinding
import kotlinx.coroutines.launch

class MainTheory : Fragment() {

    private lateinit var binding: FragmentMainTheoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainTheoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        applyTheme()
        loadTheoryTopics()
    }

    private fun setupClickListeners() {
        binding.profileBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_profile)
        }
        binding.codeBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_writeCode)
        }
        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_searchSystemFromPrograms)
        }
        binding.cheatsbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_mainCheats)
        }
        binding.descriptionbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_mainDescription)
        }
    }

    private fun loadTheoryTopics() {
        lifecycleScope.launch {
            try {
                // Используем suspend-функцию (добавьте её в KtorApiService, если ещё нет)
                val topics = KtorRetrofitClient.authService.getTheoryTopicsSuspend()
                createTopicButtons(topics)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки тем: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createTopicButtons(topics: List<Topic>) {
        binding.linearLayoutTopics.removeAllViews()

        val settings = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDark = settings.getBoolean("isDarkTheme", false)
        val btnTextColor = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        val btnBg = if (isDark) R.drawable.button_background_dark else R.drawable.button_background

        for (topic in topics) {
            val button = AppCompatButton(requireContext()).apply {
                text = topic.title
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
                setBackgroundResource(btnBg)
                setTextColor(btnTextColor)
                // Иконка стрелки справа
                setCompoundDrawablesWithIntrinsicBounds(
                    null, null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right),
                    null
                )
                compoundDrawablePadding = 24
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                minHeight = 72
                setPadding(32, 0, 32, 0)
                id = View.generateViewId()
                setOnClickListener {
                    openTopicAssignments(topic)
                }
            }
            binding.linearLayoutTopics.addView(button)
        }
    }

    private fun openTopicAssignments(topic: Topic) {
        val bundle = Bundle().apply {
            putInt("topicId", topic.topicId)
            putString("topicTitle", topic.title)
        }
        // Убедитесь, что в nav_graph.xml есть это действие
        findNavController().navigate(R.id.action_mainPrograms_to_ChapWelcomePython, bundle)
    }

    private fun applyTheme() {
        val settings = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDark = settings.getBoolean("isDarkTheme", false)

        val bgRoot = if (isDark) R.color.gray_back else R.color.background_light
        binding.fragmentContainerView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), bgRoot)
        )

        val iconTint = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        binding.profileBt.setColorFilter(iconTint)
        binding.codeBt.setColorFilter(iconTint)
        binding.titleText.setTextColor(iconTint)

        val searchIcon = binding.searchButton.compoundDrawablesRelative[0]
        searchIcon?.setTint(iconTint)
        binding.searchButton.setCompoundDrawablesRelative(searchIcon, null, null, null)
        binding.searchButton.setTextColor(iconTint)

        val layoutBg = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.gray_back else R.color.background_light
        )
        binding.linearLayoutTopics.setBackgroundColor(layoutBg)
        binding.frameLayout.setBackgroundColor(layoutBg)

        val btnTextColor = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        binding.programbt.setTextColor(btnTextColor)
        binding.cheatsbt.setTextColor(btnTextColor)
        binding.descriptionbt.setTextColor(btnTextColor)

        if (isDark) {
            binding.programbt.setBackgroundResource(R.drawable.button_border_black)
            binding.cheatsbt.setBackgroundResource(R.drawable.button_border_white)
            binding.descriptionbt.setBackgroundResource(R.drawable.button_border_white)
        } else {
            binding.programbt.setBackgroundResource(R.drawable.button_border_black)
            binding.cheatsbt.setBackgroundResource(R.drawable.button_border_white)
            binding.descriptionbt.setBackgroundResource(R.drawable.button_border_white)
        }
    }
}