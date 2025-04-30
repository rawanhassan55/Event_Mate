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

package com.example.eventymate

import androidx.navigation.compose.rememberNavController



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesEvent
import com.example.eventymate.presentation.NotesViewModel

@Composable
fun HomeScreen(navController: NavController,onCreateEventNavigation: () -> Unit
               , state : NoteState,
               viewModel : NotesViewModel) {
    Scaffold(
        bottomBar = { EventMateBottomNavigation(navController) },
        floatingActionButton = {
            FloatingCreateEventButton(
                onCreateEventNavigation = {
                    navController.navigate("createEvent")
                }
            )
        },
        isFloatingActionButtonDocked = true,
        topBar = { TopBarSection() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                //.verticalScroll(rememberScrollState())
        ) {
            WelcomeSection()
            FiltersSection()
            if (state.notes.isEmpty()){
                EmptyEventsIllustration()
            }else {
                NonEmptyEventsIllustration(
                    state = state, viewModel = viewModel, navController = navController
                )
            }
        }
    }
}

@Composable
fun TopBarSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.wb_sunny),
            contentDescription = "Theme Icon",
            modifier = Modifier.size(24.dp)
        )

        /*
 Button(onClick = { onSettingNavigation() }) {
            Text(text = "Settings")
        }
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(onClick = {onSignOut() }) {
            Icon(Icons.Filled.ExitToApp, contentDescription = "Localized description")
        }
    }
         */
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "EN",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(onClick = { /*onSettingNavigation()*/}) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }

        }
    }
}

@Composable
fun WelcomeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome Back ✨",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Rawan Hassan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Cairo, Egypt",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun FiltersSection() {
    val filters = listOf("All", "Sport", "Birthday", "Music", "Food", "Travel","Others")
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
        color = MaterialTheme.colors.primary.copy(alpha = 0.2f),
        modifier = Modifier
            .height(40.dp)
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
fun NonEmptyEventsIllustration(state : NoteState, viewModel : NotesViewModel, navController: NavController) {
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
            contentDescription = "No Events",
            modifier = Modifier
                .size(150.dp)
        )

    }
}


@Composable
fun NoteItem(
    state: NoteState,
    index: Int,
    onEvent: (NotesEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = state.notes[index].title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.notes[index].description,
                fontSize = 18.sp,
                color = MaterialTheme.colors.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.notes[index].eventDate,
                    fontSize = 16.sp,
                    color = Color(0xff000000),
                    modifier = Modifier.weight(1f)

                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = state.notes[index].eventTime,
                    fontSize = 16.sp,
                    color = Color(0xff000000),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = state.notes[index].location,
                    fontSize = 16.sp,
                    color = Color(0xff000000),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        IconButton(
            onClick = {
                onEvent(NotesEvent.DeleteNote(state.notes[index]))
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete Note",
                modifier = Modifier.size(35.dp),
                tint = MaterialTheme.colors.primary
            )

        }

    }
}


@Composable
fun FloatingCreateEventButton(onCreateEventNavigation: () -> Unit) {
    FloatingActionButton(
        onClick = {
            onCreateEventNavigation()
        },
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(8.dp)
        //.align(Alignment.BottomCenter)


    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Create Event", tint = Color.White)
    }
}

@Composable
fun EventMateBottomNavigation(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    BottomAppBar(
        cutoutShape = CircleShape,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        BottomNavigationItem(
            selected = selectedItem == 0,
            onClick = { selectedItem = 0 },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        BottomNavigationItem(
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 },
            icon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Map") },
            label = { Text("Map") }
        )
        Spacer(Modifier.weight(1f, true))
        BottomNavigationItem(
            selected = selectedItem == 2,
            onClick = { selectedItem = 2 },
            icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Love") },
            label = { Text("Love") }
        )
        BottomNavigationItem(
            selected = selectedItem == 3,
            onClick = { selectedItem = 3 },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navController = rememberNavController(),
        onCreateEventNavigation = {},
        state = TODO(),
        viewModel = TODO(),
    )
}







