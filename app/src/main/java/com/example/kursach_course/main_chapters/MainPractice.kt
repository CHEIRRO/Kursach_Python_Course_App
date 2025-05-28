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
import com.example.kursach_course.databinding.FragmentMainPracticeBinding

class MainPractice : Fragment() {
    private lateinit var binding: FragmentMainPracticeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPracticeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val settings = requireContext()
            .getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
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

        val btnTextColor = ContextCompat.getColor(
            requireContext(),
            if (isDark) R.color.white else R.color.black
        )
        val btnBg = if (isDark)
            R.drawable.button_background_dark
        else
            R.drawable.button_background

        listOf(
            binding.myButton6, binding.myButton7,
            binding.myButton12, binding.myButton8, binding.myButton9,
            binding.myButton10, binding.myButton11
        ).forEach { btn ->
            btn.setBackgroundResource(btnBg)
            btn.setTextColor(btnTextColor)
        }
    }
}
