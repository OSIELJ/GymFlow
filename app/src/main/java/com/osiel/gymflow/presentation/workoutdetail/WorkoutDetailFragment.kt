package com.osiel.gymflow.presentation.workoutdetail

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import coil.load
import com.osiel.gymflow.R
import com.osiel.gymflow.databinding.DialogAddExerciseBinding
import com.osiel.gymflow.databinding.DialogEditWorkoutBinding
import com.osiel.gymflow.databinding.FragmentWorkoutDetailBinding
import com.osiel.gymflow.domain.model.Exercicio
import com.osiel.gymflow.domain.model.Treino
import com.osiel.gymflow.presentation.adapter.ExerciseAdapter
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutState
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutViewModel
import com.osiel.gymflow.presentation.viewmodel.ExerciseState
import com.osiel.gymflow.presentation.viewmodel.ExerciseViewModel
import com.osiel.gymflow.presentation.viewmodel.SelectedWorkout
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
    private val exerciseViewModel: ExerciseViewModel by viewModel()

    private lateinit var workout: Treino

    private var selectedImageUri: Uri? = null
    private var dialogPreviewImage: ImageView? = null
    private var crudExerciseDialog: AlertDialog? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            dialogPreviewImage?.load(uri) { crossfade(true) }
        }
    }

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
                when (selectedWorkout) {
                    is SelectedWorkout.UserWorkout -> {
                        workout = selectedWorkout.treino
                        updateUiForWorkout(isSuggested = false)
                        exerciseViewModel.loadExercises(workout.id, isSuggested = false)
                    }
                    is SelectedWorkout.SuggestedWorkout -> {
                        workout = selectedWorkout.treino
                        updateUiForWorkout(isSuggested = true)
                        exerciseViewModel.loadExercises(workout.id, isSuggested = true)
                    }
                    is SelectedWorkout.None -> {

                    }
                }
            }
        }

        observeCrudViewModel()
        observeExercises()
        observeExerciseCrudState()
    }


    private fun updateUiForWorkout(isSuggested: Boolean) {
        binding.toolbar.title = workout.nome
        binding.textWorkoutName.text = workout.nome
        binding.textWorkoutDescription.text = workout.descricao
        val visibility = if (isSuggested) View.GONE else View.VISIBLE
        binding.buttonEdit.visibility = visibility
        binding.buttonDelete.visibility = visibility
        binding.buttonAddExercise.visibility = visibility

        if (!isSuggested) {
            setupListeners()
        }
    }

    private fun observeExercises() {
        viewLifecycleOwner.lifecycleScope.launch {
            exerciseViewModel.exercises.collectLatest { exercisesList ->
                val exerciseAdapter = ExerciseAdapter(
                    items = exercisesList,
                    onEditClick = { exercise ->
                        showEditExerciseDialog(exercise)
                    },
                    onDeleteClick = { exercise ->
                        showDeleteExerciseConfirmationDialog(exercise)
                    }
                )
                binding.recyclerExercises.adapter = exerciseAdapter
            }
        }
    }

    private fun setupListeners() {
        binding.buttonEdit.setOnClickListener { showEditDialog() }
        binding.buttonDelete.setOnClickListener { showDeleteConfirmation() }
        binding.buttonAddExercise.setOnClickListener { showAddExerciseDialog() }
    }

    private fun showAddExerciseDialog() {
        selectedImageUri = null
        dialogPreviewImage = null

        val dialogBinding = DialogAddExerciseBinding.inflate(layoutInflater)
        dialogPreviewImage = dialogBinding.imagePreview

        crudExerciseDialog = AlertDialog.Builder(requireContext())
            .setTitle("Novo Exercício")
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialogBinding.btnSelectImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        crudExerciseDialog?.setOnShowListener {
            val saveButton = crudExerciseDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton?.setOnClickListener {
                val name = dialogBinding.editExerciseName.text.toString().trim()
                val obs = dialogBinding.editExerciseObservation.text.toString().trim()
                exerciseViewModel.createExercise(workout.id, name, obs, selectedImageUri)
            }
        }

        crudExerciseDialog?.show()
    }


    private fun showEditExerciseDialog(exercise: Exercicio) {
        val dialogBinding = DialogAddExerciseBinding.inflate(layoutInflater)

        dialogBinding.editExerciseName.setText(exercise.nome)
        dialogBinding.editExerciseObservation.setText(exercise.observacoes)
        dialogBinding.imagePreview.load(exercise.imagemUrl) {
            placeholder(R.drawable.ic_placeholder_exercise)
            error(R.drawable.ic_placeholder_exercise)
        }

        dialogBinding.btnSelectImage.visibility = View.GONE

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Exercício")
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = dialogBinding.editExerciseName.text.toString().trim()
                val newObs = dialogBinding.editExerciseObservation.text.toString().trim()

                if (newName.isNotEmpty()) {
                    val updatedExercise = exercise.copy(nome = newName, observacoes = newObs)
                    exerciseViewModel.updateExercise(workout.id, updatedExercise)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteExerciseConfirmationDialog(exercise: Exercicio) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Exercício")
            .setMessage("Tem certeza que deseja excluir o exercício '${exercise.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                exerciseViewModel.deleteExercise(workout.id, exercise.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeExerciseCrudState() {
        lifecycleScope.launch {
            exerciseViewModel.state.collectLatest { state ->
                val saveButton = crudExerciseDialog?.getButton(AlertDialog.BUTTON_POSITIVE)

                when (state) {
                    is ExerciseState.Loading -> {
                        saveButton?.isEnabled = false
                    }
                    is ExerciseState.Success -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        crudExerciseDialog?.dismiss()
                        exerciseViewModel.resetState()
                    }
                    is ExerciseState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        saveButton?.isEnabled = true
                        exerciseViewModel.resetState()
                    }
                    is ExerciseState.Idle -> {
                        saveButton?.isEnabled = true
                    }
                }
            }
        }
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
        crudExerciseDialog?.dismiss()
        _binding = null
    }
}