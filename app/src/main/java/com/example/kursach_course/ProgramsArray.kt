package com.example.kursach_course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentProgramsArrayBinding

class ProgramsArray : Fragment() {
private lateinit var binding:FragmentProgramsArrayBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProgramsArrayBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_programsArray_to_mainPrograms)
        }
        binding.ArrayExample.setOnClickListener {
            findNavController().navigate(R.id.action_programsArray_to_arrayExample)
        }
    }

}