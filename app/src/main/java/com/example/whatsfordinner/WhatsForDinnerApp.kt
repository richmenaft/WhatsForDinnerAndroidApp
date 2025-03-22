package com.example.whatsfordinner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.whatsfordinner.data.source.network.dao.TagType
import com.example.whatsfordinner.ui.QueryStatus
import com.example.whatsfordinner.ui.SharedViewModel
import com.example.whatsfordinner.ui.navigation.AppNavHost
import org.koin.androidx.compose.koinViewModel

@Composable
fun WhatsForDinnerApp(
    navController: NavHostController = rememberNavController(),
    sharedViewModel: SharedViewModel = koinViewModel()
) {
    if (sharedViewModel.queryState != QueryStatus.Loading)
        AppNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsForDinnerTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable() (RowScope.() -> Unit) = {},
    navigateUp: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_desc)
                    )
                }
            }
        },
        actions = actions,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBottomAppBar(
    scrollBehavior: BottomAppBarScrollBehavior? = null,
    onClickLike: () -> Unit,
    onClickDislike: () -> Unit
) {
    BottomAppBar(
        scrollBehavior = scrollBehavior
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            IconButton(
                onClick = onClickDislike,
            ) {
                Icon(
                    imageVector = Icons.Filled.ThumbUp,
                    contentDescription = stringResource(R.string.reject_dish_desc),
                    modifier = Modifier
                        .rotate(180f)
                        .size(125.dp),
                    tint = MaterialTheme.colorScheme.inversePrimary
                )
            }

            IconButton(
                onClick = onClickLike
            ) {
                Icon(
                    imageVector = Icons.Filled.ThumbUp,
                    contentDescription = stringResource(R.string.reject_dish_desc),
                    modifier = Modifier
                        .size(125.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            /*
            Button(
                onClick = onClickLike,
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, Color.Black),
                contentPadding = PaddingValues(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.ThumbUp,
                    contentDescription = stringResource(R.string.reject_dish_desc),
                    modifier = Modifier
                        .rotate(180f)
                )
            }
            Button(
                onClick = onClickLike,
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, Color.Black),
                contentPadding = PaddingValues(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.ThumbUp,
                    contentDescription = stringResource(R.string.accept_dish_desc),
                    modifier = Modifier
                )
            }

             */
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomAppBar(
    scrollBehavior: BottomAppBarScrollBehavior? = null,
    onClickChangeTagType: (tagType: TagType) -> Unit,
) {
    BottomAppBar(
        scrollBehavior = scrollBehavior
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            IconButton(
                onClick = {
                    onClickChangeTagType(TagType.INGREDIENT)
                },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_ingredient_tag),
                    contentDescription = stringResource(R.string.ingredient_tags_desc),
                    modifier = Modifier
                        .size(125.dp)
                )
            }
            IconButton(
                onClick = {
                    onClickChangeTagType(TagType.TYPE)
                },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_type_tag),
                    contentDescription = stringResource(R.string.type_tags_desc),
                    modifier = Modifier
                        .size(125.dp)
                )
            }
            IconButton(
                onClick = {
                    onClickChangeTagType(TagType.HOLIDAY)
                },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_holiday_tag),
                    contentDescription = stringResource(R.string.holiday_tags_desc),
                    modifier = Modifier
                        .size(125.dp)
                )
            }
            /*
            Button(
                onClick = {
                    onClickChangeTagType(TagType.INGREDIENT)
                },
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, Color.Black),
                contentPadding = PaddingValues(16.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_ingredient_tag),
                    contentDescription = stringResource(R.string.ingredient_tags_desc),
                )
            }
            Button(
                onClick = {
                    onClickChangeTagType(TagType.TYPE)
                },
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, Color.Black),
                contentPadding = PaddingValues(16.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_type_tag),
                    contentDescription = stringResource(R.string.type_tags_desc),
                )
            }
            Button(
                onClick = {
                    onClickChangeTagType(TagType.HOLIDAY)
                },
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.dp, Color.Black),
                contentPadding = PaddingValues(16.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_holiday_tag),
                    contentDescription = stringResource(R.string.holiday_tags_desc),
                )
            }

             */
        }
    }
}