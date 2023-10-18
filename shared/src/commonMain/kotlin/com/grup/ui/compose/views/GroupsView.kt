package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.Group
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.GroupsViewModel
import kotlinx.coroutines.launch

internal class GroupsView : Screen {
    @Composable
    override fun Content() {
        val groupsViewModel = getScreenModel<GroupsViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            GroupsLayout(groupsViewModel = groupsViewModel, navigator = navigator)
        }
    }
}

@Composable
private fun GroupsLayout(
    groupsViewModel: GroupsViewModel,
    navigator: Navigator
) {
    val groups: List<Group> by groupsViewModel.groups.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.primary)
        ) {
            item {
                H1Text(text = "Groups")
            }
            item {
                Row(horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = {
                            navigator.push(CreateGroupView())
                        }
                    ) {
                        SmallIcon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Back"
                        )
                    }
                }
            }
            items(groups) { group ->
                GroupCard(
                    group = group,
                    onClick = {
                        groupsViewModel.selectGroup(group)
                        navigator.push(GroupDetailsView())
                    }
                )
            }
        }
    }
}

@Composable
private fun GroupCard(
    group: Group,
    onClick: () -> Unit,
    cardSize: Dp = 140.dp
) {
    Box(
        modifier = Modifier
            .clip(AppTheme.shapes.large)
            .size(cardSize)
            .background(AppTheme.colors.secondary)
            .clickable(onClick = onClick)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.dimensions.cardPadding)
        ) {
            H1Text(text = group.groupName)
        }
    }
}
