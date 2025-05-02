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
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.eventymate.data.Note
import com.example.eventymate.presentation.NoteState
import com.example.eventymate.presentation.NotesEvent
import com.example.eventymate.presentation.NotesViewModel
import com.example.eventymate.screens.eventadd.CategorySelector

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
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        topBar = { TopBarSection() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                //.verticalScroll(rememberScrollState())
        ) {

            FiltersSection(
                state = state, viewModel = viewModel, navController = navController
            )
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
fun TopBarSection(
    userName: String = "Rawan Hassan",
    onLanguageClick: () -> Unit = {},
    onThemeToggle: () -> Unit = {}
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
                text = "Welcome Back ✨",
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
                text = "EN",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onLanguageClick() }
                    .padding(end = 12.dp)
            )
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Toggle Theme",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onThemeToggle() }
            )
        }


    }
}

@Composable
fun FiltersSection(state : NoteState, viewModel : NotesViewModel, navController: NavController) {
    CategorySelector(
        categories = listOf("All","Work", "Education", "Personal", "Sport", "Birthday", "Travel", "Other"),
        initialSelected = "All"
    ) { selected ->
        viewModel.onEvent(NotesEvent.SelectCategory(selected))
    }
}

@Composable
fun FilterItem(filter: String) {
    Surface(
        shape = CircleShape,
        color = Color(0XFF4A5182).copy(alpha = 0.1f),
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
    val filteredNotes = if (state.category == "All" || state.category.isBlank()) {
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
    note: Note,
    onEvent: (NotesEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp)
    ){
        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(10.dp))
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0XFF4A5182)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = note.description,
                    fontSize = 18.sp,
                    color = Color.Black
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
                    tint = Color(0XFF4A5182)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "\uD83D\uDCC5 ${note.eventDate}",
                fontSize = 16.sp,
                color = Color(0xff000000),
                modifier = Modifier.weight(2f)

            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = note.eventTime,
                fontSize = 16.sp,
                color = Color(0xff000000),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "\uD83D\uDCCD${note.location}",
                fontSize = 16.sp,
                color = Color(0xff000000),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = note.category,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Blue
        )
    }
}


@Composable
fun FloatingCreateEventButton(onCreateEventNavigation: () -> Unit) {
    FloatingActionButton(
        onClick = {
            onCreateEventNavigation()
        },
        backgroundColor =Color(0XFF4A5182) ,
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
        backgroundColor = Color(0XFF4A5182)
    ) {
        BottomNavigationItem(
            selected = selectedItem == 0,
            onClick = { selectedItem = 0
                navController.navigate("home")
                      },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
            label = { Text("Home") }
        )
        BottomNavigationItem(
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 },
            icon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Map", tint = Color.White) },
            label = { Text("Map") }
        )
        Spacer(Modifier.weight(1f, true))
        BottomNavigationItem(
            selected = selectedItem == 2,
            onClick = { selectedItem = 2 },
            icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Love", tint = Color.White) },
            label = { Text("Love") }
        )
        BottomNavigationItem(
            selected = selectedItem == 3,
            onClick = { selectedItem = 3
                navController.navigate("profile")
                      },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color.White) },
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







