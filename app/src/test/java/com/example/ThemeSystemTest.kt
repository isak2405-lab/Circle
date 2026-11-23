package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.data.local.InMemoryThemePreferenceStore
import com.example.data.local.ThemePreferencesRepository
import com.example.data.repository.CircleRepository
import com.example.ui.CircleViewModel
import com.example.ui.Screen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppearanceMode
import com.example.ui.theme.OrbynPalettes
import com.example.ui.theme.ThemeConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeSystemTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun contrastRatio(foreground: Color, background: Color): Float {
        val l1 = foreground.luminance()
        val l2 = background.luminance()
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05f) / (darker + 0.05f)
    }

    @Test
    fun `six themes exist and default config is Royal Purple following system`() {
        assertEquals(6, AppTheme.entries.size)
        val expectedIds = setOf(
            "royal_purple",
            "ocean_blue",
            "emerald_green",
            "rose_pink",
            "sunset_gold",
            "midnight"
        )
        assertEquals(expectedIds, AppTheme.entries.map { it.id }.toSet())

        val defaultConfig = ThemeConfig()
        assertEquals(AppTheme.ROYAL_PURPLE, defaultConfig.theme)
        assertEquals(AppearanceMode.SYSTEM, defaultConfig.appearanceMode)
        assertEquals(AppTheme.ROYAL_PURPLE, AppTheme.fromId("unknown_id"))
        assertEquals(AppearanceMode.SYSTEM, AppearanceMode.fromId("unknown_mode"))
    }

    @Test
    fun `all six themes have accessible WCAG contrast in both Light and Dark modes`() {
        val lightPrimaries = mutableSetOf<Color>()
        val darkPrimaries = mutableSetOf<Color>()

        AppTheme.entries.forEach { theme ->
            val palette = OrbynPalettes.forTheme(theme)

            // Light palette checks
            val lightScheme = palette.colorScheme(isDark = false)
            val lightExt = palette.extendedColors(isDark = false)
            lightPrimaries.add(lightScheme.primary)

            assertFalse("${theme.displayName} light should not be dark", lightExt.isDark)
            assertTrue(
                "${theme.displayName} light canvas should be bright",
                lightExt.canvas.luminance() > 0.85f
            )
            assertTrue(
                "${theme.displayName} light primaryText on cardSurface contrast >= 4.5",
                contrastRatio(lightExt.primaryText, lightExt.cardSurface) >= 4.5f
            )
            assertTrue(
                "${theme.displayName} light secondaryText on cardSurface contrast >= 4.5",
                contrastRatio(lightExt.secondaryText, lightExt.cardSurface) >= 4.5f
            )
            assertTrue(
                "${theme.displayName} light onPrimary on primary contrast >= 4.5",
                contrastRatio(lightScheme.onPrimary, lightScheme.primary) >= 4.5f
            )
            assertTrue(
                "${theme.displayName} light onSelectedContainer on selectedContainer contrast >= 4.5",
                contrastRatio(lightExt.onSelectedContainer, lightExt.selectedContainer) >= 4.5f
            )

            // Dark palette checks
            val darkScheme = palette.colorScheme(isDark = true)
            val darkExt = palette.extendedColors(isDark = true)
            darkPrimaries.add(darkScheme.primary)

            assertTrue("${theme.displayName} dark should be dark", darkExt.isDark)
            assertTrue(
                "${theme.displayName} dark canvas should be dark",
                darkExt.canvas.luminance() < 0.08f
            )
            assertTrue(
                "${theme.displayName} dark primaryText on cardSurface contrast >= 4.5",
                contrastRatio(darkExt.primaryText, darkExt.cardSurface) >= 4.5f
            )
            assertTrue(
                "${theme.displayName} dark secondaryText on cardSurface contrast >= 4.5",
                contrastRatio(darkExt.secondaryText, darkExt.cardSurface) >= 4.5f
            )
            assertTrue(
                "${theme.displayName} dark onPrimary on primary contrast >= 4.5",
                contrastRatio(darkScheme.onPrimary, darkScheme.primary) >= 4.5f
            )
            assertTrue(
                "${theme.displayName} dark onSelectedContainer on selectedContainer contrast >= 4.5",
                contrastRatio(darkExt.onSelectedContainer, darkExt.selectedContainer) >= 4.5f
            )
            assertNotEquals(lightExt.canvas, darkExt.canvas)
        }

        // Every theme must have a distinct visual identity
        assertEquals(6, lightPrimaries.size)
        assertEquals(6, darkPrimaries.size)
    }

    @Test
    fun `appearance mode resolves independently of selected colour theme`() {
        val oceanDark = ThemeConfig(AppTheme.OCEAN_BLUE, AppearanceMode.DARK)
        assertTrue(oceanDark.resolveDarkTheme(isSystemInDarkTheme = false))
        assertTrue(oceanDark.resolveDarkTheme(isSystemInDarkTheme = true))

        val midnightLight = ThemeConfig(AppTheme.MIDNIGHT, AppearanceMode.LIGHT)
        assertFalse(midnightLight.resolveDarkTheme(isSystemInDarkTheme = true))
        assertFalse(midnightLight.resolveDarkTheme(isSystemInDarkTheme = false))

        val sunsetSystem = ThemeConfig(AppTheme.SUNSET_GOLD, AppearanceMode.SYSTEM)
        assertTrue(sunsetSystem.resolveDarkTheme(isSystemInDarkTheme = true))
        assertFalse(sunsetSystem.resolveDarkTheme(isSystemInDarkTheme = false))
    }

    @Test
    fun `theme and appearance preferences persist to DataStore across restart`() = runTest {
        val prefsFile = tempFolder.newFile("orbyn_test_prefs.preferences_pb")

        // First app session: write Ocean Blue + Dark Mode
        val scope1 = CoroutineScope(Dispatchers.IO + Job())
        val dataStore1 = PreferenceDataStoreFactory.create(
            scope = scope1,
            produceFile = { prefsFile }
        )
        val repo1 = ThemePreferencesRepository(dataStore1)
        assertEquals(ThemeConfig(AppTheme.ROYAL_PURPLE, AppearanceMode.SYSTEM), repo1.themeConfig.first())

        repo1.setTheme(AppTheme.OCEAN_BLUE)
        repo1.setAppearanceMode(AppearanceMode.DARK)
        assertEquals(ThemeConfig(AppTheme.OCEAN_BLUE, AppearanceMode.DARK), repo1.themeConfig.first())
        scope1.cancel()

        // Second app session (re-instantiating from same file on disk)
        val scope2 = CoroutineScope(Dispatchers.IO + Job())
        val dataStore2 = PreferenceDataStoreFactory.create(
            scope = scope2,
            produceFile = { prefsFile }
        )
        val repo2 = ThemePreferencesRepository(dataStore2)
        val restored = repo2.themeConfig.first()
        assertEquals(AppTheme.OCEAN_BLUE, restored.theme)
        assertEquals(AppearanceMode.DARK, restored.appearanceMode)
        scope2.cancel()
    }

    @Test
    fun `theme selection survives logout and re-login and supports navigation to AppearanceThemes`() {
        val store = InMemoryThemePreferenceStore()
        val viewModel = CircleViewModel(
            repository = CircleRepository(),
            themePreferenceStore = store
        )

        // Navigate Profile -> AppearanceThemes -> Back
        viewModel.navigateTo(Screen.Profile)
        assertEquals(Screen.Profile, viewModel.currentScreen.value)
        viewModel.navigateTo(Screen.AppearanceThemes)
        assertEquals(Screen.AppearanceThemes, viewModel.currentScreen.value)

        // Apply Emerald Green + Dark Mode
        viewModel.setAppTheme(AppTheme.EMERALD_GREEN)
        viewModel.setAppearanceMode(AppearanceMode.DARK)
        assertEquals(ThemeConfig(AppTheme.EMERALD_GREEN, AppearanceMode.DARK), viewModel.themeConfig.value)

        // Navigate back to Profile
        assertTrue(viewModel.navigateBack())
        assertEquals(Screen.Profile, viewModel.currentScreen.value)

        // Logout then log back in: theme must remain Emerald Green + Dark Mode
        viewModel.logout()
        assertEquals(Screen.Auth, viewModel.currentScreen.value)
        assertEquals(AppTheme.EMERALD_GREEN, viewModel.themeConfig.value.theme)
        assertEquals(AppearanceMode.DARK, viewModel.themeConfig.value.appearanceMode)

        viewModel.login("alex@orbyn.app", "alexvance")
        assertEquals(Screen.Feed, viewModel.currentScreen.value)
        assertEquals(AppTheme.EMERALD_GREEN, viewModel.themeConfig.value.theme)
        assertEquals(AppearanceMode.DARK, viewModel.themeConfig.value.appearanceMode)

        // Reset restores default Royal Purple + System
        viewModel.resetThemeToDefault()
        assertEquals(ThemeConfig(AppTheme.ROYAL_PURPLE, AppearanceMode.SYSTEM), viewModel.themeConfig.value)
    }
}
