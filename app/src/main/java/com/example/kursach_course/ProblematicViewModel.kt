package com.example.kursach_course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// viewmodel/ProblematicViewModel.kt
class ProblematicViewModel(private val repository: AssignmentRepository) : ViewModel() {

    private val _problematicTasks = MutableLiveData<List<ProblematicResponse>>()
    val problematicTasks: LiveData<List<ProblematicResponse>> = _problematicTasks

    fun loadProblematicTasks(email: String) {
        viewModelScope.launch {
            try {
                val tasks = repository.getProblematicAssignments(email)
                _problematicTasks.value = tasks
            } catch (e: Exception) {
                _problematicTasks.value = emptyList()
            }
        }
    }
}