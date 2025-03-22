package com.example.whatsfordinner.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsfordinner.data.source.local.userpreferences.UserPreferencesRepository
import com.example.whatsfordinner.data.source.local.userpreferences.UserPreferredTheme
import com.example.whatsfordinner.di.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var themeFlow: Flow<UserPreferredTheme> = userPreferencesRepository.preferredTheme
}
