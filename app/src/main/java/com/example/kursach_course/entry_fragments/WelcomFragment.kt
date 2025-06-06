package com.example.kursach_course.entry_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.R
import com.example.kursach_course.databinding.FragmentWelcomBinding

class WelcomFragment : Fragment() {
    private lateinit var binding: FragmentWelcomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.welBtNext.setOnClickListener {
            findNavController().navigate(R.id.action_welcomFragment_to_login)
        }
    }
}