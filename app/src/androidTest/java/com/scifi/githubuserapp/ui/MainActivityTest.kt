package com.scifi.githubuserapp.ui

import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.scifi.githubuserapp.R
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    private val dummySearch = "dicodingacademy"

    @Before
    fun setup(){
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun assertAddFavorite() {
        onView(withId(R.id.searchBar)).perform(click())

        onView(withId(com.google.android.material.R.id.open_search_view_edit_text)).perform(typeText(dummySearch), closeSoftKeyboard())

        onView(withId(R.id.searchBar)).perform(pressKey(KeyEvent.KEYCODE_ENTER))

        onView(isRoot()).perform(waitFor(1000))

        onView(withId(R.id.rvUser)).perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(withId(R.id.btnFavorite)).perform(click())
    }

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for " + delay + "milliseconds"
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}