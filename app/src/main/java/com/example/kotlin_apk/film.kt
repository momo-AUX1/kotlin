package com.example.kotlin_apk

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.*
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.example.kotlin_apk.ui.theme.KotlinapkTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalFoundationApi::class)
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
            composable("films") { FilmScreen(viewModel, navController) }
            composable("series") { SeriesScreen(viewModel, navController) }
            composable("acteurs") { ActorsScreen(viewModel, navController) }
            composable("favoris") { FavoritesScreen(viewModel, navController) }
            composable("filmDetail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                if (movieId != null) {
                    FilmDetailScreen(movieId, viewModel, navController)
                } else {
                    Text("Movie ID pas trouvé")
                }
            }
            composable("serieDetail/{seriesId}") { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getString("seriesId")?.toIntOrNull()
                if (seriesId != null) {
                    SeriesDetailScreen(seriesId, viewModel, navController)
                } else {
                    Text("Series ID pas trouvé")
                }
            }
            composable("actorDetail/{actorId}") { backStackEntry ->
                val actorId = backStackEntry.arguments?.getString("actorId")?.toIntOrNull()
                if (actorId != null) {
                    ActorDetailScreen(actorId, viewModel)
                } else {
                    Text("Actor ID pas trouvé")
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "Films") },
            label = { Text("Films") },
            selected = currentRoute == "films",
            onClick = { navController.navigate("films") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Series") },
            label = { Text("Series") },
            selected = currentRoute == "series",
            onClick = { navController.navigate("series") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Acteurs") },
            label = { Text("Acteurs") },
            selected = currentRoute == "acteurs",
            onClick = { navController.navigate("acteurs") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoris") },
            label = { Text("Favoris") },
            selected = currentRoute == "favoris",
            onClick = { navController.navigate("favoris") }
        )
    }
}

@Composable
fun FilmScreen(viewModel: MainViewModel, navController: NavHostController) {
    val movies by viewModel.movies.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }


    Column {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (!isLandscape) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchMovies(key, searchQuery)
                },
                label = { Text("recherche films") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                FloatingActionButton(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }

            if (showDialog) {
                SearchDialog(
                    initialText = searchQuery,
                    onDismissRequest = { showDialog = false },
                    onSearch = { query ->
                        searchQuery = query
                        viewModel.searchMovies(key, searchQuery)
                        showDialog = false
                    }
                )
            }
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(12.dp)
        ) {
            items(movies) { movie ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("filmDetail/${movie.id}")
                        }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w300${movie.poster_path}",
                        contentDescription = "Image de ${movie.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = movie.release_date ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SeriesScreen(viewModel: MainViewModel, navController: NavHostController) {
    val seriesList by viewModel.series.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }


    Column {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (!isLandscape) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchSeries(key, searchQuery)
                },
                label = { Text("recherche Series") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                FloatingActionButton(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
            if (showDialog) {
                SearchDialog(
                    initialText = searchQuery,
                    onDismissRequest = { showDialog = false },
                    onSearch = { query ->
                        searchQuery = query
                        viewModel.searchSeries(key, searchQuery)
                        showDialog = false
                    }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(12.dp)
        ) {
            items(seriesList) { series ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("serieDetail/${series.id}")
                        }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w300${series.poster_path}",
                        contentDescription = "Image de ${series.name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = series.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = series.first_air_date ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActorsScreen(viewModel: MainViewModel, navController: NavHostController) {
    val actorsList by viewModel.actors.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }


    Column {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (!isLandscape) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchActors(key, searchQuery)
                },
                label = { Text("Recherche Acteurs") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                FloatingActionButton(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
            if (showDialog) {
                SearchDialog(
                    initialText = searchQuery,
                    onDismissRequest = { showDialog = false },
                    onSearch = { query ->
                        searchQuery = query
                        viewModel.searchActors(key, searchQuery)
                        showDialog = false
                    }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(12.dp)
        ) {
            items(actorsList) { actor ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("actorDetail/${actor.id}")
                        }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w300${actor.profile_path}",
                        contentDescription = "Image de ${actor.name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = actor.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(viewModel: MainViewModel, navController: NavHostController) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()
    val favoriteSeries by viewModel.favoriteSeries.collectAsState()
    val favoriteActors by viewModel.favoriteActors.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Films Favoris:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow {
            items(favoriteMovies) { movie ->
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            navController.navigate("filmDetail/${movie.id}")
                        }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w185${movie.poster_path}",
                        contentDescription = "Favorite Movie ${movie.title}",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Series Favoris:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow {
            items(favoriteSeries) { series ->
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            navController.navigate("serieDetail/${series.id}")
                        }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w185${series.poster_path}",
                        contentDescription = "Favorite Series ${series.name}",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = series.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Acteurs Favoris:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow {
            items(favoriteActors) { actor ->
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            navController.navigate("actorDetail/${actor.id}")
                        }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w185${actor.profile_path}",
                        contentDescription = "Favorite Actor ${actor.name}",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = actor.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun FilmDetailScreen(movieId: Int, viewModel: MainViewModel, navController: NavController) {
    val movieDetails by viewModel.movieDetails.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId, key)
    }

    if (movieDetails != null) {
        val movie = movieDetails!!
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.backdrop_path ?: movie.poster_path}",
                contentDescription = "Poster de ${movie.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    Toast.makeText(context, "En train d'ajouter aux favoris...", Toast.LENGTH_SHORT).show()
                    viewModel.toggleFavoriteMovie(movie)
                    Toast.makeText(context, "Ajouté!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = if (viewModel.isFavoriteMovie(movie)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Favorite"
                    )
                }
            }
            Text(
                text = "Release Date: ${movie.release_date ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = movie.overview ?: "Pas d'info.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cast:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow {
                items(movie.credits?.cast ?: emptyList()) { castMember ->
                    Column(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(100.dp)
                            .clickable {
                                navController.navigate("actorDetail/${castMember.id}")
                            }
                    ) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w185${castMember.profile_path}",
                            contentDescription = "Acteur ${castMember.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = castMember.name,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun SearchDialog(
    initialText: String,
    onDismissRequest: () -> Unit,
    onSearch: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Recherche") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Recherche ici") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onSearch(text) }) {
                Text("Rechercher")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Anuller")
            }
        }
    )
}

@Composable
fun SeriesDetailScreen(seriesId: Int, viewModel: MainViewModel, navController: NavController) {
    val seriesDetails by viewModel.seriesDetails.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(seriesId) {
        viewModel.getSeriesDetails(seriesId, key)
    }

    if (seriesDetails != null) {
        val series = seriesDetails!!
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${series.backdrop_path ?: series.poster_path}",
                contentDescription = "Poster de ${series.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = series.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    Toast.makeText(context, "En train d'ajouter aux favoris...", Toast.LENGTH_SHORT).show()
                    viewModel.toggleFavoriteSeries(series)
                    Toast.makeText(context, "Ajouté!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = if (viewModel.isFavoriteSeries(series)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Favorite"
                    )
                }
            }
            Text(
                text = "Sortie: ${series.first_air_date ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = series.overview ?: "Pas d'info.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cast:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow {
                items(series.credits?.cast ?: emptyList()) { castMember ->
                    Column(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .width(100.dp)
                            .clickable {
                                navController.navigate("actorDetail/${castMember.id}")
                            }
                    ) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w185${castMember.profile_path}",
                            contentDescription = "Acteur ${castMember.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = castMember.name,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ActorDetailScreen(actorId: Int, viewModel: MainViewModel) {
    val actorDetails by viewModel.actorDetails.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(actorId) {
        viewModel.getActorDetails(actorId, key)
    }

    if (actorDetails != null) {
        val actor = actorDetails!!
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${actor.profile_path}",
                contentDescription = "Photo of ${actor.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = actor.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    Toast.makeText(context, "En train d'ajouter aux favoris...", Toast.LENGTH_SHORT).show()
                    viewModel.toggleFavoriteActor(actor)
                    Toast.makeText(context, "Ajouté!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = if (viewModel.isFavoriteActor(actor)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Favorite"
                    )
                }
            }
            Text(
                text = "Connu pour: ${actor.known_for_department ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Biographie:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = actor.biography ?: "No biography available.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

class MainViewModel : ViewModel() {
    val movies = MutableStateFlow<List<TmdbMovie>>(emptyList())
    val series = MutableStateFlow<List<TmdbSeries>>(emptyList())
    val actors = MutableStateFlow<List<TmdbActor>>(emptyList())
    val movieDetails = MutableStateFlow<TmdbMovieDetailResponse?>(null)
    val seriesDetails = MutableStateFlow<TmdbSeriesDetailResponse?>(null)
    val actorDetails = MutableStateFlow<TmdbActorDetailResponse?>(null)

    val favoriteMovies = MutableStateFlow<List<TmdbMovie>>(emptyList())
    val favoriteSeries = MutableStateFlow<List<TmdbSeries>>(emptyList())
    val favoriteActors = MutableStateFlow<List<TmdbActor>>(emptyList())

    init {
        getFilmsInitiaux(key)
        getSeriesInitiaux(key)
        getActorsInitiaux(key)
    }

    fun getFilmsInitiaux(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTrendingMovies(apiKey)
                movies.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSeriesInitiaux(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTrendingSeries(apiKey)
                series.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getActorsInitiaux(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTrendingActors(apiKey)
                actors.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getMovieDetails(movieId: Int, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMovieDetails(movieId, apiKey)
                movieDetails.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSeriesDetails(seriesId: Int, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getSeriesDetails(seriesId, apiKey)
                seriesDetails.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getActorDetails(actorId: Int, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getActorDetails(actorId, apiKey)
                actorDetails.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchMovies(apiKey: String, query: String) {
        viewModelScope.launch {
            try {
                val response = if (query.isBlank()) {
                    RetrofitInstance.api.getTrendingMovies(apiKey)
                } else {
                    RetrofitInstance.api.searchMovies(apiKey, query)
                }
                movies.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchSeries(apiKey: String, query: String) {
        viewModelScope.launch {
            try {
                val response = if (query.isBlank()) {
                    RetrofitInstance.api.getTrendingSeries(apiKey)
                } else {
                    RetrofitInstance.api.searchSeries(apiKey, query)
                }
                series.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchActors(apiKey: String, query: String) {
        viewModelScope.launch {
            try {
                val response = if (query.isBlank()) {
                    RetrofitInstance.api.getTrendingActors(apiKey)
                } else {
                    RetrofitInstance.api.searchActors(apiKey, query)
                }
                actors.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavoriteMovie(movie: TmdbMovieDetailResponse) {
        val currentFavorites = favoriteMovies.value.toMutableList()
        if (currentFavorites.any { it.id == movie.id }) {
            currentFavorites.removeAll { it.id == movie.id }
        } else {
            currentFavorites.add(
                TmdbMovie(
                    adult = false,
                    backdrop_path = movie.backdrop_path,
                    genre_ids = emptyList(),
                    id = movie.id,
                    original_language = null,
                    original_title = null,
                    overview = movie.overview,
                    popularity = 0.0,
                    poster_path = movie.poster_path,
                    release_date = movie.release_date,
                    title = movie.title,
                    video = false,
                    vote_average = 0.0,
                    vote_count = 0
                )
            )
        }
        favoriteMovies.value = currentFavorites
    }

    fun isFavoriteMovie(movie: TmdbMovieDetailResponse): Boolean {
        return favoriteMovies.value.any { it.id == movie.id }
    }

    fun toggleFavoriteSeries(series: TmdbSeriesDetailResponse) {
        val currentFavorites = favoriteSeries.value.toMutableList()
        if (currentFavorites.any { it.id == series.id }) {
            currentFavorites.removeAll { it.id == series.id }
        } else {
            currentFavorites.add(
                TmdbSeries(
                    id = series.id,
                    name = series.name,
                    original_name = series.name,
                    overview = series.overview,
                    poster_path = series.poster_path,
                    backdrop_path = series.backdrop_path,
                    first_air_date = series.first_air_date,
                    origin_country = emptyList(),
                    genre_ids = emptyList(),
                    original_language = null,
                    popularity = 0.0,
                    vote_average = 0.0,
                    vote_count = 0
                )
            )
        }
        favoriteSeries.value = currentFavorites
    }

    fun isFavoriteSeries(series: TmdbSeriesDetailResponse): Boolean {
        return favoriteSeries.value.any { it.id == series.id }
    }

    fun toggleFavoriteActor(actor: TmdbActorDetailResponse) {
        val currentFavorites = favoriteActors.value.toMutableList()
        if (currentFavorites.any { it.id == actor.id }) {
            currentFavorites.removeAll { it.id == actor.id }
        } else {
            currentFavorites.add(
                TmdbActor(
                    id = actor.id,
                    name = actor.name,
                    profile_path = actor.profile_path,
                    known_for_department = actor.known_for_department,
                    popularity = 0.0
                )
            )
        }
        favoriteActors.value = currentFavorites
    }

    fun isFavoriteActor(actor: TmdbActorDetailResponse): Boolean {
        return favoriteActors.value.any { it.id == actor.id }
    }
}
