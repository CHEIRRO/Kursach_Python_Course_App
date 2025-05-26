// File: ParIntroduction.kt
package com.example.kursach_course.main_chapters.chapters_main_theory.partition_welcome_python

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.R
import com.example.kursach_course.databinding.FragmentParIntroductionBinding

class ParIntroduction : Fragment() {
    private var _binding: FragmentParIntroductionBinding? = null
    private val binding get() = _binding!!

    private var startY = 0f
    private val SWIPE_THRESHOLD = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Слежение за касанием для простого свайпа вверх
        binding.bottomDetector.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                    val endY = event.rawY
                    if (startY - endY > SWIPE_THRESHOLD) {
                        showTest()
                    }
                }
            }
            true
        }

        // Проверка теста
        binding.btnSubmit.setOnClickListener {
            val checkedId = binding.rgQuestion1.checkedRadioButtonId
            if (checkedId == R.id.option1_2) {
                val sp = requireContext()
                    .getSharedPreferences("AppData", Context.MODE_PRIVATE)
                val email = sp.getString("CURRENT_USER_EMAIL", "")!!
                // Ключ с учётом аккаунта:
                sp.edit()
                    .putBoolean("USER_${email}_SECTION1_DONE", true)
                    .apply()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(),
                    "Неверно. Правильный ответ — Гвидо ван Россум.",
                    Toast.LENGTH_SHORT).show()
            }
        }




        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_parIntroduction_to_ChapWelcomePython)
        }
    }

    private fun showTest() {
        binding.scrollView.visibility = View.GONE
        binding.testContainer.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
