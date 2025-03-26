package com.example.kursach_course

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kursach_course.databinding.FragmentSearchSystemFromProgramsBinding

class SearchSystemFromPrograms : Fragment() {
    private var _binding: FragmentSearchSystemFromProgramsBinding? = null
    private lateinit var binding: FragmentSearchSystemFromProgramsBinding
    private var lastQuery: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private val searchHistoryKey = "search_history"
    private val maxHistorySize = 10
    private lateinit var sharedPreferences: SharedPreferences
    private var isSearchRunning = false

    private val searchRunnable = Runnable {
        performSearch(binding.searchEditText.text.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSystemFromProgramsBinding.inflate(inflater, container, false)
        binding = FragmentSearchSystemFromProgramsBinding.inflate(inflater)
        sharedPreferences = requireContext().getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)

        setupSearchBar()

        return binding.root
    }
    private val buttonList = listOf(
        ButtonInfo("Array", R.id.action_searchSystemFromPrograms_to_programsArray),
        ButtonInfo("Dictionary", R.id.action_searchSystemFromPrograms_to_programsArray),
        ButtonInfo("File Handling", R.id.action_searchSystemFromPrograms_to_programsArray),
        ButtonInfo("Function", R.id.action_searchSystemFromPrograms_to_programsArray),
        ButtonInfo("Basic", R.id.action_searchSystemFromPrograms_to_programsArray),
        ButtonInfo("Exception Handling", R.id.action_searchSystemFromPrograms_to_programsArray),
        ButtonInfo("Formula", R.id.action_searchSystemFromPrograms_to_programsArray),
        ButtonInfo("Lists", R.id.action_searchSystemFromPrograms_to_programsArray)
    )
    private fun setupSearchBar() {
        binding.apply {
            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showSearchHistory()
                }
            }
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString()
                    if (query.isNotEmpty()) {
                        searchEditText.visibility = View.VISIBLE
                        clearButton.visibility = View.VISIBLE
                        handler.removeCallbacks(searchRunnable)
                        handler.postDelayed(searchRunnable, 2000)
                    } else {
                        clearButton.visibility = View.GONE
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            searchEditText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    handler.removeCallbacks(searchRunnable)
                    performSearch(searchEditText.text.toString())
                    true
                } else {
                    false
                }
            }
            searchEditText.setOnClickListener {
                showSearchHistory()
            }
            clearButton.setOnClickListener {
                searchEditText.text.clear()
                clearButton.visibility = View.GONE
                hideKeyboard()
            }
            retryButton.setOnClickListener {
                performSearch(lastQuery ?: "")
            }
            clearHistoryButton.setOnClickListener {
                clearSearchHistory()
            }
        }
    }
    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            showPlaceholderNoResults()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.resultContainer.visibility = View.GONE
        binding.placeholderLayout.visibility = View.GONE

        handler.postDelayed({
            val matchedButtons = buttonList.filter { it.name.contains(query, ignoreCase = true) }
            binding.progressBar.visibility = View.GONE

            if (matchedButtons.isNotEmpty()) {
                showSearchResults(matchedButtons)
                saveToSearchHistory(query)
            } else {
                showPlaceholderError()
            }
        }, 1500)
    }
    private fun showSearchResults(buttons: List<ButtonInfo>) {
        binding.resultContainer.removeAllViews()
        for (buttonInfo in buttons) {
            val newButton = AppCompatButton(requireContext()).apply {
                text = buttonInfo.name
                setOnClickListener {
                    findNavController().navigate(buttonInfo.actionId)
                }
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
            }
            binding.resultContainer.addView(newButton)
        }
        binding.resultContainer.visibility = View.VISIBLE
        binding.placeholderLayout.visibility = View.GONE
    }
    private fun showPlaceholderNoResults() {
        binding.placeholderLayout.visibility = View.VISIBLE
        binding.placeholderText.text = getString(R.string.no_results_found)
        binding.retryButton.visibility = View.GONE
        binding.resultContainer.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }
    private fun showPlaceholderError() {
        binding.placeholderLayout.visibility = View.VISIBLE
        binding.placeholderText.text = getString(R.string.error_occurred)
        binding.retryButton.visibility = View.VISIBLE
        binding.resultContainer.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }
    private fun hideSearchResults() {
        binding.resultContainer.removeAllViews()
        binding.placeholderLayout.visibility = View.GONE
    }
    private fun showSearchHistory() {
        val history = getSearchHistory()
        if (history.isNotEmpty()) {
            binding.historyContainer.removeAllViews()
            history.forEach { query ->
                val historyButton = Button(requireContext()).apply {
                    text = query
                    setOnClickListener {
                        binding.searchEditText.setText(query)
                        binding.searchEditText.setSelection(query.length)
                        performSearch(query)
                    }
                }
                binding.historyContainer.addView(historyButton)
            }
        }
    }
    private fun saveToSearchHistory(query: String) {
        val history = getSearchHistory().toMutableList()
        history.remove(query)
        history.add(0, query)
        if (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }
        sharedPreferences.edit().putStringSet(searchHistoryKey, history.toSet()).apply()
    }
    private fun getSearchHistory(): List<String> {
        return sharedPreferences.getStringSet(searchHistoryKey, emptySet())?.toList() ?: emptyList()
    }
    private fun clearSearchHistory() {
        sharedPreferences.edit().remove(searchHistoryKey).apply()
        binding.historyContainer.removeAllViews()
    }
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("lastQuery", binding.searchEditText.text.toString())
        outState.putBoolean("isSearchRunning", isSearchRunning)
    }
    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        _binding = null
        super.onDestroyView()
    }
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString("lastQuery")?.let {
            binding.searchEditText.setText(it)
            binding.searchEditText.setSelection(it.length)
            performSearch(it)
        }
    }
    data class ButtonInfo(
        val name: String,
        val actionId: Int
    )
}