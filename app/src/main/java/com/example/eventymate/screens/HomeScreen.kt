package com.example.eventymate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventymate.data.Note
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesEvent
import com.example.eventymate.presentation.NotesViewModel
import com.example.eventymate.screens.eventadd.CategorySelector
import com.example.eventymate.ui.theme.ThemeColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    onCreateEventNavigation: () -> Unit,
    state: NoteState,
    viewModel: NotesViewModel,
    onLanguageToggle: (String) -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
) {
    val colors = if (isDarkTheme) ThemeColors.Night else ThemeColors.Day
    val nameState = remember { mutableStateOf("Loading...") }
    //get the username from firebase
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nameState.value = document.getString("name") ?: "User".toString()
                    }
                }
                .addOnFailureListener {
                    nameState.value = "Failed to load name"
                }
        }
    }

    Scaffold(
        bottomBar = {
            EventMateBottomNavigation(
                navController = navController,
                backgroundColor = colors.surafce,
                contentColor = colors.text
            )
        },
        floatingActionButton = {
            FloatingCreateEventButton(
                onCreateEventNavigation = {
                    navController.navigate("createEvent")
                },
                backgroundColor = colors.primary
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        topBar = {
            TopBarSection(
                userName = nameState.value,
                onLanguageClick = {
                    val newLang = if (Locale.getDefault().language == "en") "ar" else "en"
                    onLanguageToggle(newLang)
                },
                onThemeToggle = onThemeToggle,
                backgroundColor = colors.primary,
                contentColor = colors.text,
                isDarkTheme = isDarkTheme
            )
        },
        backgroundColor = colors.bacground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            FiltersSection(
                state = state,
                viewModel = viewModel,
                navController = navController,
                filterColor = colors.primary,
                textColor = colors.text
            )

            if (state.notes.isEmpty()) {
                EmptyEventsIllustration()
            } else {
                NonEmptyEventsIllustration(
                    state = state,
                    viewModel = viewModel,
                    navController = navController,
                    containerColor = colors.surafce,
                    textColor = colors.text,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun TopBarSection(
    userName: String = "Rawan Hassan",
    onLanguageClick: () -> Unit = {},
    onThemeToggle: () -> Unit = {},
    backgroundColor: Color,
    contentColor: Color,
    isDarkTheme: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(
                text = stringResource(id = R.string.welcom),
                color = contentColor,
                fontSize = 14.sp
            )
            Text(
                text = userName,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (Locale.getDefault().language == "en") "AR" else "EN",
                color = contentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onLanguageClick() }
                    .padding(end = 12.dp)
            )
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.WbSunny else Icons.Default.DarkMode,
                contentDescription = stringResource(id = R.string.toggle_theme_description),
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onThemeToggle() }
            )
        }
    }
}

@Composable
fun FiltersSection(
    state: NoteState,
    viewModel: NotesViewModel,
    navController: NavController,
    filterColor: Color,
    textColor: Color,
) {
    CategorySelector(
        categories = listOf(
            stringResource(id = R.string.filter_all),
            stringResource(id = R.string.filter_work),
            stringResource(id = R.string.filter_education),
            stringResource(id = R.string.filter_personal),
            stringResource(id = R.string.filter_sport),
            stringResource(id = R.string.filter_birthday),
            stringResource(id = R.string.filter_travel),
            stringResource(id = R.string.filter_others)
        ),
        initialSelected = stringResource(id = R.string.filter_all),
        filterColor = filterColor,
        textColor = textColor,
        onCategorySelected = { selected ->
            viewModel.onEvent(NotesEvent.SelectCategory(selected))
        }
    )
}

@Composable
fun FilterItem(
    filter: String,
    filterColor: Color,
    textColor: Color,
) {
    Surface(
        shape = CircleShape,
        color = filterColor.copy(alpha = 0.1f),
        modifier = Modifier
            .height(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = filter,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun NonEmptyEventsIllustration(
    state: NoteState,
    viewModel: NotesViewModel,
    navController: NavController,
    containerColor: Color,
    textColor: Color,
    isDarkTheme: Boolean,
) {
    val filteredNotes =
        if (state.category == stringResource(id = R.string.filter_all) || state.category.isBlank()) {
            state.notes
        } else {
            state.notes.filter { it.category == state.category }
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(filteredNotes.size) { index ->
            NoteItem(
                note = filteredNotes[index],
                onEvent = viewModel::onEvent,
                containerColor = containerColor,
                textColor = textColor,
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
fun EmptyEventsIllustration() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.event_busy),
            contentDescription = stringResource(id = R.string.no_events_description),
            modifier = Modifier
                .size(150.dp)
        )
    }
}

@Composable
fun NoteItem(
    note: Note,
    onEvent: (NotesEvent) -> Unit,
    containerColor: Color,
    textColor: Color,
    isDarkTheme: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .padding(12.dp)
    ) {
        Row {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.description,
                    fontSize = 18.sp,
                    color = textColor
                )
            }
            IconButton(
                onClick = {
                    onEvent(NotesEvent.DeleteNote(note))
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete Note",
                    modifier = Modifier.size(35.dp),
                    tint = textColor
                )
            }
            // Add Love Icon Button
            IconButton(
                onClick = {
                    onEvent(NotesEvent.ToggleLoveNote(note))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Love Note",
                    tint = if (note.isLoved) Color.Red else textColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "\uD83D\uDCC5 ${note.eventDate}",
                fontSize = 14.sp,
                color = textColor,
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = note.eventTime,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "\uD83D\uDCCD${note.location}",
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = note.category,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            color = if (isDarkTheme) Color.Cyan else Color.Blue
        )
    }
}

@Composable
fun EventMateBottomNavigation(
    navController: NavController,
    backgroundColor: Color,
    contentColor: Color,
) {
    var selectedItem by remember { mutableStateOf(0) }
    BottomAppBar(
        cutoutShape = CircleShape,
        backgroundColor = backgroundColor
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f)) {
                BottomNavigationItem(
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        navController.navigate("home")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = contentColor
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            color = contentColor
                        )
                    }
                )
                BottomNavigationItem(
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Map",
                            tint = contentColor
                        )
                    },
                    label = {
                        Text(
                            text = "Map",
                            color = contentColor
                        )
                    }
                )
            }
            //Spacer(Modifier.weight(1f, true))
            Spacer(modifier = Modifier.width(48.dp))

            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                BottomNavigationItem(
                    selected = selectedItem == 2,
                    onClick = {
                        selectedItem = 2
                        navController.navigate("love")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Love",
                            tint = contentColor
                        )
                    },
                    label = {
                        Text(
                            text = "Love",
                            color = contentColor
                        )
                    }
                )
                BottomNavigationItem(
                    selected = selectedItem == 3,
                    onClick = {
                        selectedItem = 3
                        navController.navigate("profile")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = contentColor
                        )
                    },
                    label = {
                        Text(
                            text = "Profile",
                            color = contentColor
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FloatingCreateEventButton(
    onCreateEventNavigation: () -> Unit,
    backgroundColor: Color,
) {
    FloatingActionButton(
        onClick = {
            onCreateEventNavigation()
        },
        backgroundColor = backgroundColor,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create Event",
            tint = Color.White
        )
    }
}