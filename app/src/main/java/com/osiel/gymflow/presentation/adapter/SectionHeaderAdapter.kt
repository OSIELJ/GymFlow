package com.osiel.gymflow.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osiel.gymflow.databinding.ItemSectionHeaderBinding

class SectionHeaderAdapter(private val title: String) :
    RecyclerView.Adapter<SectionHeaderAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSectionHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.sectionTitle.text = title
    }
}
