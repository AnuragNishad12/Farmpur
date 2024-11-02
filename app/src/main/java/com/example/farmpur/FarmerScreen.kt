package com.example.farmpur

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.farmpur.Farmer.EducationScreen
import com.example.farmpur.Farmer.FarmRegisterScreen
import com.example.farmpur.Farmer.GovSchemeScreen
import com.example.farmpur.Farmer.ProfileScreen


sealed class BottomNavItem(val route: String, val label: String, @DrawableRes val iconResId: Int) {
    object Education : BottomNavItem("education", "Education", R.drawable.education)
    object GovScheme : BottomNavItem("government_scheme", "Gov Scheme", R.drawable.governement)
    object FarmRegister : BottomNavItem("farm_register", "Register Farm", R.drawable.farm)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.user)
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Education,
        BottomNavItem.GovScheme,
        BottomNavItem.FarmRegister,
        BottomNavItem.Profile
    )


    NavigationBar(
        modifier = Modifier.padding(8.dp),
        containerColor = Color(0xFFE3F2FD),
        tonalElevation = 8.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            val iconTint by animateColorAsState(if (selected) Color(0xFF1976D2) else Color(0xFFB0BEC5)) // Deep blue for selected, gray for unselected
            val labelColor by animateColorAsState(if (selected) Color(0xFF1976D2) else Color.Gray)

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                        tint = iconTint,
                        modifier = Modifier.padding(4.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = labelColor,
                        style = MaterialTheme.typography.labelSmall // Use a smaller label style
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1976D2),
                    unselectedIconColor = Color(0xFFB0BEC5),
                    selectedTextColor = Color(0xFF1976D2),
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun FarmerScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Education.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Education.route) { EducationScreen() }
            composable(BottomNavItem.GovScheme.route) { GovSchemeScreen() }
            composable(BottomNavItem.FarmRegister.route) { FarmRegisterScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}
