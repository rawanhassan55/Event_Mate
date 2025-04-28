package com.example.eventymate.screens.eventadd.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DateTimePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                if (label.contains("Date", ignoreCase = true)) {
                    val datePicker = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                            onValueChange(formatter.format(calendar.time))
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePicker.show()
                } else if (label.contains("Time", ignoreCase = true)) {
                    // Time Picker
                    val timePicker = TimePickerDialog(
                        context,
                        { _: TimePicker, hourOfDay: Int, minute: Int ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            onValueChange(formatter.format(calendar.time))
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    )
                    timePicker.show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)

        ) {
            Text(
                text = if (value.isEmpty()) "Select $label" else value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
