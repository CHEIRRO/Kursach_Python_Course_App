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
import com.example.kursach_course.databinding.FragmentMainDescriptionBinding

class MainDescription : Fragment() {
    private lateinit var binding:FragmentMainDescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainDescriptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.programbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainDescription_to_mainPrograms)
        }
        binding.cheatsbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainDescription_to_mainCheats)
        }
        binding.profileBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainDescription_to_profile)
        }
        binding.codeBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainDescription_to_writeCodeFragment)
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
        binding.scrollView.setBackgroundColor(columnBg)


    }
}