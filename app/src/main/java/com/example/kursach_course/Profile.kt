package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentProfileBinding

class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_mainPrograms)
        }
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", true)
        binding.themeSwitch.isChecked = isDarkTheme
        binding.profileCard.setCardBackgroundColor(
            ContextCompat.getColorStateList(
                requireContext(),
                if (isDarkTheme) R.color.gray else R.color.white
            )
        )
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isDarkTheme", isChecked).apply()

            if (isChecked) {
                requireActivity().setTheme(R.style.Theme_AppDark)
            } else {
                requireActivity().setTheme(R.style.Theme_Kursach_course)
            }
            requireActivity().recreate()
        }
    }
}