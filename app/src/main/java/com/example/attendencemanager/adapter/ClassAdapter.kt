package com.example.attendencemanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.attendencemanager.R
import com.example.attendencemanager.activity.StudentActivity
import com.example.attendencemanager.databinding.SubjectItemListBinding
import com.example.attendencemanager.model.SubClassEntity

class ClassAdapter(
    private val context: Context,
    private val item: ArrayList<SubClassEntity>,
    private val deleteListener: (id: Int) -> Unit,
    private val updateListener:(id:Int)-> Unit
) : RecyclerView.Adapter<ClassAdapter.ViewHolder>(), View.OnLongClickListener,
    PopupMenu.OnMenuItemClickListener {

    inner class ViewHolder(binding: SubjectItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val textClass = binding.tvClass
        val textSubject = binding.tvSubject
       // val deleteList = binding.deleteSubClass
        val cardList = binding.card
       // val editList = binding.editSubClass
        val menu=binding.cardMenu
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassAdapter.ViewHolder {
        val binding =
            SubjectItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassAdapter.ViewHolder, position: Int) {
        val item = item[position]
        holder.textClass.text = item.ClassName
        holder.textSubject.text = item.SubjectName
        holder.cardList.setOnClickListener {
            val intent = Intent(context, StudentActivity::class.java)
            intent.putExtra("SUBJECT", item.SubjectName)
            intent.putExtra("CLASS_NAME", item.ClassName)
            intent.putExtra("class_id", item.id)
            context.startActivity(intent)
        }
        holder.menu.setOnClickListener{
            val popupMenu=PopupMenu(context,holder.menu)
            popupMenu.menuInflater.inflate(R.menu.popmenu_list,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId==R.id.delete_item){
                    deleteListener.invoke(item.id)
                }
                else if (it.itemId==R.id.update_item){
                    updateListener.invoke(item.id)
                }
                true
            }
            popupMenu.show()

        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onLongClick(v: View?): Boolean {
        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.popmenu_list)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete_item -> {
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
            }
            R.id.update_item -> {
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }
}