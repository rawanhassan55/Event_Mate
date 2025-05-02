/*package com.example.eventymate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    onSettingNavigation: () -> Unit,
    onSignOut: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Button(onClick = { onSettingNavigation() }) {
            Text(text = "Settings")
        }
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(onClick = {onSignOut() }) {
            Icon(Icons.Filled.ExitToApp, contentDescription = "Localized description")
        }
    }
}
*/

package com.example.eventymate.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventymate.R
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesEvent
import com.example.eventymate.presentation.NotesViewModel
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    onCreateEventNavigation: () -> Unit,
    state: NoteState,
    viewModel: NotesViewModel,
    onLanguageToggle: (String) -> Unit
) {
    Scaffold(
        bottomBar = { EventMateBottomNavigation(navController) },
        floatingActionButton = {
            FloatingCreateEventButton(
                onCreateEventNavigation = {
                    navController.navigate("createEvent")
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        topBar = {
            TopBarSection(onLanguageClick = {
                val newLang = if (Locale.getDefault().language == "en") "ar" else "en"
                onLanguageToggle(newLang)
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            FiltersSection()
            if (state.notes.isEmpty()) {
                EmptyEventsIllustration()
            } else {
                NonEmptyEventsIllustration(
                    state = state, viewModel = viewModel, navController = navController
                )
            }
        }
    }
}

@Composable
fun TopBarSection(
    userName: String = "Rawan Hassan",
    onThemeToggle: () -> Unit = {},
    onLanguageClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0XFF4A5182))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(id = R.string.welcom),
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = userName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (Locale.getDefault().language == "en") "AR" else "EN",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onLanguageClick() }
                    .padding(end = 12.dp)
            )
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = stringResource(id = R.string.toggle_theme_description),
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onThemeToggle() }
            )
        }
    }
}

@Composable
fun FiltersSection() {
    val filters = listOf(
        stringResource(id = R.string.filter_all),
        stringResource(id = R.string.filter_sport),
        stringResource(id = R.string.filter_birthday),
        stringResource(id = R.string.filter_music),
        stringResource(id = R.string.filter_food),
        stringResource(id = R.string.filter_travel),
        stringResource(id = R.string.filter_others)
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters) { filter ->
            FilterItem(filter)
        }
    }
}

@Composable
fun FilterItem(filter: String) {
    Surface(
        shape = CircleShape,
        color = Color(0XFF4A5182).copy(alpha = 0.1f),
        modifier = Modifier.height(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = filter, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun NonEmptyEventsIllustration(
    state: NoteState,
    viewModel: NotesViewModel,
    navController: NavController,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(state.notes.size) { index ->
            NoteItem(
                state = state,
                index = index,
                onEvent = viewModel::onEvent
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
            modifier = Modifier.size(150.dp)
        )
    }
}

@Composable
fun NoteItem(
    state: NoteState,
    index: Int,
    onEvent: (NotesEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = state.notes[index].title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0XFF4A5182)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.notes[index].description,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.notes[index].eventDate,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = state.notes[index].eventTime,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = state.notes[index].location,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        IconButton(onClick = {
            onEvent(NotesEvent.DeleteNote(state.notes[index]))
        }) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(id = R.string.delete_note),
                modifier = Modifier.size(35.dp),
                tint = Color(0XFF4A5182)
            )
        }
    }
}

@Composable
fun FloatingCreateEventButton(onCreateEventNavigation: () -> Unit) {
    FloatingActionButton(
        onClick = onCreateEventNavigation,
        backgroundColor = Color(0XFF4A5182),
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.fab_create_event),
            tint = Color.White
        )
    }
}

@Composable
fun EventMateBottomNavigation(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    BottomAppBar(
        cutoutShape = CircleShape,
        backgroundColor = Color(0XFF4A5182)
    ) {
        BottomNavigationItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                navController.navigate("home")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.nav_home),
                    tint = Color.White
                )
            },
            label = { Text("Home") }
        )
        BottomNavigationItem(
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(id = R.string.nav_map),
                    tint = Color.White
                )
            },
            label = { Text(stringResource(id = R.string.nav_map)) }
        )
        Spacer(Modifier.weight(1f, true))
        BottomNavigationItem(
            selected = selectedItem == 2,
            onClick = { selectedItem = 2 },
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(id = R.string.nav_love),
                    tint = Color.White
                )
            },
            label = { Text(stringResource(id = R.string.nav_love)) }
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
                    contentDescription = stringResource(id = R.string.nav_profile),
                    tint = Color.White
                )
            },
            label = { Text(stringResource(id = R.string.nav_profile)) }
        )
    }
}
