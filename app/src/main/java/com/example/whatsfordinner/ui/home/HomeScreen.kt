package com.example.whatsfordinner.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.whatsfordinner.R
import com.example.whatsfordinner.HomeBottomAppBar
import com.example.whatsfordinner.WhatsForDinnerTopAppBar
import com.example.whatsfordinner.dto.DishDto
import com.example.whatsfordinner.ui.QueryStatus
import com.example.whatsfordinner.ui.navigation.NavigationDestination
import com.example.whatsfordinner.ui.theme.WhatsForDinnerTheme
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToHistory: () -> Unit,
    navigateToTags: () -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: HomeViewModel,
) {

    val homeUiState = viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var randomDish = viewModel.randomDish
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(homeUiState) {
        viewModel.shuffleAndUpdateRandomDishes()
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            WhatsForDinnerTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(R.dimen.padding_small))
                    ) {
                        IconButton(
                            onClick = navigateToHistory,
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_history_screen_button),
                                contentDescription = stringResource(R.string.history_button_desc),
                            )
                        }
                        IconButton(
                            onClick = navigateToTags,
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_tag_screen_button),
                                contentDescription = stringResource(R.string.settings_button_desc),
                            )
                        }
                        IconButton(
                            onClick = navigateToSettings,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings_button_desc),
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (homeUiState.value.queryStatus == QueryStatus.Success) {
                HomeBottomAppBar(
                    onClickLike = {
                        coroutineScope.launch {
                            val randomDish: DishDto? = randomDish.value
                            if (randomDish != null) {
                                viewModel.approveDishAndSaveInHistory(randomDish)
                                viewModel.shuffleAndUpdateRandomDishes()
                            }
                        }
                    },
                    onClickDislike = {
                        viewModel.popAndUpdateRandomDishes()
                    }
                )
            }
        }
    ) { innerPadding ->
        when (homeUiState.value.queryStatus) {
            QueryStatus.Loading -> LoadingScreen(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            QueryStatus.Success -> {

                if (randomDish.value != null) {
                    RandomDish(
                        dishTitle = randomDish.value!!.title,
                        dishDescription = randomDish.value!!.description,
                        imageUrl = randomDish.value!!.image.url,
                        imageContentDescription = randomDish.value!!.image.contentDescription,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                } else {
                    NothingFoundScreen(
                        viewModel::shuffleAndUpdateRandomDishes,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }

            QueryStatus.Error -> ErrorScreen(
                retryAction = {},
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }

    }
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(60.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.something_went_wrong),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = retryAction,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .height(75.dp)
                .width(150.dp)
        ) {
            Text(
                text = "Попробовать снова",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.dishes_loading)
        )
    }
}

@Composable
fun RandomDish(
    dishTitle: String,
    dishDescription: String,
    imageUrl: String,
    imageContentDescription: String,
    modifier: Modifier = Modifier
) {
    //TODO: возможная причина изменения цвета текста
    Card(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium)),
        colors = CardColors(
            containerColor = CardDefaults.cardColors().containerColor,
            contentColor = CardDefaults.cardColors().contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
            disabledContentColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = dishTitle,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = dishDescription,
            )
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
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
            )
        }
    }
}

@Composable
fun NothingFoundScreen(
    updateDishes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Упс, мы ничего не нашли под ваш запрос. Попробуйте изменить фильтр.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(
            onClick = updateDishes
        ) {
            Text(
                text = "Попробовать снова"
            )
        }
    }
}

@Preview
@Composable
fun NothingFoundScreenPreview(
    modifier: Modifier = Modifier,
) {
    WhatsForDinnerTheme {
        Surface {
            NothingFoundScreen(
                updateDishes = {},
                modifier
                    .fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun ErrorScreenPreview(
    modifier: Modifier = Modifier,
) {
    WhatsForDinnerTheme {
        Surface {
            ErrorScreen(
                retryAction = {},
                modifier
                    .fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun RandomDishPreview(
    modifier: Modifier = Modifier,
) {
    WhatsForDinnerTheme {
        Surface {
            RandomDish(
                dishTitle = "Пюрешка с котлетой",
                modifier = modifier
                    .fillMaxSize(),
                dishDescription = "Пюре с котлетой - это одно из лучших блюд, придуманных человечеством! Утонченное мясо, переработанное в котлеты и величие белорусской картошки, которое дополняется молоком и перемалывается в мягкую субстанцию. Готово! Приятного аппетита!",
                imageUrl = "https://zoobvjljdcddlwjsmoak.supabase.co/storage/v1/object/public/standart-dish-collection//pure_kotleta.jpg",
                imageContentDescription = "Изображение котлеты с пюре в блюдце"
            )
        }
    }
}

@Preview
@Composable
fun LoadingScreenPreview(
    modifier: Modifier = Modifier,
) {
    WhatsForDinnerTheme {
        Surface {
            LoadingScreen(
                modifier = modifier
                    .fillMaxSize(),
            )
        }
    }
}

