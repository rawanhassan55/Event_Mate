package com.example.eventymate.screens.eventadd

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventymate.R
import com.example.eventymate.screens.eventadd.components.CustomButton
import com.example.eventymate.screens.eventadd.components.CustomTextField
import com.example.eventymate.screens.eventadd.components.DateTimePicker


@Composable
fun CreateEventScreen(
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Create Event",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.image),
            contentDescription = "Event Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomButton(text = "Sport")
            CustomButton(text = "Birthday")
            CustomButton(text = "Meeting")
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = title,
            onValueChange = { title = it },
            label = "Event Title"
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = description,
            onValueChange = { description = it },
            label = "Event Description",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        DateTimePicker(
            label = "Event Date",
            value = eventDate,
            onValueChange = { eventDate = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DateTimePicker(
            label = "Event Time",
            value = eventTime,
            onValueChange = { eventTime = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = location,
            onValueChange = { location = it },
            label = "Location"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /*  save the event */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Save Event", fontSize = 18.sp)
        }
    }
}
