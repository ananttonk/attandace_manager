package com.example.attendencemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendencemanager.databinding.ItemStudentDetailsBinding
import com.example.attendencemanager.model.DateEntity

class StudentDetailsAdapter(private val item: ArrayList<DateEntity>) :
    RecyclerView.Adapter<StudentDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemStudentDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentDetailsAdapter.ViewHolder {
        return ViewHolder(
            ItemStudentDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StudentDetailsAdapter.ViewHolder, position: Int) {
        val item = item[position]
        holder.binding.studentDateDetail.text = item.date
    }

    override fun getItemCount(): Int {
        return item.size
    }
}