package com.example.kursach_course.applications

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager
import com.example.kursach_course.AssignmentRepository
import com.example.kursach_course.R
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var repository: AssignmentRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repository = AssignmentRepository(KtorRetrofitClient.authService)

        val userPrefs = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val email = userPrefs.getString("USER_EMAIL", "") ?: ""

        binding.profileName.text = userPrefs.getString("USER_NAME", "")
        binding.profileEmail.text = email

        // Загрузка рейтинга
        loadRating(email)

        val settings = requireContext()
            .getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        val enabled = settings.getBoolean("notif_enabled", true)
        binding.notifSwitch.isChecked = enabled
        binding.notifSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.edit().putBoolean("notif_enabled", isChecked).apply()
            if (!isChecked) {
                WorkManager.getInstance(requireContext()).cancelUniqueWork("reminder_work")
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_mainPrograms)
        }
        binding.logoutButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_welcomFragment)
        }
        binding.problematicButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_problematicTasksFragment)
        }

        val isDark = settings.getBoolean("isDarkTheme", true)
        binding.themeSwitch.isChecked = isDark
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settings.edit().putBoolean("isDarkTheme", isChecked).apply()
            requireActivity().setTheme(
                if (isChecked) R.style.Theme_AppDark else R.style.Theme_Kursach_course
            )
            requireActivity().recreate()
        }

        val bgRoot = if (isDark) R.color.gray_back else R.color.background_light
        binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), bgRoot))
        val bgCard = if (isDark) R.color.button_back else R.color.white
        binding.profileCard2.setBackgroundColor(ContextCompat.getColor(requireContext(), bgCard))
    }

    private fun loadRating(email: String) {
        lifecycleScope.launch {
            try {
                val rating = repository.getUserRating(email)
                binding.ratingValue.text = rating.toString()

                // Цвет круга зависит от балла
                val colorRes = when {
                    rating >= 8 -> R.color.green_rating
                    rating >= 5 -> R.color.amber_rating
                    else        -> R.color.red_rating
                }
                binding.ratingValue.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), colorRes)

            } catch (e: Exception) {
                binding.ratingValue.text = "—"
            }
        }
    }
}