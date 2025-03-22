package com.example.whatsfordinner.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.whatsfordinner.BuildConfig
import com.example.whatsfordinner.data.source.local.DishesDatabase
import com.example.whatsfordinner.data.source.local.HistoryRepository
import com.example.whatsfordinner.data.source.local.OfflineHistoryRepository
import com.example.whatsfordinner.data.source.local.OfflineSelectedTagsRepository
import com.example.whatsfordinner.data.source.local.SelectedTagsRepository
import com.example.whatsfordinner.data.source.local.userpreferences.UserPreferencesRepository
import com.example.whatsfordinner.data.source.network.DishCollectionRepository
import com.example.whatsfordinner.data.source.network.StandardDishCollectionRepository
import com.example.whatsfordinner.dto.DishDto
import com.example.whatsfordinner.dto.TagDto
import com.example.whatsfordinner.ui.QueryStatus
import com.example.whatsfordinner.ui.SharedState
import com.example.whatsfordinner.ui.SharedViewModel
import com.example.whatsfordinner.ui.history.HistoryViewModel
import com.example.whatsfordinner.ui.home.HomeViewModel
import com.example.whatsfordinner.ui.settings.SettingsViewModel
import com.example.whatsfordinner.ui.tags.TagsViewModel
import com.example.whatsfordinner.ui.theme.ThemeViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val appModule = module {
    single {
        val context = get<Context>()
        context.dataStore
    }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ThemeViewModel)
    singleOf(::UserPreferencesRepository)

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
    }
    singleOf(::StandardDishCollectionRepository) bind DishCollectionRepository::class
    viewModelOf(::SharedViewModel)
    single {
        DishesDatabase.getDatabase(get()).selectedTagsDao()
    }
    single {
        DishesDatabase.getDatabase(get()).history()
    }
    singleOf(::OfflineSelectedTagsRepository) bind SelectedTagsRepository::class
    singleOf(::OfflineHistoryRepository) bind HistoryRepository::class
    viewModel { (validDishes: List<DishDto>, queryStatus: QueryStatus, sharedState: StateFlow<SharedState>) ->
        HomeViewModel(
            validDishes = validDishes,
            queryStatus = queryStatus,
            sharedState = sharedState,
            historyRepository = get()
        )
    }
    viewModel { (tags: List<TagDto>, updateAndValidateDishes: suspend () -> Boolean) ->
        TagsViewModel(
            tags = tags,
            updateAndValidateDishes = updateAndValidateDishes,
            selectedTagsRepository = get()
        )
    }
    viewModelOf(::HistoryViewModel)
}