package com.example.serviceexam

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.example.serviceexam.login.LoginFragment
import com.example.serviceexam.login.LoginFragmentDirections
import com.example.serviceexam.main.MainFragment
import com.example.serviceexam.main.MainFragmentDirections
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginTest {

    private lateinit var activity: MainActivity

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup(){
        activity = activityRule.launchActivity(null)!!

    }
    @Test
    fun testNavigationToMain() {
        val mockNavController = mock(NavController::class.java)
        val fragmentScenario = launchFragmentInContainer<LoginFragment>()

        var userSaved: String? = null

        fragmentScenario.onFragment { fragment ->
            apply {
                Navigation.setViewNavController(fragment.requireView(), mockNavController)
                userSaved = fragment.checkCredentials()
            }
        }

        if(!userSaved.isNullOrEmpty()){
            verify(mockNavController).navigate(LoginFragmentDirections.navigateToMain())
        }else{
            onView(withId(R.id.radioGroup)).check(ViewAssertions.matches(isDisplayed()))
        }
    }

    //Util method to test the click function
    private fun forceClick(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isClickable(), isEnabled(), isDisplayed())
            }
            override fun getDescription(): String {
                return "force click"
            }
            override fun perform(uiController: UiController, view: View) {
                view.performClick() // perform click without checking view coordinates.
                uiController.loopMainThreadUntilIdle()
            }
        }
    }
}

@RunWith(AndroidJUnit4ClassRunner::class)
class SignOffTest{
    private lateinit var activity: MainActivity

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup(){
        activity = activityRule.launchActivity(null)!!

    }
    @Test
    fun testNavigationToMain() {
        val mockNavController = mock(NavController::class.java)
        val fragmentScenario = launchFragmentInContainer<LoginFragment>()

        var signOff: Boolean = false

        fragmentScenario.onFragment { fragment ->
            apply {
                Navigation.setViewNavController(fragment.requireView(), mockNavController)
                signOff = fragment.checkSignOff()
            }
        }

        if(signOff){
            verify(mockNavController).navigate(LoginFragmentDirections.navigateToMain())
        }
    }
}
