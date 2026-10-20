package com.example.data.templates

import com.example.data.model.AppTemplate

object AppTemplates {
    val list = listOf(
        AppTemplate(
            id = "template_ecommerce",
            title = "ApexShop E-Commerce",
            category = "E-Commerce",
            description = "Complete shopping app with product catalog, category filters, interactive cart, checkout summary, and M3 design.",
            prompt = "Build a modern E-Commerce Shopping App with product cards, category filters, cart management, and price calculation.",
            previewComponentType = "cart",
            iconName = "shopping_bag",
            initialFiles = mapOf(
                "ShopScreen.kt" to """
package com.example.shop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShopScreen(viewModel: ShopViewModel = remember { ShopViewModel() }) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("ApexShop Studio") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(state.products) { product ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text(product.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text("${'$'}99.99", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { viewModel.addToCart(product) }) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}
""".trimIndent(),
                "ShopViewModel.kt" to """
package com.example.shop.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Product(val id: String, val name: String, val price: Double)
data class ShopUiState(val products: List<Product> = emptyList(), val cartCount: Int = 0)

class ShopViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ShopUiState(products = listOf(
            Product("1", "Cyber Headphones X1", 129.99),
            Product("2", "Aura Smart Watch Pro", 199.99),
            Product("3", "Neo Mechanical Keyboard", 89.99)
        ))
    )
    val uiState = _uiState.asStateFlow()

    fun addToCart(product: Product) {
        _uiState.value = _uiState.value.copy(cartCount = _uiState.value.cartCount + 1)
    }
}
""".trimIndent()
            )
        ),
        AppTemplate(
            id = "template_fitness",
            title = "FitPulse Step & Workout",
            category = "Health & Fitness",
            description = "Workout logging, daily step counter ring, calorie metrics, and weekly progress charts.",
            prompt = "Build a Fitness Tracker app with daily step goal progress ring, calorie burn estimates, and workout routine logging.",
            previewComponentType = "counter",
            iconName = "fitness_center",
            initialFiles = mapOf(
                "FitScreen.kt" to """
package com.example.fit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FitScreen(viewModel: FitViewModel = remember { FitViewModel() }) {
    val steps by viewModel.steps.collectAsState()
    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("FitPulse Tracker", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(progress = { 0.75f }, modifier = Modifier.size(140.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("7,500 / 10,000 Steps", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { viewModel.addSteps(500) }) {
                Text("+500 Steps Walked")
            }
        }
    }
}
""".trimIndent()
            )
        ),
        AppTemplate(
            id = "template_taskmaster",
            title = "TaskMaster Kanban & Todo",
            category = "Productivity",
            description = "Priority task planner with category tags, checkable items, filter tabs, and progress tracking.",
            prompt = "Build a Task & Project Manager app with checkable todos, urgent priority badges, and category tabs.",
            previewComponentType = "todo",
            iconName = "checklist",
            initialFiles = mapOf(
                "TaskScreen.kt" to """
package com.example.tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskScreen(viewModel: TaskViewModel = remember { TaskViewModel() }) {
    val tasks by viewModel.tasks.collectAsState()
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("TaskMaster AI") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("New task") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { if (input.isNotBlank()) { viewModel.addTask(input); input = "" } }) {
                    Text("Add")
                }
            }
            LazyColumn {
                items(tasks) { task ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Checkbox(checked = task.done, onCheckedChange = { viewModel.toggle(task.id) })
                            Text(task.title, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
""".trimIndent()
            )
        ),
        AppTemplate(
            id = "template_weather",
            title = "SkyCast Live Weather Radar",
            category = "Weather & Travel",
            description = "Weather forecast app with hourly temperatures, humidity and wind telemetry, and atmospheric cards.",
            prompt = "Build a beautiful SkyCast Weather app with current temperature, 5-day forecast, humidity indicators, and atmospheric gradients.",
            previewComponentType = "weather",
            iconName = "cloud",
            initialFiles = mapOf(
                "WeatherScreen.kt" to """
package com.example.weather.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Colombo, Sri Lanka", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("29°C", style = MaterialTheme.typography.displayLarge)
            Text("Partly Cloudy • Humidity 74%", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Wind")
                        Text("14 km/h", style = MaterialTheme.typography.titleMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("UV Index")
                        Text("6 High", style = MaterialTheme.typography.titleMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Rain")
                        Text("20%", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
""".trimIndent()
            )
        )
    )
}
