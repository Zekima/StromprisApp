
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stromprisapp.ui.GraphScreen
import com.example.stromprisapp.ui.HomeScreen
import com.example.stromprisapp.ui.SettingsScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(start = 8.dp)
                .padding(end = 8.dp)
                .padding(bottom = 4.dp))
        {
            composable("home") { HomeScreen() }
            composable("graph") { GraphScreen() }
            composable("settings") { SettingsScreen() }
        }

        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavigationBarItem(
                selected = currentRoute == "home",
                label = { Text("Hjem") },
                onClick = { navController.navigate("home") },
                icon = { Icon(Icons.Filled.Home, contentDescription = "Hjem") })
            NavigationBarItem(
                selected = currentRoute == "graph",
                label = { Text("Graf") },
                onClick = { navController.navigate("graph") },
                icon = { Icon(Icons.Filled.Star, contentDescription = "Graf") })
            NavigationBarItem(
                selected = currentRoute == "settings",
                label = { Text("Innstillinger") },
                onClick = { navController.navigate("settings") },
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Innstillinger") })
        }
    }
}

