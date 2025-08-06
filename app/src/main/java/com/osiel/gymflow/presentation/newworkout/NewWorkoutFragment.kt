package com.osiel.gymflow.presentation.newworkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.osiel.gymflow.databinding.FragmentNewWorkoutBinding
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutState
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutViewModel
import com.osiel.gymflow.presentation.viewmodel.WorkoutViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewWorkoutFragment : Fragment() {

    private var _binding: FragmentNewWorkoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CrudWorkoutViewModel by viewModel()
    private val sharedWorkoutViewModel: WorkoutViewModel by activityViewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()
            viewModel.createWorkout(name, description)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is CrudWorkoutState.Loading -> {
                        binding.saveButton.isEnabled = false
                        binding.saveButton.text = "Salvando..."
                    }

                    is CrudWorkoutState.Success -> {
                        Toast.makeText(requireContext(), "Treino salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        sharedWorkoutViewModel.loadWorkouts()
                        binding.nameEditText.text?.clear()
                        binding.descriptionEditText.text?.clear()
                        viewModel.resetState()
                    }

                    is CrudWorkoutState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        binding.saveButton.isEnabled = true
                        binding.saveButton.text = "Salvar"
                        viewModel.resetState()
                    }

                    else -> {
                        binding.saveButton.isEnabled = true
                        binding.saveButton.text = "Salvar"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
