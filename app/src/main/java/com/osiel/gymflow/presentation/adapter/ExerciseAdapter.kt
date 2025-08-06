package com.osiel.gymflow.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osiel.gymflow.domain.model.Exercicio

import coil.load
import com.osiel.gymflow.R
import com.osiel.gymflow.databinding.ItemExerciseBinding

class ExerciseAdapter(
    private val items: List<Exercicio>,
    private val onEditClick: (Exercicio) -> Unit,
    private val onDeleteClick: (Exercicio) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercicio) {
            binding.exerciseName.text = exercise.nome
            binding.exerciseObservation.text = exercise.observacoes


            binding.exerciseImage.load(exercise.imagemUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_exercise)
                error(R.drawable.ic_placeholder_exercise)
            }

            binding.btnEdit.setOnClickListener { onEditClick(exercise) }
            binding.btnDelete.setOnClickListener { onDeleteClick(exercise) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}