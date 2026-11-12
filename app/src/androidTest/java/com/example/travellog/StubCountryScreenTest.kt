package com.example.travellog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

    @Test
    fun selectsCountryAndShowsVisitedAndUnvisitedCities() {
        assumeTrue(BuildConfig.USE_STUB)
        compose.onNodeWithText("Japan (JP)").performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.stub_active)).assertIsDisplayed()
        compose.onNodeWithText("東京").assertIsDisplayed()
        compose.onNodeWithText("京都").assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.city_visited)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.city_not_visited)).assertIsDisplayed()
        compose.onNodeWithText("東京").performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.city_detail_pending)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.back_to_cities)).performClick()
        compose.onNodeWithText("京都").assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.back_to_countries)).performClick()
        compose.onNodeWithText("Japan (JP)").assertIsDisplayed()
    }
}
