package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppearanceMode
import com.example.ui.theme.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.IOException

private val Context.orbynThemeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "orbyn_theme_preferences"
)

interface ThemePreferenceStore {
    val themeConfig: Flow<ThemeConfig>
    suspend fun setTheme(theme: AppTheme)
    suspend fun setAppearanceMode(mode: AppearanceMode)
    suspend fun updateThemeConfig(config: ThemeConfig)
}

class InMemoryThemePreferenceStore(
    initialConfig: ThemeConfig = ThemeConfig()
) : ThemePreferenceStore {
    private val _state = MutableStateFlow(initialConfig)
    override val themeConfig: Flow<ThemeConfig> = _state.asStateFlow()

    override suspend fun setTheme(theme: AppTheme) {
        _state.update { it.copy(theme = theme) }
    }

    override suspend fun setAppearanceMode(mode: AppearanceMode) {
        _state.update { it.copy(appearanceMode = mode) }
    }

    override suspend fun updateThemeConfig(config: ThemeConfig) {
        _state.value = config
    }
}

class ThemePreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : ThemePreferenceStore {

    constructor(context: Context) : this(context.applicationContext.orbynThemeDataStore)

    override val themeConfig: Flow<ThemeConfig> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ThemeConfig(
                theme = AppTheme.fromId(preferences[KEY_APP_THEME]),
                appearanceMode = AppearanceMode.fromId(preferences[KEY_APPEARANCE_MODE])
            )
        }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[KEY_APP_THEME] = theme.id
        }
    }

    override suspend fun setAppearanceMode(mode: AppearanceMode) {
        dataStore.edit { preferences ->
            preferences[KEY_APPEARANCE_MODE] = mode.id
        }
    }

    override suspend fun updateThemeConfig(config: ThemeConfig) {
        dataStore.edit { preferences ->
            preferences[KEY_APP_THEME] = config.theme.id
            preferences[KEY_APPEARANCE_MODE] = config.appearanceMode.id
        }
    }

    companion object {
        val KEY_APP_THEME = stringPreferencesKey("app_theme_id")
        val KEY_APPEARANCE_MODE = stringPreferencesKey("appearance_mode_id")
    }
}
