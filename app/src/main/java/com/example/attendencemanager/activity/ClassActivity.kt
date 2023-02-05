package com.example.attendencemanager.activity

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendencemanager.adapter.ClassAdapter
import com.example.attendencemanager.databinding.ActivityClassBinding
import com.example.attendencemanager.databinding.AddDialogBinding
import com.example.attendencemanager.databinding.UpdateDialogBinding
import com.example.attendencemanager.model.DateDao
import com.example.attendencemanager.model.RollStudentDao
import com.example.attendencemanager.model.SubClassDao
import com.example.attendencemanager.model.SubClassEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ClassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClassBinding
    private lateinit var db: SubClassDao
    private lateinit var rollStudentDao: RollStudentDao
    private lateinit var attendanceDao: DateDao
    var classList: List<SubClassEntity> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        db = (application as DataBaseApplication).database.subClassDao()
        rollStudentDao=(application as DataBaseApplication).database.rollStudentDao()
        attendanceDao=(application as DataBaseApplication).database.dateDao()
        binding.floatingBt.setOnClickListener {
            customDialogAddRecord()
        }
        lifecycleScope.launch {
            db.getAllSubClassList().collect {
                classList = ArrayList(it)
                setUpRecycleView(classList as ArrayList<SubClassEntity>)
            }
        }
    }

    private fun setUpRecycleView(
        subClassList: ArrayList<SubClassEntity>,
    ) {
        if (subClassList.isNotEmpty()) {
            val itemAdapter = ClassAdapter(
                this,
                subClassList,
                { deleteId -> deleteRecord(deleteId) },
                { updateId -> updateRecord(updateId) }
            )
            binding.revSubList.layoutManager = LinearLayoutManager(this)
            binding.revSubList.adapter = itemAdapter
            binding.tvNoData.visibility = View.GONE
            binding.revSubList.visibility = View.VISIBLE
        } else {
            binding.revSubList.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        }
    }

    private fun customDialogAddRecord() {
        val dialog = Dialog(this)
        val dBinding: AddDialogBinding = AddDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dBinding.root)
        dBinding.btAdd.setOnClickListener {
            val textClass = dBinding.etClass.text.toString()
            val textSubject = dBinding.etSubject.text.toString()
            db.apply {
                var userAvailable = false
                if (textClass.isNotEmpty()&&textClass.isNotBlank() && textSubject.isNotEmpty()&&textSubject.isNotBlank()) {
                    if (classList.isEmpty()) {
                        addRecord(db, textClass, textSubject)
                        dialog.dismiss()
                    } else if (classList.isNotEmpty()) {
                        classList.forEach { model ->
                            if (model.ClassName == textClass && model.SubjectName == textSubject) {
                                Toast.makeText(
                                    this@ClassActivity,
                                    "Filed is exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                                userAvailable = true
                                return@forEach
                            }
                        }
                        if (!userAvailable) {
                            addRecord(db, textClass, textSubject)
                        }
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(this@ClassActivity, "filled empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dBinding.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addRecord(SubClassDao: SubClassDao, textClass: String, textSubject: String) {
        lifecycleScope.launch {
            SubClassDao.insertSubClass(
                SubClassEntity(
                    id = 0,
                    ClassName = textClass,
                    SubjectName = textSubject
                )
            )
            Toast.makeText(this@ClassActivity, "add record", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRecord(id: Int) {
        val dialog = Dialog(this)
        val dBinding: UpdateDialogBinding = UpdateDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dBinding.root)
        lifecycleScope.launch {
            db.getUpdateSubClass(id).collect {
                if (it != null) {
                    dBinding.etClass.setText(it.ClassName)
                    dBinding.etSubject.setText(it.SubjectName)
                }
            }
        }
        dBinding.btAdd.setOnClickListener {
            lifecycleScope.launch {
                val textClass = dBinding.etClass.text.toString()
                val textSection = dBinding.etSubject.text.toString()
                if (textClass.isNotEmpty()&& textClass.isNotBlank() && textSection.isNotEmpty() &&textSection.isNotBlank()) {
                    db.updateSubClass(
                        SubClassEntity(
                            id,
                            ClassName = textClass,
                            SubjectName = textSection
                        )
                    )
                } else {
                    Toast.makeText(this@ClassActivity, "filled empty", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }
        dBinding.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteRecord(classId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        lifecycleScope.launch {
            db.getDeleteSubClass(classId).collect {
                if (it != null) {
                    builder.setMessage("Are you sure to delete${it.ClassName}")
                }
            }
        }
        builder.setPositiveButton("yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                db.deleteSubClass(SubClassEntity(classId))
                rollStudentDao.deleteStudentWhenDeleteClass(classId)
                attendanceDao.deleteStudentAttendanceWhenDeleteClass(classId)
                Toast.makeText(this@ClassActivity, "Record Deleted", Toast.LENGTH_SHORT).show()
            }
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("no") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}