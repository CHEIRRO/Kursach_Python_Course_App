package com.example.kursach_course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentLoginBinding

class Login : Fragment() {
private lateinit var binding:FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btNext.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_mainPrograms)
        }
        binding.createAccountbt.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registrationFragment)
        }
        binding.changeEmailbt.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_changeEmail)
        }
    }

}