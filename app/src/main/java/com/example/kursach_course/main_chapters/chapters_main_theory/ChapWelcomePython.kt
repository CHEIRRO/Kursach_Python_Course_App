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
        // 1) Навигация назад
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_ChapWelcomePython_to_mainPrograms)
        }

        // 2) Получаем текущего пользователя
        val appData = requireContext()
            .getSharedPreferences("AppData", Context.MODE_PRIVATE)
        val email = appData.getString("CURRENT_USER_EMAIL", "")!!

        // 3) Чтение флага с учётом аккаунта:
        val done1 = appData.getBoolean("USER_${email}_SECTION1_DONE", false)

        // 4) Обновляем UI
        if (done1) {
            // показываем галочку
            binding.card1.findViewById<ImageView>(R.id.checkIcon1)
                .visibility = View.VISIBLE
            // делаем вторую карточку доступной
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

        // 5) Навешиваем клики
        binding.card1.setOnClickListener {
            findNavController().navigate(R.id.action_ChapWelcomePython_to_parIntroduction)
        }
        binding.card2.setOnClickListener {
            //findNavController().navigate(R.id.action_ChapWelcomePython_to_parSecond)
        }
    }

}
