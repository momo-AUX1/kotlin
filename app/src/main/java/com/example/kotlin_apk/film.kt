package com.example.kotlin_apk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.kotlin_apk.ui.theme.KotlinapkTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel()

        setContent {
            KotlinapkTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "films",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("films") { FilmScreen(viewModel) }
            composable("test") { TestScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Films") },
            label = { Text("Films") },
            selected = currentRoute == "films",
            onClick = { navController.navigate("films") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Test") },
            label = { Text("Test") },
            selected = currentRoute == "test",
            onClick = { navController.navigate("test") }
        )
    }
}

@Composable
fun FilmScreen(viewModel: MainViewModel) {
    val movies by viewModel.movies.collectAsState()

    if (movies.isEmpty()) {
        viewModel.getFilmsInitiaux("e4009b8963dbfe389c28cb3b4d0c309e")
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(12.dp)
    ) {
        items(movies.size) { index ->
            val movie = movies[index]
            Column(modifier = Modifier.padding(8.dp)) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w300${movie.poster_path}",
                    contentDescription = "Image du film ${movie.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Text(
                    text = movie.title,
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = movie.release_date,
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                )
            }
        }
    }
}

@Composable
fun TestScreen() {
    Text("Test", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
}

class MainViewModel {
    val movies = MutableStateFlow<List<TmdbMovie>>(emptyList())

    fun getFilmsInitiaux(apiKey: String) {
        movies.value = listOf(
            TmdbMovie("T 1", "2023-01-01", "/poster1.jpg"),
            TmdbMovie("T 2", "2023-02-01", "/poster2.jpg"),
            TmdbMovie("T 3", "2023-03-01", "/poster3.jpg")
        )
    }
}


data class TmdbMovie(
    val title: String,
    val release_date: String,
    val poster_path: String
)