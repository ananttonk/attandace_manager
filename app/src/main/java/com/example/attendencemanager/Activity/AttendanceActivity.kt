package com.example.attendencemanager.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.attendencemanager.R
import com.example.attendencemanager.adapter.AttendanceSheetAdapter
import com.example.attendencemanager.databinding.ActivityAttendanceSheetBinding
import com.example.attendencemanager.model.DateDao
import com.example.attendencemanager.model.DateEntity
import com.example.attendencemanager.model.RollStudentDao
import com.view.calender.horizontal.umar.horizontalcalendarview.DayDateMonthYearModel
import com.view.calender.horizontal.umar.horizontalcalendarview.HorizontalCalendarListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class AttendanceActivity : AppCompatActivity(), HorizontalCalendarListener {
    private lateinit var binding: ActivityAttendanceSheetBinding
    private lateinit var dataBase: DateDao
    private lateinit var rollStudentDao: RollStudentDao
    private var className=""
    private var subjectName=""
    private var classId: Int = -1
    private var list:List<DateEntity> = ArrayList()
    private lateinit var adapter: AttendanceSheetAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceSheetBinding.inflate(layoutInflater)
        setUpRecyclerView()
        dataBase = (application as DataBaseApplication).database.dateDao()
        rollStudentDao = (application as DataBaseApplication).database.rollStudentDao()
        setContentView(binding.root)
        setupToolBar()
        binding.horizontalcalendarview.setContext(this)
    }

    private fun setupToolBar() {
        val toolbar = binding.toolbarTaskList
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
            actionBar.title = "Attendance"
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    override fun updateMonthOnScroll(selectedDate: DayDateMonthYearModel?) {
        binding.currentMonth.text = "${selectedDate?.month}/${selectedDate?.year}"
    }

    override fun newDateSelected(selectedDate: DayDateMonthYearModel?) {
        val day = selectedDate?.date
        val month = selectedDate?.monthNumeric
        val year = selectedDate?.year
        val date = "$day/$month/$year"
         className = intent.getStringExtra("className").orEmpty()
         subjectName = intent.getStringExtra("subjectName").orEmpty()
        classId = intent.getIntExtra("class_id", -1)
        lifecycleScope.launch {
            dataBase.attendance(date, classId = classId).collect {
                list = ArrayList(it)
                setUpRecyclerView()
            }
        }
    }

    private fun setUpRecyclerView() {
            adapter = AttendanceSheetAdapter(list as ArrayList<DateEntity>)
            binding.revPresentAbsentList.layoutManager = LinearLayoutManager(this)
            binding.revPresentAbsentList.adapter = adapter
        if(list.isEmpty()){
            binding.revPresentAbsentList.visibility = View.GONE
            binding.llAttendanceSheet.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        }else {
            binding.revPresentAbsentList.visibility = View.VISIBLE
            binding.llAttendanceSheet.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.GONE
        }
    }
}