package com.example.eventymate.screens.eventadd

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventymate.R
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesEvent
import com.example.eventymate.screens.eventadd.components.CustomButton
import com.example.eventymate.screens.eventadd.components.CustomTextField
import com.example.eventymate.screens.eventadd.components.DateTimePicker
import com.example.eventymate.ui.theme.ThemeColors


@Composable
fun CreateEventScreen(
    onBackClick: () -> Unit,
    state: NoteState,
    navController: NavController,
    onEvent: (NotesEvent) -> Unit
) {
    var isTitleError  by remember { mutableStateOf(false) }
    var isDescriptionError  by remember { mutableStateOf(false) }
    var isDateError  by remember { mutableStateOf(false) }
    var isTimeError  by remember { mutableStateOf(false) }
    var isLocationError  by remember { mutableStateOf(false) }
    var isCategoryError  by remember { mutableStateOf(false) }
    state.category = ""


    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {

        Text(
            text = "Create Event",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.pic),
            contentDescription = "Event Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val eventCategories = listOf(
            stringResource(id = R.string.filter_work),
            stringResource(id = R.string.filter_education),
            stringResource(id = R.string.filter_personal),
            stringResource(id = R.string.filter_sport),
            stringResource(id = R.string.filter_birthday),
            stringResource(id = R.string.filter_travel),
            stringResource(id = R.string.filter_others)
        )

        CategorySelector(categories = eventCategories) { selected ->
            println("Selected Category: $selected")
            state.category = selected
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = state.title.value,
            onValueChange = {
                state.title.value = it
                isTitleError = it.trim().isEmpty()
            },
            label = "Event Title",
            isError = isTitleError,
            )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = state.description.value,
            onValueChange = {
                state.description.value = it
                isDescriptionError = it.trim().isEmpty()
            },
            label = "Event Description",
            singleLine = false,
            isError = isDescriptionError,
            )

        Spacer(modifier = Modifier.height(8.dp))

        DateTimePicker(
            label = "Event Date",
            value = state.eventDate.value,
            onValueChange = {
                state.eventDate.value = it
                isDateError = it.trim().isEmpty()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DateTimePicker(
            label = "Event Time",
            value = state.eventTime.value,
            onValueChange = {
                state.eventTime.value = it
                isTimeError = it.trim().isEmpty()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = state.location.value,
            onValueChange = {
                state.location.value = it
                isLocationError = it.trim().isEmpty()
            },
            label = "Location",
            isError = isLocationError,
            )

        Spacer(modifier = Modifier.height(16.dp))

        if (isTitleError) {
            Text("Title is required", color = Color.Red, fontSize = 12.sp)
        }
        if (isDescriptionError) {
            Text("Description is required", color = Color.Red, fontSize = 12.sp)
        }
        if (isDateError) {
            Text("Date is required", color = Color.Red, fontSize = 12.sp)
        }
        if (isTimeError) {
            Text("Time is required", color = Color.Red, fontSize = 12.sp)
        }
        if (isLocationError) {
            Text("Location is required", color = Color.Red, fontSize = 12.sp)
        }
        if (isCategoryError) {
            Text("Category is required", color = Color.Red, fontSize = 12.sp)
        }

        Button(
            onClick = {

                val titleEmpty = state.title.value.trim().isEmpty()
                val descriptionEmpty = state.description.value.trim().isEmpty()
                val dateEmpty = state.eventDate.value.trim().isEmpty()
                val timeEmpty = state.eventTime.value.trim().isEmpty()
                val locationEmpty = state.location.value.trim().isEmpty()
                val categoryEmpty = state.category.trim().isEmpty()


                isTitleError = titleEmpty
                isDescriptionError = descriptionEmpty
                isDateError = dateEmpty
                isTimeError = timeEmpty
                isLocationError = locationEmpty
                isCategoryError = categoryEmpty


                //Save Note
                if (!titleEmpty && !descriptionEmpty && !dateEmpty && !timeEmpty && !locationEmpty && !categoryEmpty) {
                    onEvent(NotesEvent.SaveNote(
                    title = state.title.value,
                    description = state.description.value,
                    eventDate = state.eventDate.value,
                    eventTime = state.eventTime.value,
                    location = state.location.value
                ))
                navController.popBackStack()
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Save Event", fontSize = 18.sp)
        }
    }
}




@Composable
fun CategorySelector(
    categories: List<String>,
    initialSelected: String = "",
    filterColor: Color = Color(0xFF6200EE),
    textColor: Color = Color.Black,
    onCategorySelected: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialSelected) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        selectedCategory = category
                        onCategorySelected(category)
                    }
                    .background(
                        if (isSelected) filterColor else ThemeColors.Day.surafce,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
