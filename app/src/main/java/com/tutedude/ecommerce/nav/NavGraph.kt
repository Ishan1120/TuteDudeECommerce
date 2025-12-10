package com.tutedude.ecommerce.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tutedude.ecommerce.DetailsScreen
import com.tutedude.ecommerce.FavoritesScreen
import com.tutedude.ecommerce.HomeScreen
import com.tutedude.ecommerce.UploadScreen
import androidx.compose.runtime.collectAsState
import com.tutedude.ecommerce.SignInScreen

object Routes {
    const val SPLASH = "splash"
    const val SIGN_IN = "signin"
    const val HOME = "home"
    const val DETAILS = "details/{id}"
    const val FAVORITES = "favorites"
    const val UPLOAD = "upload"
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.SPLASH, modifier = modifier) {
        composable(Routes.SPLASH) {
            // Decide where to go based on auth state
            val vm: com.tutedude.ecommerce.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val loggedIn = vm.isLoggedIn.collectAsState().value
            androidx.compose.runtime.LaunchedEffect(loggedIn) {
                if (loggedIn) nav.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                else nav.navigate(Routes.SIGN_IN) { popUpTo(Routes.SPLASH) { inclusive = true } }
            }
        }
        composable(Routes.SIGN_IN) {
            SignInScreen(onContinue = {
                nav.navigate(Routes.HOME) { popUpTo(Routes.SIGN_IN) { inclusive = true } }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(
                onOpenDetails = { id -> nav.navigate("details/$id") },
                onOpenFavorites = { nav.navigate(Routes.FAVORITES) },
                onOpenUpload = { nav.navigate(Routes.UPLOAD) }
            )
        }
        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            DetailsScreen(productId = id, onBack = { nav.popBackStack() })
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(onBack = { nav.popBackStack() }, onOpenDetails = { id -> nav.navigate("details/$id") })
        }
        composable(Routes.UPLOAD) {
            UploadScreen(onBack = { nav.popBackStack() })
        }
    }
}
