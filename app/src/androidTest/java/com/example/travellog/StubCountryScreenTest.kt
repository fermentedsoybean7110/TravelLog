package com.example.travellog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StubCountryScreenTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsStubBannerAndSampleCountriesWithoutLogin() {
        assumeTrue(BuildConfig.USE_STUB)
        compose.onNodeWithText(compose.activity.getString(R.string.stub_active)).assertIsDisplayed()
        compose.onNodeWithText("Japan (JP)").assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.login)).assertDoesNotExist()
        compose.onNodeWithText(compose.activity.getString(R.string.logout)).assertDoesNotExist()
    }
}
