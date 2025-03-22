package com.example.whatsfordinner.ui.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.whatsfordinner.R
import com.example.whatsfordinner.TagsBottomAppBar
import com.example.whatsfordinner.WhatsForDinnerTopAppBar
import com.example.whatsfordinner.data.source.local.dish.TagStatus
import com.example.whatsfordinner.data.source.network.dao.TagType
import com.example.whatsfordinner.dto.TagDto
import com.example.whatsfordinner.dto.toSelectedTag
import com.example.whatsfordinner.ui.navigation.NavigationDestination
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object TagsDestination : NavigationDestination {
    override val route = "tags"
    override var titleRes = TagType.INGREDIENT.tagScreenTitle
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TagsViewModel,
) {
    val tagsUiState = viewModel.tagsUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TagsDestination.titleRes = tagsUiState.value.currentTagType.tagScreenTitle


    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            WhatsForDinnerTopAppBar(
                title = stringResource(TagsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior,
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(R.dimen.padding_small))
                    ) {
                        IconButton(
                            onClick = viewModel::deleteAllSelectedTags,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = stringResource(R.string.refresh_tags_desc),
                            )
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val isSaveCompleted: Deferred<Boolean> = async {
                                        return@async viewModel.saveTagsChanges(tagsUiState.value.tags.map {
                                            it.toSelectedTag()
                                        })
                                    }
                                    if (isSaveCompleted.await()) {
                                        navigateBack()
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.save_changes_desc),
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            TagsBottomAppBar(
                onClickChangeTagType = viewModel::changeTagType
            )
        }
    ) { innerPadding ->
        when (tagsUiState.value.currentTagType) {
            TagType.INGREDIENT -> ListOfTags(
                tags = tagsUiState.value.tags.filter {
                    it.type == TagType.INGREDIENT
                },
                contentPadding = innerPadding,
                updateTagStatus = viewModel::updateTagStatus,
                modifier = Modifier
                    .fillMaxSize()
            )

            TagType.TYPE -> ListOfTags(
                tags = tagsUiState.value.tags.filter {
                    it.type == TagType.TYPE
                },
                contentPadding = innerPadding,
                updateTagStatus = viewModel::updateTagStatus,
                modifier = Modifier
                    .fillMaxSize()
            )

            TagType.HOLIDAY -> ListOfTags(
                tags = tagsUiState.value.tags.filter {
                    it.type == TagType.HOLIDAY
                },
                contentPadding = innerPadding,
                updateTagStatus = viewModel::updateTagStatus,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

    }
}

@Composable
fun ListOfTags(
    tags: List<TagDto> = listOf(),
    contentPadding: PaddingValues,
    updateTagStatus: (tag: TagDto) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(25.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    ) {
        items(tags, key = { tag ->
            tag.id
        }) { tag ->
            Tag(
                tag = tag,
                updateTagStatus = {
                    updateTagStatus(tag)
                },
                modifier
                    .fillMaxSize()
                    .animateItem()
            )
        }
    }
}

@Composable
fun Tag(
    tag: TagDto,
    updateTagStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val chipColors = when (tag.status) {
        TagStatus.APPLY -> FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        TagStatus.REJECT -> FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.inversePrimary,
            labelColor = MaterialTheme.colorScheme.onPrimary
        )

        TagStatus.INACTIVE -> FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }

    val iconColors = when (tag.status) {
        TagStatus.APPLY -> MaterialTheme.colorScheme.onPrimary

        TagStatus.REJECT -> MaterialTheme.colorScheme.onPrimary

        TagStatus.INACTIVE -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    FilterChip(
        selected = tag.status == TagStatus.APPLY,
        onClick = updateTagStatus,
        label = {
            Text(
                text = tag.title,
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier,
        elevation = SelectableChipElevation(
            elevation = 8.dp,
            pressedElevation = 10.dp,
            focusedElevation = 10.dp,
            hoveredElevation = 10.dp,
            draggedElevation = 10.dp,
            disabledElevation = 10.dp
        ),
        leadingIcon = {
            //if (currentFilterState == FilterState.Exclude) {
            Icon(
                imageVector =
                    when (tag.status) {
                        TagStatus.APPLY -> Icons.Filled.Check
                        TagStatus.REJECT -> Icons.Filled.Close
                        TagStatus.INACTIVE -> Icons.Filled.Home
                    },
                contentDescription = "Исключить",
                tint = iconColors
            )
            //}
        },
        colors = chipColors,
    )
}

