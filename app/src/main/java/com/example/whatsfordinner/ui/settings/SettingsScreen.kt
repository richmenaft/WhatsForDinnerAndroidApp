package com.example.whatsfordinner.ui.settings

import android.R.attr.left
import android.widget.RadioGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.recreate
import com.example.whatsfordinner.MainActivity
import com.example.whatsfordinner.R
import com.example.whatsfordinner.WhatsForDinnerTopAppBar
import com.example.whatsfordinner.data.source.local.userpreferences.UserPreferredTheme
import com.example.whatsfordinner.data.source.network.dao.TagType
import com.example.whatsfordinner.ui.navigation.NavigationDestination
import com.example.whatsfordinner.ui.tags.TagsDestination

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override var titleRes = R.string.settings_screen_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            WhatsForDinnerTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->

        val settingsUiState = viewModel.settingsUiState.collectAsState()
        val radioOptions = UserPreferredTheme.entries
        Column(
            modifier
                .padding(innerPadding)
                .selectableGroup()
        ) {
            Text(
                text = "Тема приложения",
                modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
                style = MaterialTheme.typography.headlineSmall
                )
            radioOptions.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (theme == settingsUiState.value.selectedTheme),
                            onClick = {
                                viewModel.updateUserPreferredTheme(theme)
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (theme == settingsUiState.value.selectedTheme),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = when (theme){
                            UserPreferredTheme.DARK -> "Темная"
                            UserPreferredTheme.LIGHT -> "Светлая"
                            UserPreferredTheme.SYSTEM -> "Как в системе"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}