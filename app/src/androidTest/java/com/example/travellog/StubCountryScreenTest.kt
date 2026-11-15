package com.example.travellog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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
        compose.onNodeWithText(compose.activity.getString(R.string.visit_add)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.back_to_cities)).performClick()
        compose.onNodeWithText("京都").assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.back_to_countries)).performClick()
        compose.onNodeWithText("Japan (JP)").assertIsDisplayed()
    }

    @Test
    fun createsEditsAndDeletesVisitAndUpdatesCityStatus() {
        assumeTrue(BuildConfig.USE_STUB)
        compose.onNodeWithText("Japan (JP)").performClick()
        compose.onNodeWithText("京都").performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.visit_add)).performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.save)).performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.visit_invalid)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.visit_title)).performTextInput("Kyoto trip")
        compose.onNodeWithText(compose.activity.getString(R.string.save)).performClick()
        compose.onNodeWithText("Kyoto trip").assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.visit_edit)).performClick()
        compose.onNodeWithText("Kyoto trip").performTextClearance()
        compose.onNodeWithText(compose.activity.getString(R.string.visit_title)).performTextInput("Edited trip")
        compose.onNodeWithText(compose.activity.getString(R.string.save)).performClick()
        compose.onNodeWithText("Edited trip").assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.back_to_cities)).performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.city_not_visited)).assertDoesNotExist()
        compose.onNodeWithText("京都").performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.visit_delete)).performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.visit_delete_confirm)).assertIsDisplayed()
        compose.onAllNodesWithText(compose.activity.getString(R.string.visit_delete))[1].performClick()
        compose.onNodeWithText("Edited trip").assertDoesNotExist()
        compose.onNodeWithText(compose.activity.getString(R.string.back_to_cities)).performClick()
        compose.onNodeWithText(compose.activity.getString(R.string.city_not_visited)).assertIsDisplayed()
    }

    @Test
    fun showsHistoryWithCityAndCountry() {
        assumeTrue(BuildConfig.USE_STUB)
        compose.onNodeWithText(compose.activity.getString(R.string.visit_history)).performClick()
        compose.onNodeWithText("Tokyo, Japan").assertIsDisplayed()
        compose.onNodeWithText("2024-04-01").assertIsDisplayed()
    }
}
