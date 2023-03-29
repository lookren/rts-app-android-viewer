package io.dolby.rtsviewer.preferenceStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import io.dolby.rtscomponentkit.utils.SingletonHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

private const val USER_PREFERENCES_STORE_NAME = "user_preferences"

class PrefsStoreImpl private constructor(private val context: Context) : PrefsStore {
    companion object : SingletonHolder<PrefsStore, Context>(::PrefsStoreImpl)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_STORE_NAME)

    private object PreferencesKeys {
        val SHOW_LIVE_INDICATOR = booleanPreferencesKey("show_live_indicator")
    }

    init {
        // Register default preference values, if it does not exist
        coroutineScope.launch {
            context.dataStore.edit { userPreferences ->
                if (userPreferences[PreferencesKeys.SHOW_LIVE_INDICATOR] == null) {
                    userPreferences[PreferencesKeys.SHOW_LIVE_INDICATOR] = true
                }
            }
        }
    }

    private var userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            // Defaults to `true` if the setting does not exist yet - thus shows the live indicator by default on first use
            val showLiveIndicator = preferences[PreferencesKeys.SHOW_LIVE_INDICATOR] ?: true
            UserPreferences(showLiveIndicator)
        }

    override var isLiveIndicatorEnabled: Flow<Boolean> =
        userPreferencesFlow.map { userPreferences ->
            userPreferences.showLiveIndicator
        }

    override suspend fun updateLiveIndicator(checked: Boolean) {
        context.dataStore.edit { userPreferences ->
            userPreferences[PreferencesKeys.SHOW_LIVE_INDICATOR] = checked
        }
    }
}