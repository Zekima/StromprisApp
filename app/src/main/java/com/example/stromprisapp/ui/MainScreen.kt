package com.example.stromprisapp.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stromprisapp.ui.Global.sharedPrefEur
import com.example.stromprisapp.ui.Global.sharedPrefNOK
import com.example.stromprisapp.ui.Global.sharedPrefSone
import com.example.stromprisapp.ui.Global.valgtSone
import com.example.stromprisapp.ui.Global.valutaEUR
import com.example.stromprisapp.ui.Global.valutaNOK
import com.example.stromprisapp.ui.graph.GraphScreen
import com.example.stromprisapp.ui.theme.StromprisAppTheme

@Composable
fun MainScreen() {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val currentSone = LocalContext.current
    sharedPrefSone = currentSone.getSharedPreferences("minPrefSone", Context.MODE_PRIVATE)
    valgtSone = sharedPrefSone.getString("valgtSone", Global.valgtSone).toString()

    val currentEur = LocalContext.current
    sharedPrefEur = currentEur.getSharedPreferences("minPrefValuta", Context.MODE_PRIVATE)
    valutaEUR = sharedPrefEur.getBoolean("valutaEUR", false)

    val currentNOK = LocalContext.current
    sharedPrefNOK = currentNOK.getSharedPreferences("minPrefValuta", Context.MODE_PRIVATE)
    valutaNOK = sharedPrefNOK.getBoolean("valutaNOK", false)

    val startDestination = if(valgtSone == "velg sone") {
        "settings"
    } else {
        "home"
    }

    StromprisAppTheme {
        if (isLandscape) { //For horisontal view
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Left side: NavigationBar
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomStart
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(125.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier.padding(top = 50.dp, bottom = 50.dp),
                        ) {
                            this@NavigationBar.NavigationBarItem(
                                selected = currentRoute == "home",
                                label = { Text("Hjem") },
                                onClick = { navController.navigate("home") },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Hjem") }
                            )
                            this@NavigationBar.NavigationBarItem(
                                selected = currentRoute == "graph",
                                label = { Text("Graf") },
                                onClick = { navController.navigate("graph") },
                                icon = { Icon(Icons.Filled.Star, contentDescription = "Graf") }
                            )
                            this@NavigationBar.NavigationBarItem(
                                selected = currentRoute == "settings",
                                label = { Text("Innstillinger") },
                                onClick = { navController.navigate("settings") },
                                icon = { Icon(Icons.Filled.Settings, contentDescription = "Innstillinger") }
                            )
                        }
                    }
                }
                // Right side: Content
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    getNavHost(navController, startDestination)
                }
            }
        } else {
            // Default view i "loddrett" modus
            Box(
                modifier = Modifier.fillMaxSize()

            ) {
                getNavHost(navController, startDestination)

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
    }
}
@Composable
fun getNavHost(navController: NavHostController, startDestination : String) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(start = 10.dp)
            .padding(end = 10.dp)
            .padding(bottom = 4.dp)
    ) {
        composable("home") { HomeScreen() }
        composable("graph") { GraphScreen(navController) }
        composable("settings") { SettingsScreen() }
    }
}
