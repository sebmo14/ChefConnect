package com.example.chefconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.chefconnect.data.model.MealState
import com.example.chefconnect.ui.viewmodel.FavoritesViewModel
import com.example.chefconnect.ui.viewmodel.MealDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    mealId: String,
    onBackClick: () -> Unit,
    detailViewModel: MealDetailViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModel.Factory(LocalContext.current)
    )
) {
    LaunchedEffect(mealId) { detailViewModel.loadMeal(mealId) }

    val state by detailViewModel.state.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val isFavorite = mealId in favoriteIds

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("ChefConnect", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            if (state is MealState.Success) {
                val meal = (state as MealState.Success).data
                FloatingActionButton(
                    onClick = { favoritesViewModel.toggleFavorite(meal.id, meal.name) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        "Favorite", modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { padding ->
        when (val s = state) {
            is MealState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is MealState.Error -> {
                Box(Modifier.padding(padding)) {
                    ErrorContent(s.message) { detailViewModel.loadMeal(mealId) }
                }
            }
            is MealState.Success -> {
                val meal = s.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                ) {
                    // Hero image
                    Box(Modifier.fillMaxWidth().height(320.dp)) {
                        AsyncImage(
                            model = meal.thumbnail,
                            contentDescription = meal.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            Modifier.fillMaxSize().background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(0.2f), Color.Black.copy(0.7f))
                                )
                            )
                        )
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            meal.tags?.split(",")?.filter { it.isNotBlank() }?.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        tag.trim().uppercase(),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                            Text(meal.name, style = MaterialTheme.typography.headlineLarge, color = Color.White)
                            if (!meal.area.isNullOrBlank()) {
                                Text("${meal.area} Cuisine", style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(0.8f))
                            }
                        }
                    }

                    // Content
                    Column(
                        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        // Info card
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("MEAL ID", style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)
                                    Text(meal.id, style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary)
                                }
                                meal.category?.let {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerLowest, shadowElevation = 1.dp
                                    ) {
                                        Text(it, Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Ingredients
                        val ingredients = meal.getIngredientPairs()
                        if (ingredients.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Ingredients", style = MaterialTheme.typography.headlineSmall)
                                ingredients.forEach { (name, measure) ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                                        )
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                            Surface(
                                                Modifier.size(44.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.secondaryContainer
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Filled.Check, null,
                                                        Modifier.size(22.dp), MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                            Column {
                                                Text(name, style = MaterialTheme.typography.labelLarge)
                                                Text(measure, style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Instructions
                        if (!meal.instructions.isNullOrBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Instructions", style = MaterialTheme.typography.headlineSmall)
                                Text(
                                    meal.instructions,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}
