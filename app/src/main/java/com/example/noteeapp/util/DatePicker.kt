package com.example.noteeapp.util

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePicker (val listener: (day:Int, month: Int, year: Int)-> Unit ) : DialogFragment(), OnDateSetListener{
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        listener(day, month+1, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        val month: Int = c.get(Calendar.MONTH)
        val year: Int = c.get(Calendar.YEAR)
        return DatePickerDialog(activity as Context, this, year, month, day)
    }
}