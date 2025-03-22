package com.example.whatsfordinner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.whatsfordinner.ui.SharedViewModel
import com.example.whatsfordinner.ui.history.HistoryDestination
import com.example.whatsfordinner.ui.history.HistoryScreen
import com.example.whatsfordinner.ui.history.HistoryViewModel
import com.example.whatsfordinner.ui.home.HomeDestination
import com.example.whatsfordinner.ui.home.HomeScreen
import com.example.whatsfordinner.ui.home.HomeViewModel
import com.example.whatsfordinner.ui.settings.SettingsDestination
import com.example.whatsfordinner.ui.settings.SettingsScreen
import com.example.whatsfordinner.ui.settings.SettingsViewModel
import com.example.whatsfordinner.ui.tags.TagsDestination
import com.example.whatsfordinner.ui.tags.TagsScreen
import com.example.whatsfordinner.ui.tags.TagsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel = koinViewModel(),
) {
    //TODO: доделать передачу нужных аргументов во viewModel

    val sharedState = sharedViewModel.sharedState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(
            route = HomeDestination.route,
        ) {
            HomeScreen(
                navigateToHistory = {
                    navController.navigate(HistoryDestination.route)
                },
                navigateToTags = {
                    navController.navigate(TagsDestination.route)
                },
                navigateToSettings = {
                    navController.navigate(SettingsDestination.route)
                },
                viewModel = koinViewModel<HomeViewModel>(
                    parameters = {
                        parametersOf(
                            sharedState.value.validDishes,
                            sharedViewModel.queryState,
                            sharedViewModel.sharedState
                        )
                    }
                )
            )
        }
        composable(route = HistoryDestination.route) {
            HistoryScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                viewModel = koinViewModel<HistoryViewModel>()
            )
        }
        composable(route = TagsDestination.route) {
            TagsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                viewModel = koinViewModel<TagsViewModel>(
                    parameters = {
                        parametersOf(
                            sharedState.value.dishes.flatMap {
                                it.tags
                            }
                                .distinct()
                                .sortedBy { it.title },
                            sharedViewModel::updateAndValidateDishes
                        )
                    }
                )
            )
        }
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                viewModel = koinViewModel<SettingsViewModel>()
            )
        }
    }
}