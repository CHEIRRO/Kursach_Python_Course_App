package com.example.kursach_course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentMainCheatsBinding

class MainCheats : Fragment() {
    private lateinit var binding: FragmentMainCheatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCheatsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.programbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainCheats_to_mainPrograms)
        }
        binding.descriptionbt.setOnClickListener {
            findNavController().navigate(R.id.action_mainCheats_to_mainDescription)
        }
        binding.profileBt.setOnClickListener {
            findNavController().navigate(R.id.action_mainCheats_to_profile)
        }
    }
}