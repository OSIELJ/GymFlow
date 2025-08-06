package com.osiel.gymflow.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osiel.gymflow.databinding.ItemWorkoutBinding
import com.osiel.gymflow.domain.model.Treino
import java.text.SimpleDateFormat
import java.util.*

class WorkoutAdapter(
    private val workouts: List<Treino>,
    private val onItemClicked: (Treino) -> Unit
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val training = workouts[position]
        holder.binding.apply {
            workoutNameText.text = training.nome
            workoutDescriptionText.text = training.descricao

            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(training.data.toDate())

            workoutDateText.text = "Data: $formattedDate"
        }
        holder.itemView.setOnClickListener {
            onItemClicked(training)
        }
    }
}

