package com.example.kursach_course.main_chapters.chapters_main_theory

import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.R
import com.example.kursach_course.databinding.FragmentChapWelcomePythonBinding

class ChapWelcomePython : Fragment() {
    private lateinit var binding: FragmentChapWelcomePythonBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentChapWelcomePythonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_ChapWelcomePython_to_mainPrograms)
        }

        val appData = requireContext()
            .getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val email = appData.getString("CURRENT_USER_EMAIL", "")!!

        val done1 = appData.getBoolean("USER_${email}_SECTION1_DONE", false)

        if (done1) {
            binding.card1.findViewById<ImageView>(R.id.checkIcon1)
                .visibility = View.VISIBLE
            binding.card2.apply {
                isClickable = true
                isFocusable = true
                setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.purple_700)
                )
            }
        } else {
            binding.card2.isClickable = false
        }

        binding.card1.setOnClickListener {
            findNavController().navigate(R.id.action_ChapWelcomePython_to_parIntroduction)
        }
        binding.card2.setOnClickListener {
            //findNavController().navigate(R.id.action_ChapWelcomePython_to_parSecond)
        }
    }

}
