package com.example.kursach_course.main_chapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.R

import com.example.kursach_course.databinding.FragmentMainTheoryBinding

class MainTheory : Fragment() {
    private lateinit var binding: FragmentMainTheoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainTheoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cheatsbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_mainCheats)
        }
        binding.descriptionbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_mainDescription)
        }
        binding.profileBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_profile)
        }
        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_searchSystemFromPrograms)
        }
        binding.codeBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_writeCode)
        }
        binding.WelcomePBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_ChapWelcomePython)
        }

        val settings = requireContext()
            .getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDark = settings.getBoolean("isDarkTheme", false)

        // фон корневого контейнера
        val bgRoot = if (isDark) R.color.gray_back else R.color.background_light
        binding.fragmentContainerView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), bgRoot)
        )

        // иконки и заголовок
        val iconTint = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        binding.profileBt.setColorFilter(iconTint)
        binding.codeBt.setColorFilter(iconTint)
        binding.titleText.setTextColor(iconTint)

        // searchButton: иконка и текст
        val searchIcon = binding.searchButton.compoundDrawablesRelative[0]
        searchIcon?.setTint(iconTint)
        binding.searchButton.setCompoundDrawablesRelative(searchIcon, null, null, null)
        binding.searchButton.setTextColor(iconTint)

        // фоны двух колонок и фрейма
        val layoutBg = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.gray_back else R.color.background_light
        )
        binding.linearLayout2.setBackgroundColor(layoutBg)
        binding.linearLayout3.setBackgroundColor(layoutBg)
        binding.frameLayout.setBackgroundColor(layoutBg)

        // кнопки в колонках — скруглённые drawables
        val btnTextColor = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        val btnBgDrawable = if (isDark)
            R.drawable.button_background_dark
        else
            R.drawable.button_background

        listOf(
            binding.WelcomePBt, binding.SchoolMathBt, binding.StringBt,
            binding.ListBt, binding.DataBt, binding.NumbersBt,
            binding.LoopBt, binding.FunctionsBt
        ).forEach { btn ->
            btn.setBackgroundResource(btnBgDrawable)
            btn.setTextColor(btnTextColor)
        }
    }
}