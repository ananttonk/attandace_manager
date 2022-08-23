package com.example.attendencemanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.attendencemanager.Attendance
import com.example.attendencemanager.R
import com.example.attendencemanager.databinding.StudentItemListBinding
import com.example.attendencemanager.model.DateEntity
import com.example.attendencemanager.model.RollStudentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


class StudentAdapter(
    private val context: Context,
    private val item: MutableList<RollStudentEntity>,
    private val listener: Attendance,
    private val updateListener:(id:Int)-> Unit,
    private val deleteListener: (id: Int) -> Unit,
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAdapter.ViewHolder {
        return ViewHolder(
            StudentItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StudentAdapter.ViewHolder, position: Int) {
        val item = item[position]
        holder.binding.tvRollNumber.text =item.RollNumber.toString()
        holder.binding.tvName.text = item.Name
        holder.binding.cardMenu.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.binding.cardMenu)
            popupMenu.menuInflater.inflate(R.menu.popmenu_list, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId==R.id.delete_item){
                    deleteListener.invoke(item.idR)
                }
                else if (it.itemId==R.id.update_item){
                    updateListener.invoke(item.idR)
                }
                true
            }
            popupMenu.show()
        }
        if (item.isAttendance) {
            holder.binding.card.setCardBackgroundColor(Color.GREEN)
        } else {
            holder.binding.card.setCardBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class ViewHolder(val binding: StudentItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val item = item[layoutPosition]
                listener.setAttendance(item, item.isAttendance)
            }
        }
    }
}