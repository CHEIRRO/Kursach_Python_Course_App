package com.example.kursach_course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentMainProgramsBinding

class MainPrograms : Fragment() {
    private lateinit var binding: FragmentMainProgramsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainProgramsBinding.inflate(inflater)
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
        binding.ArrayBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_programsArray)
        }
        binding.BasicBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainPrograms_to_writeCode)
        }
    }

}