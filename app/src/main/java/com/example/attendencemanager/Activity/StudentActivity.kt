package com.example.attendencemanager.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendencemanager.Attendance
import com.example.attendencemanager.R
import com.example.attendencemanager.adapter.StudentAdapter
import com.example.attendencemanager.databinding.ActivityStudentBinding
import com.example.attendencemanager.databinding.AddDialogBinding
import com.example.attendencemanager.databinding.UpdateDialogBinding
import com.example.attendencemanager.getCurrentDate
import com.example.attendencemanager.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class StudentActivity : AppCompatActivity(), Attendance {
    private lateinit var binding: ActivityStudentBinding
    private lateinit var db: RollStudentDao
    private lateinit var dateDao: DateDao
    private var textRollNumber: String = ""
    private var textName: String = ""
    private var selectedDate: String = getCurrentDate("dd/MM/yyyy")
    private var dateList: MutableList<DateEntity> = ArrayList()
    private var studentList: MutableList<RollStudentEntity> = mutableListOf()
    private lateinit var adapter: StudentAdapter
    private lateinit var className: String
    private lateinit var subjectName: String
    private var classId: Int = -1

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        className = intent.getStringExtra("CLASS_NAME").orEmpty()
        subjectName = intent.getStringExtra("SUBJECT").orEmpty()
        classId = intent.getIntExtra("class_id", -1)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolBar()
        db = (application as DataBaseApplication).database.rollStudentDao()
        dateDao = (application as DataBaseApplication).database.dateDao()
        binding.floatingBtAddStudent.setOnClickListener {
            customDialogAddSubject()
        }
        setUpRecyclerView()
        getAttendance()
    }

    private fun getAttendance() {
        lifecycleScope.launch(Dispatchers.IO) {
            db.getStudentList(
                classId,
            ).collect {
                studentList.clear()
                studentList.addAll(it)
                dateDao.checkAttendance(selectedDate, classId).let { dateDateList ->
                    dateList = dateDateList as MutableList<DateEntity>
                    println("dateList size : ${dateDateList.size}   $selectedDate")
                    studentList.forEach { student ->
                        student.isAttendance = false
                        dateList.forEach { date ->
                            if (student.idR == date.studentId) {
                                student.isAttendance = student.idR == date.studentId
                                return@forEach
                            }
                        }
                    }
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_change_date -> {
                datePickerDialog {
                }
            }
            R.id.item_attendance_sheet -> {
                val intent = Intent(this, AttendanceActivity::class.java)
                intent.putExtra("Date", selectedDate)
                intent.putExtra("className", className)
                intent.putExtra("subjectName", subjectName)
                intent.putExtra("class_id", classId)
                startActivity(intent)
            }
            R.id.student_details_item -> {
//                Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, StudentDetails::class.java)
                intent.putExtra("className", className)
                intent.putExtra("subjectName", subjectName)
                intent.putExtra("class_id", classId)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolBar() {
        val toolbar = binding.toolbarTaskList
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
            actionBar.title = className
            actionBar.subtitle = subjectName + "|${selectedDate}"
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    private fun customDialogAddSubject() {
        val dialog = Dialog(this)
        val dBinding: AddDialogBinding = AddDialogBinding.inflate(layoutInflater)
        dBinding.etClass.hint = "RollNumber"
        dBinding.etSubject.hint = "Name"
        dBinding.tvAdd.text = "Add Student"
        dBinding.etClass.inputType = InputType.TYPE_CLASS_NUMBER
        dBinding.etClass.filters = arrayOf(InputFilter.LengthFilter(10))
        dialog.setContentView(dBinding.root)
        dBinding.btAdd.setOnClickListener {
            textRollNumber = dBinding.etClass.text.toString()
            textName = dBinding.etSubject.text.toString()
            db.apply {
                var userAvailable = false
                if (textRollNumber.isNotEmpty() && textRollNumber.isNotBlank() && textRollNumber.isNotEmpty() && textRollNumber.isNotBlank()) {
                    if (studentList.isEmpty()) {
                        addRecord(db)
                    } else if (studentList.isNotEmpty()) {
                        studentList.forEach { model ->
                            if (model.RollNumber.toString() == textRollNumber) {
                                Toast.makeText(
                                    this@StudentActivity,
                                    "roll Number already insert",
                                    Toast.LENGTH_SHORT
                                ).show()
                                userAvailable = true
                                return@forEach
                            }
                        }
                        if (!userAvailable) {
                            addRecord(db)
                        }
                    }
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@StudentActivity, "filled empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dBinding.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addRecord(RollStudentDao: RollStudentDao) {
        lifecycleScope.launch {
            RollStudentDao.insertRollStudent(
                RollStudentEntity(
                    idR = 0,
                    Name = textName,
                    RollNumber = textRollNumber.toInt(),
                    classId = classId
                )
            )
            Toast.makeText(this@StudentActivity, "add record", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpRecyclerView() {
        adapter =
            StudentAdapter(
                this,
                studentList as ArrayList<RollStudentEntity>,
                this@StudentActivity,
                { updateId -> updateRecord(updateId) },
                { deleteId -> deleteRecord(deleteId) }
            )
        binding.studentList.layoutManager = LinearLayoutManager(this)
        binding.studentList.adapter = adapter
        binding.tvNoData.visibility = View.GONE
    }

    override fun setAttendance(model: RollStudentEntity, isPresent: Boolean) {
        Log.d("DATA", model.toString())
        lifecycleScope.launch {

            if (isPresent) {
                for (item in dateList) {
                    if (item.studentId == model.idR) {
                        dateDao.deleteAttendance(item)
                        break
                    }
                }
            } else {
                val dateEntity = DateEntity(
                    0,
                    selectedDate,
                    model.idR,
                    model.Name,
                    model.classId,
                    model.RollNumber
                )
                dateDao.insertDate(dateEntity)
            }
            getAttendance()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun datePickerDialog(string: String.() -> Unit) {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            { View, selectedYear, selectedMonth, selectedDayOfMonth ->
                var day: String = selectedDayOfMonth.toString()
                var month: String = (selectedMonth + 1).toString()
                if (selectedDayOfMonth < 10) {
                    day = "0$day";
                }
                if ((selectedMonth + 1) < 10) {
                    month = "0$month"
                }
                selectedDate = "$day/$month/$selectedYear"
                supportActionBar?.let { actionBar ->
                    actionBar.title = className
                    actionBar.subtitle = subjectName + "|${selectedDate}"
                }
                getAttendance()
                Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show()
            }, year, month, day
        )
        dpd.datePicker.maxDate = Date().time - 1
        dpd.show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateRecord(id: Int) {
        val dialog = Dialog(this)
        val dBinding: UpdateDialogBinding = UpdateDialogBinding.inflate(layoutInflater)
        dBinding.tvAdd.text="Edit Student"
        dBinding.etSubject.hint="Name"
        dBinding.etClass.hint="Roll Number"
        dialog.setContentView(dBinding.root)
        lifecycleScope.launch {
            db.getUpdateStudent(id).collect {
                if (it != null) {
                    dBinding.etSubject.setText(it.Name)
                    dBinding.etClass.setText(it.RollNumber.toString())
                }
            }
        }
        dBinding.btAdd.setOnClickListener {
            lifecycleScope.launch {
                textName = dBinding.etSubject.text.toString()
                textRollNumber = dBinding.etClass.text.toString()
                if (textRollNumber.isNotEmpty() && textRollNumber.isNotBlank() && textName.isNotEmpty() && textName.isNotBlank()) {
                    db.updateStudentName(id,textName,textRollNumber.toInt())
                    dateDao.updateStudentNameWhenStudentNameUpdate(classId = classId, studentName = textName, rollNumber = textRollNumber.toInt())
                } else {
                    Toast.makeText(this@StudentActivity, "filled empty", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }
        dBinding.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteRecord(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        lifecycleScope.launch {
            db.getDeleteStudent(id).collect {
                if (it != null) {
                    builder.setMessage("Are you sure to delete${it.Name}")
                }
            }
        }
        builder.setPositiveButton("yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                db.deleteStudent(id)
                dateDao.deleteStudentAttendanceWhenDeleteStudent(id)
                Toast.makeText(this@StudentActivity, "Record Deleted", Toast.LENGTH_SHORT).show()
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