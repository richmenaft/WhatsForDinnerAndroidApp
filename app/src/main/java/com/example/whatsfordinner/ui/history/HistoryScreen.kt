package com.example.whatsfordinner.ui.history

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.whatsfordinner.R
import com.example.whatsfordinner.WhatsForDinnerTopAppBar
import com.example.whatsfordinner.data.source.local.dish.HistoryDish
import com.example.whatsfordinner.ui.home.HomeDestination
import com.example.whatsfordinner.ui.navigation.NavigationDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.time.Month

object HistoryDestination : NavigationDestination {
    override val route = "history"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel
) {
    val historyUiState = viewModel.historyUiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            WhatsForDinnerTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior,
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(R.dimen.padding_small))
                    ) {
                        IconButton(
                            onClick = { showDialog = true },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = stringResource(R.string.clear_history_desc),
                            )
                        }
                    }
                }
            )
        },

        ) { innerPadding ->
        HistoryList(
            dishes = historyUiState.value.dishes,
            innerPadding,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            onDelete = viewModel::deleteDishFromHistory
        )
        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Text(
                            text = "Вы уверены, что хотите стереть историю?",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.clearHistory()
                                    showDialog = false
                                }
                            ) {
                                Text(
                                    text = "Да",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = { showDialog = false }
                            ) {
                                Text(
                                    text = "Нет",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryList(
    dishes: List<HistoryDish>,
    contentPadding: PaddingValues,
    onDelete: (HistoryDish) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = contentPadding,
    ) {
        items(dishes, key = { dish ->
            dish.id
        }) { dish ->
            HistoryListElement(
                title = dish.title,
                date = dish.date,
                imageUrl = dish.imageUrl,
                imageContentDescription = dish.imageContentDescription,
                onDelete = {
                    onDelete(dish)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryListElement(
    title: String,
    date: LocalDateTime,
    imageUrl: String,
    imageContentDescription: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            if (state == SwipeToDismissBoxValue.EndToStart) {
                coroutineScope.launch {
                    delay(100)
                    onDelete()
                }
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color =
                animateColorAsState(
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.background
                        SwipeToDismissBoxValue.StartToEnd -> Color.Green
                        SwipeToDismissBoxValue.EndToStart -> Color.Red
                    }
                )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color.value)
            )
        },
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium)),
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        Card(
            modifier = Modifier
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        error = painterResource(R.drawable.ic_broken_image),
                        placeholder = painterResource(R.drawable.loading_img),
                        contentDescription = imageContentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(0.2f)
                            .height(70.dp)
                            .clip(MaterialTheme.shapes.large)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = modifier
                            .fillMaxSize()
                            .weight(0.5f)
                    )
                    {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = formatDateToRussianStyle(date, LocalContext.current)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDateToRussianStyle(date: LocalDateTime, context: Context): String {

    return when (date.month) {
        Month.JANUARY -> context.getString(
            R.string.russian_formatted_date_january,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.FEBRUARY -> context.getString(
            R.string.russian_formatted_date_february,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.MARCH -> context.getString(
            R.string.russian_formatted_date_march,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.APRIL -> context.getString(
            R.string.russian_formatted_date_april,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.MAY -> context.getString(
            R.string.russian_formatted_date_may,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.JUNE -> context.getString(
            R.string.russian_formatted_date_june,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.JULY -> context.getString(
            R.string.russian_formatted_date_july,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.AUGUST -> context.getString(
            R.string.russian_formatted_date_august,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.SEPTEMBER -> context.getString(
            R.string.russian_formatted_date_september,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.OCTOBER -> context.getString(
            R.string.russian_formatted_date_october,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.NOVEMBER -> context.getString(
            R.string.russian_formatted_date_november,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )

        Month.DECEMBER -> context.getString(
            R.string.russian_formatted_date_december,
            date.dayOfMonth.toString(),
            date.year.toString(),
            date.hour.toString(),
            date.minute.toString(),
        )
    }
}