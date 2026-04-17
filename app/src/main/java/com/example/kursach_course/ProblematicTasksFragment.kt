package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kursach_course.api.KtorRetrofitClient
import com.example.kursach_course.databinding.FragmentProblematicTasksBinding
import com.example.kursach_course.databinding.ItemProblematicBinding
import kotlinx.coroutines.launch

class ProblematicTasksFragment : Fragment() {

    private var _binding: FragmentProblematicTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: AssignmentRepository
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProblematicTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("USER_EMAIL", "") ?: ""

        repository = AssignmentRepository(KtorRetrofitClient.authService)

        val adapter = ProblematicAdapter(
            onRetry = { assignment ->
                // Открыть задание заново
                val bundle = Bundle().apply {
                    putInt("assignment_id", assignment.assignmentId)
                }
                findNavController().navigate(R.id.action_problematic_to_solveAssignment, bundle)
            },
            onTheory = { assignment ->
                // Загрузить подсказки и открыть первую теоретическую тему
                lifecycleScope.launch {
                    try {
                        val help = repository.getHelpForAssignment(assignment.assignmentId)
                        val firstTopic = help.theoryTopics.firstOrNull()
                        if (firstTopic != null) {
                            val bundle = Bundle().apply {
                                putInt("topic_id", firstTopic.topicId)
                                putString("topic_title", firstTopic.title)
                            }
                            findNavController().navigate(
                                R.id.action_problematicTasksFragment_to_ChapWelcomePython,
                                bundle
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Нет рекомендаций по теории",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onExtra = { assignment ->
                lifecycleScope.launch {
                    try {
                        val help = repository.getHelpForAssignment(assignment.assignmentId)
                        val extra = help.extraAssignment
                        if (extra != null) {
                            // Загружаем именно из extra_assignments
                            val extraFull = repository.getExtraAssignmentById(extra.assignmentId)
                            val bundle = Bundle().apply {
                                putString("assignment_title", extraFull.title)
                                putString("assignment_content", extraFull.content)
                                putString("correct_answer", extraFull.correctAnswer)
                                putInt("assignment_id", -1) // -1 чтобы не грузить с сервера повторно
                            }
                            findNavController().navigate(
                                R.id.action_problematic_to_solveAssignment,
                                bundle
                            )
                        } else {
                            Toast.makeText(requireContext(), "Нет похожих задач", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val tasks = repository.getProblematicAssignments(email)
                binding.progressBar.visibility = View.GONE
                adapter.submitList(tasks)
                binding.emptyText.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ProblematicAdapter(
    private val onRetry: (ProblematicResponse) -> Unit,
    private val onTheory: (ProblematicResponse) -> Unit,
    private val onExtra: (ProblematicResponse) -> Unit
) : ListAdapter<ProblematicResponse, ProblematicAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProblematicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            titleText.text = item.title
            topicText.text = item.topicTitle
            attemptsText.text = "Попыток: ${item.attemptCount}"
            btnRetry.setOnClickListener { onRetry(item) }
            btnTheory.setOnClickListener { onTheory(item) }
            btnExtra.setOnClickListener { onExtra(item) }
        }
    }

    class ViewHolder(val binding: ItemProblematicBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class DiffUtilCallback : DiffUtil.ItemCallback<ProblematicResponse>() {
    override fun areItemsTheSame(old: ProblematicResponse, new: ProblematicResponse) =
        old.assignmentId == new.assignmentId
    override fun areContentsTheSame(old: ProblematicResponse, new: ProblematicResponse) =
        old == new
}