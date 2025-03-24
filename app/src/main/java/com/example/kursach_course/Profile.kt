package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentProfileBinding

class Profile : Fragment() {
private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_mainPrograms)
        }

        // Инициализация SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        // Инициализация переключателя
        val themeSwitch = binding.themeSwitch
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        themeSwitch.isChecked = isDarkTheme

        // Установка начальной темы
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Обработка переключения темы
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sharedPreferences.edit().putBoolean("isDarkTheme", isChecked).apply()
            requireActivity().recreate() // Перезапуск активности для применения темы
        }
    }

}