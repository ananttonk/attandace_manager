package com.example.attendencemanager.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendencemanager.R
import com.example.attendencemanager.adapter.StudentDetailsAdapter
import com.example.attendencemanager.databinding.ActivityStudentDetailsBinding
import com.example.attendencemanager.model.DateDao
import com.example.attendencemanager.model.DateEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class StudentDetails : AppCompatActivity() {
    private lateinit var binding: ActivityStudentDetailsBinding
    private lateinit var dbDateDao: DateDao
    private var rollNumber = ""
    private var className = ""
    private var subjectName = ""
    private var classId = -1
    var studentListDetail: MutableList<DateEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailsBinding.inflate(layoutInflater)
        dbDateDao = (application as DataBaseApplication).database.dateDao()
        setContentView(binding.root)
        className = intent.getStringExtra("className").orEmpty()
        subjectName = intent.getStringExtra("subjectName").orEmpty()
        classId = intent.getIntExtra("class_id", -1)
        setupToolBar()
        binding.showButton.setOnClickListener {
            rollNumber = binding.etRollNumberDetail.text.toString()
                if (rollNumber.isNotBlank() && rollNumber.isNotEmpty()) {
                    getListStudentDetails()
            }
        }
    }

    private fun setupToolBar() {
        val toolbar = binding.toolbarTaskList
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
            actionBar.title = "Student Details"
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setUpAdapter() {
       val adapter = StudentDetailsAdapter(studentListDetail as ArrayList<DateEntity>)
        binding.revStudentDetail.layoutManager = LinearLayoutManager(this)
        binding.revStudentDetail.adapter = adapter
    }

    private fun getListStudentDetails() {
        lifecycleScope.launch {
            dbDateDao.allStudentDetails(
                classId,
                rollNumber.toInt()
            ).collect {
                studentListDetail = ArrayList(it)

                if (studentListDetail.isNotEmpty()){
                    binding.revStudentDetail.visibility=View.VISIBLE
                    setUpAdapter()
                    lifecycleScope.launch {
                        dbDateDao.studentDetails(
                            classId,
                            rollNumber.toInt()
                        ).collect { stu ->
                            binding.classDetail.text = className
                            binding.rollnumberDetail.text = stu.rollNumber.toString()
                            binding.nameDetail.text = stu.studentName
                            binding.sectionDetail.text = subjectName
                            binding.totalAttendanceDetail.text= studentListDetail.size.toString()
                        }
                    }
                }else{
                    Toast.makeText(this@StudentDetails, "record not match", Toast.LENGTH_SHORT).show()
                    binding.revStudentDetail.visibility=View.INVISIBLE
                    studentListDetail.clear()
                    binding.classDetail.text =""
                    binding.rollnumberDetail.text =""
                    binding.nameDetail.text = ""
                    binding.sectionDetail.text =""
                    binding.totalAttendanceDetail.text=""
                }
            }
        }
    }
}