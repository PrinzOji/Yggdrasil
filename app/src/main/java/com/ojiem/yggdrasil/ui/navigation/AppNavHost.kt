package com.ojiem.yggdrasil.ui.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ojiem.yggdrasil.ui.screens.analytics.AnalyticsScreen
import com.ojiem.yggdrasil.ui.screens.finance.FinanceScreen
import com.ojiem.yggdrasil.ui.screens.inventory.InventoryScreen
import com.ojiem.yggdrasil.ui.screens.library.LibraryScreen
import com.ojiem.yggdrasil.ui.screens.realms.RealmsScreen
import com.ojiem.yggdrasil.ui.screens.dashboard.DashScreen
import com.ojiem.yggdrasil.ui.screens.settings.SettingsScreen
import com.ojiem.yggdrasil.ui.screens.auth.AuthScreen
import com.ojiem.yggdrasil.ui.screens.detail.PriceDetailScreen
import com.ojiem.yggdrasil.ui.screens.home.HomeFeedScreen
import com.ojiem.yggdrasil.ui.screens.leaderboard.LeaderboardScreen
import com.ojiem.yggdrasil.ui.screens.profile.ProfileScreen
import com.ojiem.yggdrasil.ui.screens.report.ReportPriceScreen
import com.ojiem.yggdrasil.ui.screens.splash.SplashScreen
import com.ojiem.yggdrasil.ui.theme.glassmorphism
import com.ojiem.yggdrasil.ui.theme.NatureMint

data class BottomDestination(val route: String, val label: String, val icon: ImageVector)

val bottomDestinations = listOf(
    BottomDestination(ROUTE_HOME, "Yggdrasil", Icons.Filled.Home),
    BottomDestination(ROUTE_LIBRARY, "Library", Icons.Filled.Book),
    BottomDestination(ROUTE_LEADERBOARD, "Seers", Icons.Filled.EmojiEvents),
    BottomDestination(ROUTE_DASH, "Root", Icons.Filled.Person),
)

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf(ROUTE_HOME, ROUTE_REPORT, ROUTE_LEADERBOARD, ROUTE_DASH, ROUTE_PROFILE, ROUTE_LIBRARY, ROUTE_ANALYTICS, ROUTE_FINANCE, ROUTE_INVENTORY, ROUTE_REALMS)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                        .glassmorphism(RoundedCornerShape(32.dp)),
                    color = Color.Transparent
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        bottomDestinations.forEach { dest ->
                            val selected = backStackEntry?.destination?.hierarchy?.any { it.route == dest.route } == true
                            
                            val iconScale by animateFloatAsState(
                                targetValue = if (selected) 1.2f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "iconScale"
                            )

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { 
                                    Icon(
                                        dest.icon, 
                                        contentDescription = dest.label,
                                        modifier = Modifier.scale(iconScale)
                                    ) 
                                },
                                label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NatureMint,
                                    selectedTextColor = NatureMint,
                                    unselectedIconColor = Color.White.copy(alpha = 0.4f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.4f),
                                    indicatorColor = NatureMint.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(padding)
        ) {
            composable(ROUTE_SPLASH) { SplashScreen(navController) }
            composable(ROUTE_AUTH) { AuthScreen(navController) }
            composable(ROUTE_HOME) {
                HomeFeedScreen(
                    onReportClick = { id ->
                        navController.navigate(createDetailRoute(id))
                    },
                    onRealmsClick = {
                        navController.navigate(ROUTE_REALMS)
                    }
                )
            }
            composable(ROUTE_REPORT) {
                ReportPriceScreen(onSubmitted = {
                    navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_HOME) { inclusive = true } }
                })
            }
            composable(ROUTE_ADD_PRODUCT) {
                ReportPriceScreen(onSubmitted = {
                    navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_HOME) { inclusive = true } }
                })
            }
            composable(ROUTE_VIEW_PRODUCT) {
                HomeFeedScreen(
                    onReportClick = { id ->
                        navController.navigate(createDetailRoute(id))
                    },
                    onRealmsClick = {
                        navController.navigate(ROUTE_REALMS)
                    }
                )
            }
            composable(ROUTE_LEADERBOARD) { LeaderboardScreen() }
            composable(ROUTE_DASH) { DashScreen(navController) }
            composable(ROUTE_PROFILE) { ProfileScreen(navController) }
            composable(ROUTE_SETTINGS) { SettingsScreen(navController) }
            composable(ROUTE_ANALYTICS) { AnalyticsScreen() }
            composable(ROUTE_FINANCE) { FinanceScreen() }
            composable(ROUTE_INVENTORY) { InventoryScreen() }
            composable(ROUTE_LIBRARY) { LibraryScreen() }
            composable(ROUTE_REALMS) { RealmsScreen() }
            composable(
                route = ROUTE_DETAIL,
                arguments = listOf(navArgument("reportId") { type = NavType.StringType })
            ) { navBackStackEntry ->
                val reportId = navBackStackEntry.arguments?.getString("reportId") ?: return@composable
                PriceDetailScreen(reportId = reportId, onBack = { navController.popBackStack() })
            }
        }
    }
}

