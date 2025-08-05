package com.osiel.gymflow.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osiel.gymflow.databinding.ItemWorkoutBinding
import com.osiel.gymflow.domain.model.Treino

class WorkoutAdapter(
    private val workouts: List<Treino>
) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = workouts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.workoutNameText.text = workouts[position].nome
    }
}
