package com.example.kursach_course.main_chapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kursach_course.R
import com.example.kursach_course.Topic
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentMainTheoryBinding
import kotlinx.coroutines.launch

class MainTheory : Fragment() {

    private lateinit var binding: FragmentMainTheoryBinding
    private lateinit var roadmapAdapter: RoadmapAdapter
    private var topicsList: List<Topic> = emptyList()

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
                val topics = KtorRetrofitClient.authService.getTheoryTopicsSuspend()
                topicsList = topics
                val completedIds = getCompletedTopics() // пока пустой, потом реализуешь
                setupRecyclerView(topics, completedIds)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки тем: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(topics: List<Topic>, completedIds: Set<Int>) {
        roadmapAdapter = RoadmapAdapter(topics, completedIds) { topic ->
            openTopicAssignments(topic)
        }
        binding.rvRoadmap.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = roadmapAdapter
        }
    }

    private fun getCompletedTopics(): Set<Int> {
        // Пока возвращаем пустой набор. Позже можно загружать с сервера или из SharedPreferences
        return emptySet()
    }

    private fun openTopicAssignments(topic: Topic) {
        val bundle = Bundle().apply {
            putInt("topic_id", topic.topicId)        // было topicId
            putString("topic_title", topic.title)     // было topicTitle
        }
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

        // Фон для RecyclerView (можно установить его родителю)
        binding.rvRoadmap.setBackgroundColor(
            ContextCompat.getColor(requireContext(),
                if (isDark) R.color.gray_back else R.color.background_light)
        )
        binding.frameLayout.setBackgroundColor(
            ContextCompat.getColor(requireContext(),
                if (isDark) R.color.gray_back else R.color.background_light)
        )

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