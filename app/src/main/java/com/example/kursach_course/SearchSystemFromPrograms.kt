package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursach_course.databinding.FragmentSearchSystemFromProgramsBinding

class SearchSystemFromPrograms : Fragment() {

    private lateinit var binding: FragmentSeachSystemFromProgramsBinding
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchSystemFromProgramsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Восстановление последнего запроса при изменении ориентации
        savedInstanceState?.let {
            lastQuery = it.getString("SEARCH_QUERY", "")
            binding.searchInput.setText(lastQuery)
        }

        // Обработка нажатия на кнопку "Очистить"
        binding.clearButton.setOnClickListener {
            binding.searchInput.text.clear()
            hideKeyboard()
        }

        // Отслеживание изменений текста в поле ввода
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.clearButton.visibility = View.GONE
                    binding.buttonsContainer.visibility = View.GONE
                } else {
                    binding.clearButton.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.isEmpty()) {
                        binding.buttonsContainer.visibility = View.GONE
                    } else {
                        showButtons(it.toString())
                    }
                }
            }
        })

        // Обработка нажатия на кнопку "Обновить"
        binding.retryButton.setOnClickListener {
            retryLastRequest()
        }

        // Обработка нажатия на кнопку "Поиск" на клавиатуре
        binding.searchInput.setOnEditorActionListener { _, _, _ ->
            performSearch(binding.searchInput.text.toString())
            true // Возвращаем true, чтобы обработать событие
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY", binding.searchInput.text.toString())
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    private fun showNoResultsPlaceholder() {
        binding.noResultsPlaceholder.text = "Результатов не найдено"
        binding.noResultsPlaceholder.visibility = View.VISIBLE
        binding.retryButton.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        binding.noResultsPlaceholder.text = "Ничего не найдено. Попробуйте ещё раз."
        binding.noResultsPlaceholder.visibility = View.VISIBLE
        binding.retryButton.visibility = View.VISIBLE // Показываем кнопку "Обновить"
    }

    private fun retryLastRequest() {
        lastQuery?.let { query ->
            if (query.isNotEmpty()) {
                performSearch(query) // Повторно выполняем поиск с последним запросом
            } else {
                Toast.makeText(requireContext(), "Нет последнего запроса", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSearch(query: String) {
        println("Выполняется поиск: $query") // Логирование
        lastQuery = query.trim()

        if (query.isEmpty()) {
            showNoResultsPlaceholder()
        } else {
            val searchResults = getSearchResults(query)
            println("Результаты поиска: $searchResults") // Логирование

            if (searchResults.isEmpty()) {
                showErrorPlaceholder()
            } else {
                binding.noResultsPlaceholder.visibility = View.GONE
                binding.retryButton.visibility = View.GONE
                showButtons(query)
            }
        }
    }

    private fun showButtons(query: String) {
        val buttonMap = mapOf(
            "array" to FragmentType.Array,
            "dictionary" to FragmentType.Dictionary,
            "file" to FragmentType.File,
            "function" to FragmentType.Function,
            "basic" to FragmentType.Basic,
            "exception" to FragmentType.Exception,
            "formula" to FragmentType.Formula,
            "lists" to FragmentType.Lists
        )

        binding.dynamicButtonContainer.removeAllViews() // Очищаем контейнер

        for ((key, fragmentType) in buttonMap) {
            if (key.contains(query, ignoreCase = true)) {
                val button = Button(requireContext()).apply {
                    text = key.capitalize()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setOnClickListener {
                        handleButtonClick(fragmentType)
                    }
                }
                binding.dynamicButtonContainer.addView(button)
            }
        }

        // Показываем контейнер с кнопками
        binding.buttonsContainer.visibility = View.VISIBLE
    }

    private fun handleButtonClick(fragmentType: FragmentType) {
        when (fragmentType) {
            FragmentType.Array -> findNavController().navigate(R.id.action_searchSystemFromPrograms_to_programsArray)
            FragmentType.Basic -> Toast.makeText(requireContext(), "Exception not implemented", Toast.LENGTH_SHORT).show()
            FragmentType.Dictionary -> Toast.makeText(requireContext(), "Exception not implemented", Toast.LENGTH_SHORT).show()
            FragmentType.Exception -> Toast.makeText(requireContext(), "Exception not implemented", Toast.LENGTH_SHORT).show()
            FragmentType.File -> Toast.makeText(requireContext(), "File not implemented", Toast.LENGTH_SHORT).show()
            FragmentType.Formula -> Toast.makeText(requireContext(), "Formula not implemented", Toast.LENGTH_SHORT).show()
            FragmentType.Function -> Toast.makeText(requireContext(), "Function not implemented", Toast.LENGTH_SHORT).show()
            FragmentType.Lists -> Toast.makeText(requireContext(), "Lists not implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSearchResults(query: String): List<String> {
        val availableButtons = listOf(
            "array", "dictionary", "file", "function",
            "basic", "exception", "formula", "lists"
        )
        return availableButtons.filter { it.contains(query, ignoreCase = true) }
    }

    sealed class FragmentType {
        object Array : FragmentType()
        object Dictionary : FragmentType()
        object File : FragmentType()
        object Function : FragmentType()
        object Basic : FragmentType()
        object Exception : FragmentType()
        object Formula : FragmentType()
        object Lists : FragmentType()
    }
}