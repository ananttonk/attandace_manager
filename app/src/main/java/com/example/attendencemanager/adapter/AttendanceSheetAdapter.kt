package com.example.attendencemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.attendencemanager.databinding.AttendanceItemListBinding
import com.example.attendencemanager.model.DateDao
import com.example.attendencemanager.model.DateEntity
import com.example.attendencemanager.model.RollStudentDao
import com.example.attendencemanager.model.RollStudentEntity

class AttendanceSheetAdapter(
    private val item: ArrayList<DateEntity>,
) : RecyclerView.Adapter<AttendanceSheetAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: AttendanceItemListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendanceSheetAdapter.ViewHolder {
        return ViewHolder(
            AttendanceItemListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AttendanceSheetAdapter.ViewHolder, position: Int) {
        val item=item[position]
        holder.binding.attendanceStudentRoll.text= item.rollNumber.toString()
        holder.binding.attendanceStudentName.text=item.studentName
    }

    override fun getItemCount(): Int {
        return item.size
    }
}