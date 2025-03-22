package com.example.whatsfordinner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsfordinner.data.source.local.userpreferences.UserPreferencesRepository
import com.example.whatsfordinner.data.source.local.userpreferences.UserPreferredTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val _settingsUiState = MutableStateFlow<SettingsUiState>(SettingsUiState())

    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            getPreferredTheme()
        }
    }

    private suspend fun getPreferredTheme(){
       userPreferencesRepository.preferredTheme.collect{ theme ->
            _settingsUiState.update { settingsUiState ->
                settingsUiState.copy(
                    selectedTheme = theme
                )
            }
        }
    }

    fun updateUserPreferredTheme(theme: UserPreferredTheme){
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(theme)
        }
    }
}

data class SettingsUiState(
    val selectedTheme: UserPreferredTheme = UserPreferredTheme.SYSTEM
)
