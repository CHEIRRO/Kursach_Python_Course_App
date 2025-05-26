package com.example.kursach_course.main_chapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

}