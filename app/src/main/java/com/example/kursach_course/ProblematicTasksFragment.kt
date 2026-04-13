package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentProblematicTasksBinding
import com.example.kursach_course.databinding.ItemProblematicBinding

// ui/ProblematicTasksFragment.kt
class ProblematicTasksFragment : Fragment() {

    private var _binding: FragmentProblematicTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProblematicViewModel
    private lateinit var adapter: ProblematicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProblematicTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем email пользователя из SharedPreferences
        val email = getUserEmail()

        // Создаём репозиторий и ViewModel
        val api = KtorRetrofitClient.authService  // замените на ваш способ получения api
        val repository = AssignmentRepository(api)
        viewModel = ProblematicViewModel(repository)

        // Настройка адаптера
        adapter = ProblematicAdapter { assignment ->
            val bundle = Bundle().apply {
                putInt("assignment_id", assignment.assignmentId)
                // НЕ передаём content и correctAnswer – они будут загружены отдельно
            }
            findNavController().navigate(R.id.action_problematic_to_solveAssignment, bundle)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Загрузка данных
        binding.progressBar.visibility = View.VISIBLE
        viewModel.loadProblematicTasks(email)

        // Наблюдение за данными
        viewModel.problematicTasks.observe(viewLifecycleOwner) { tasks ->
            binding.progressBar.visibility = View.GONE
            adapter.submitList(tasks)
            binding.emptyText.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }

        // Кнопка "Назад"
        binding.toolbar.setNavigationOnClickListener {
            view.findNavController().navigateUp()
        }
    }

    private fun getUserEmail(): String {
        val prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return prefs.getString("USER_EMAIL", "") ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Адаптер
class ProblematicAdapter(private val onClick: (ProblematicResponse) -> Unit) :
    ListAdapter<ProblematicResponse, ProblematicAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProblematicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            titleText.text = item.title
            topicText.text = item.topicTitle
            attemptsText.text = "Попыток: ${item.attemptCount}"
            root.setOnClickListener { onClick(item) }
        }
    }

    class ViewHolder(val binding: ItemProblematicBinding) : RecyclerView.ViewHolder(binding.root)
}

class DiffUtilCallback : DiffUtil.ItemCallback<ProblematicResponse>() {
    override fun areItemsTheSame(oldItem: ProblematicResponse, newItem: ProblematicResponse) =
        oldItem.assignmentId == newItem.assignmentId

    override fun areContentsTheSame(oldItem: ProblematicResponse, newItem: ProblematicResponse) =
        oldItem == newItem
}