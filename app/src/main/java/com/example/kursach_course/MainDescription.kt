package com.example.kursach_course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
    }

}