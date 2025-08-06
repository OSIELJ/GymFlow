package com.osiel.gymflow.presentation.workoutdetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.osiel.gymflow.R
import com.osiel.gymflow.databinding.DialogEditWorkoutBinding
import com.osiel.gymflow.databinding.FragmentWorkoutDetailBinding
import com.osiel.gymflow.domain.model.Treino
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutState
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutViewModel
import com.osiel.gymflow.presentation.viewmodel.WorkoutViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WorkoutDetailFragment : Fragment() {

    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!
    private val crudViewModel: CrudWorkoutViewModel by viewModel()
    private val sharedWorkoutViewModel: WorkoutViewModel by activityViewModel()

    private lateinit var workout: Treino

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
        binding.toolbar.title = ""

        ViewCompat.setOnApplyWindowInsetsListener(binding.buttonActions) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom + v.paddingTop)
            insets
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedWorkoutViewModel.selectedWorkout.collectLatest { selectedWorkout ->
                if (selectedWorkout != null) {
                    workout = selectedWorkout
                    binding.toolbar.title = workout.nome
                    binding.textWorkoutName.text = workout.nome
                    binding.textWorkoutDescription.text = workout.descricao
                    setupListeners()
                }
            }
        }

        observeCrudViewModel()
    }

    private fun setupListeners() {
        binding.buttonEdit.setOnClickListener { showEditDialog() }
        binding.buttonDelete.setOnClickListener { showDeleteConfirmation() }
    }

    private fun showEditDialog() {
        val dialogBinding = DialogEditWorkoutBinding.inflate(LayoutInflater.from(requireContext()))
        val nameInput = dialogBinding.editWorkoutName
        val descriptionInput = dialogBinding.editWorkoutDescription

        nameInput.setText(workout.nome)
        descriptionInput.setText(workout.descricao)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar treino")
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = dialogBinding.editWorkoutName.text.toString().trim()
                val newDescription = dialogBinding.editWorkoutDescription.text.toString().trim()

                if (newName.isNotEmpty()) {
                    crudViewModel.updateWorkout(workout.copy(nome = newName, descricao = newDescription))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir treino")
            .setMessage("Tem certeza que deseja excluir este treino?")
            .setPositiveButton("Excluir") { _, _ ->
                crudViewModel.deleteWorkout(workout.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeCrudViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            crudViewModel.state.collectLatest { state ->
                when (state) {
                    is CrudWorkoutState.Loading -> {
                        binding.buttonEdit.isEnabled = false
                        binding.buttonDelete.isEnabled = false
                    }
                    is CrudWorkoutState.Success -> {
                        Toast.makeText(requireContext(), "Alteração realizada com sucesso", Toast.LENGTH_SHORT).show()
                        sharedWorkoutViewModel.loadWorkouts()
                        findNavController().popBackStack()
                        crudViewModel.resetState()
                    }
                    is CrudWorkoutState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        binding.buttonEdit.isEnabled = true
                        binding.buttonDelete.isEnabled = true
                        crudViewModel.resetState()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedWorkoutViewModel.clearSelectedWorkout()
        _binding = null
    }
}