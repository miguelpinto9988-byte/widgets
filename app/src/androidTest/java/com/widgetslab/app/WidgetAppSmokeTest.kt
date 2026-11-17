package com.widgetslab.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifica en un dispositivo o emulador Android que la actividad principal
 * se abre y que el catálogo Compose aparece de forma visible.
 */
@RunWith(AndroidJUnit4::class)
class WidgetAppSmokeTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchesAndDisplaysWidgetCatalog() {
        compose.onNodeWithText("Widgets Lab").assertIsDisplayed()
    }
}
