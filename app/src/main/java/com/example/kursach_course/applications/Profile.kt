package com.example.kursach_course.applications

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager
import com.example.kursach_course.R
import com.example.kursach_course.databinding.FragmentProfileBinding

class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // User info
        val userPrefs = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        binding.profileName.text = userPrefs.getString("USER_NAME", "")
        binding.profileEmail.text = userPrefs.getString("USER_EMAIL", "")

        // Notification switch
        val settings = requireContext()
            .getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val enabled = settings.getBoolean("notif_enabled", true)
        binding.notifSwitch.isChecked = enabled
        binding.notifSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.edit().putBoolean("notif_enabled", isChecked).apply()
            if (!isChecked) {
                WorkManager.getInstance(requireContext())
                    .cancelUniqueWork("reminder_work")
            }
        }

        // Back and logout
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_mainPrograms)
        }
        binding.logoutButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_welcomFragment)
        }

        // Theme switch (existing logic)
        val isDark = settings.getBoolean("isDarkTheme", true)
        binding.themeSwitch.isChecked = isDark
        binding.profileCard.setCardBackgroundColor(
            ContextCompat.getColorStateList(
                requireContext(), if (isDark) R.color.gray else R.color.white
            )
        )
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.edit().putBoolean("isDarkTheme", isChecked).apply()
            requireActivity().setTheme(
                if (isChecked) R.style.Theme_AppDark else R.style.Theme_Kursach_course
            )
            requireActivity().recreate()
        }
    }
}
