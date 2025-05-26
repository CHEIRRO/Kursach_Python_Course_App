package com.example.kursach_course.main_chapters.chapters_main_theory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kursach_course.databinding.FragmentChapWelcomePythonBinding


class ChapWelcomePython : Fragment() {
    private lateinit var binding: FragmentChapWelcomePythonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChapWelcomePythonBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //binding..setOnClickListener {
        //findNavController().navigate(R.id.action_mainCheats_to_mainPrograms)
        //}

    }
}