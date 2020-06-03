package com.example.serviceexam

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.example.serviceexam.login.LoginFragment
import com.example.serviceexam.login.LoginFragmentDirections
import com.example.serviceexam.repositories.ListRepositoriesFragment
import com.example.serviceexam.repositories.ListRepositoriesFragmentDirections
import com.example.serviceexam.repositories.network.Properties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.reflect.Type


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginTest {

    private lateinit var activity: MainActivity
    val args = Bundle().apply {
        putBoolean("signOff", false)
        putInt("fragmentId", 1)
    }
    val factory = FragmentFactory()

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        activity = activityRule.launchActivity(null)!!

    }

    @Test
    fun testNavigationToMain() {
        val mockNavController = mock(NavController::class.java)
        val fragmentScenario =
            launchFragmentInContainer<LoginFragment>(args, R.style.Theme_AppCompat, factory)

        var userSaved: String? = null

        fragmentScenario.onFragment { fragment ->
            apply {
                Navigation.setViewNavController(fragment.requireView(), mockNavController)
                userSaved = fragment.checkCredentials()
            }
        }

        if (!userSaved.isNullOrEmpty()) {
            verify(mockNavController).navigate(LoginFragmentDirections.navigateToMain())
        } else {
            onView(withId(R.id.radioGroup)).check(ViewAssertions.matches(isDisplayed()))
        }
    }
}

@RunWith(AndroidJUnit4ClassRunner::class)
class SignOffTest {
    private lateinit var activity: MainActivity

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        activity = activityRule.launchActivity(null)!!
    }

    @Test
    fun testSignOffNavigationToMain() {
        val mockNavController = mock(NavController::class.java)
        val args = Bundle().apply {
            putBoolean("signOff", false)
            putInt("fragmentId", 1)
        }
        var signOff = false
        val factory = FragmentFactory() 

        val loginFragmentScenarioBack =
            launchFragmentInContainer<LoginFragment>(args, R.style.Theme_AppCompat, factory)
        loginFragmentScenarioBack.onFragment {
            apply {
                Navigation.setViewNavController(it.requireView(), mockNavController)
                signOff = it.checkSignOff()
            }
        }
        if (signOff) {
            onView(withId(R.id.mainButton)).perform(isEnable(true)).perform(forceClick())
            verify(mockNavController).navigate(LoginFragmentDirections.navigateToMain())
        }
    }
}

@RunWith(AndroidJUnit4ClassRunner::class)
class TestListRepositoryDetail {
    private lateinit var activity: MainActivity

    companion object {
        const val MOCK = "[\n" +
                "{\n" +
                "    \"id\": 369,\n" +
                "    \"node_id\": \"MDEwOlJlcG9zaXRvcnkzNjk=\",\n" +
                "    \"name\": \"css_naked_day\",\n" +
                "    \"full_name\": \"collectiveidea/css_naked_day\",\n" +
                "    \"private\": false,\n" +
                "    \"owner\": {\n" +
                "      \"login\": \"collectiveidea\",\n" +
                "      \"id\": 128,\n" +
                "      \"node_id\": \"MDEyOk9yZ2FuaXphdGlvbjEyOA==\",\n" +
                "      \"avatar_url\": \"https://avatars2.githubusercontent.com/u/128?v=4\",\n" +
                "      \"gravatar_id\": \"\",\n" +
                "      \"url\": \"https://api.github.com/users/collectiveidea\",\n" +
                "      \"html_url\": \"https://github.com/collectiveidea\",\n" +
                "      \"followers_url\": \"https://api.github.com/users/collectiveidea/followers\",\n" +
                "      \"following_url\": \"https://api.github.com/users/collectiveidea/following{/other_user}\",\n" +
                "      \"gists_url\": \"https://api.github.com/users/collectiveidea/gists{/gist_id}\",\n" +
                "      \"starred_url\": \"https://api.github.com/users/collectiveidea/starred{/owner}{/repo}\",\n" +
                "      \"subscriptions_url\": \"https://api.github.com/users/collectiveidea/subscriptions\",\n" +
                "      \"organizations_url\": \"https://api.github.com/users/collectiveidea/orgs\",\n" +
                "      \"repos_url\": \"https://api.github.com/users/collectiveidea/repos\",\n" +
                "      \"events_url\": \"https://api.github.com/users/collectiveidea/events{/privacy}\",\n" +
                "      \"received_events_url\": \"https://api.github.com/users/collectiveidea/received_events\",\n" +
                "      \"type\": \"Organization\",\n" +
                "      \"site_admin\": false\n" +
                "    },\n" +
                "    \"html_url\": \"https://github.com/collectiveidea/css_naked_day\",\n" +
                "    \"description\": \"A Rails plugin that disables all CSS on CSS Naked Day\",\n" +
                "    \"fork\": false,\n" +
                "    \"url\": \"https://api.github.com/repos/collectiveidea/css_naked_day\",\n" +
                "    \"forks_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/forks\",\n" +
                "    \"keys_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/keys{/key_id}\",\n" +
                "    \"collaborators_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/collaborators{/collaborator}\",\n" +
                "    \"teams_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/teams\",\n" +
                "    \"hooks_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/hooks\",\n" +
                "    \"issue_events_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/issues/events{/number}\",\n" +
                "    \"events_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/events\",\n" +
                "    \"assignees_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/assignees{/user}\",\n" +
                "    \"branches_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/branches{/branch}\",\n" +
                "    \"tags_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/tags\",\n" +
                "    \"blobs_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/git/blobs{/sha}\",\n" +
                "    \"git_tags_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/git/tags{/sha}\",\n" +
                "    \"git_refs_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/git/refs{/sha}\",\n" +
                "    \"trees_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/git/trees{/sha}\",\n" +
                "    \"statuses_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/statuses/{sha}\",\n" +
                "    \"languages_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/languages\",\n" +
                "    \"stargazers_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/stargazers\",\n" +
                "    \"contributors_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/contributors\",\n" +
                "    \"subscribers_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/subscribers\",\n" +
                "    \"subscription_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/subscription\",\n" +
                "    \"commits_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/commits{/sha}\",\n" +
                "    \"git_commits_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/git/commits{/sha}\",\n" +
                "    \"comments_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/comments{/number}\",\n" +
                "    \"issue_comment_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/issues/comments{/number}\",\n" +
                "    \"contents_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/contents/{+path}\",\n" +
                "    \"compare_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/compare/{base}...{head}\",\n" +
                "    \"merges_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/merges\",\n" +
                "    \"archive_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/{archive_format}{/ref}\",\n" +
                "    \"downloads_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/downloads\",\n" +
                "    \"issues_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/issues{/number}\",\n" +
                "    \"pulls_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/pulls{/number}\",\n" +
                "    \"milestones_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/milestones{/number}\",\n" +
                "    \"notifications_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/notifications{?since,all,participating}\",\n" +
                "    \"labels_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/labels{/name}\",\n" +
                "    \"releases_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/releases{/id}\",\n" +
                "    \"deployments_url\": \"https://api.github.com/repos/collectiveidea/css_naked_day/deployments\"\n" +
                "  }\n" +
                "]"
    }

    inner class MockCalls {
        fun getListRepositories(): List<Properties>? {
            val collectionType: Type =
                object : TypeToken<List<Properties?>?>() {}.type
            return Gson().fromJson(MOCK, collectionType)
        }
    }

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        activity = activityRule.launchActivity(null)!!
    }

    @Test
    fun navigateToDetail() {
        val mockNavController = mock(NavController::class.java)
        lateinit var item: Properties

        val listRepositoriesScenario =
            launchFragmentInContainer<ListRepositoriesFragment>()
        listRepositoriesScenario.onFragment {
            apply {
                Navigation.setViewNavController(it.requireView(), mockNavController)
                MockCalls().getListRepositories()?.let { it1 ->
                    it1.forEach {
                        item = it
                    }

                    it.adapter.addItems(it1)
                }
            }

        }

        onView(withId(R.id.recyclerView)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                clickOnViewChild(R.id.result_item_cardView)
            )
        )
        verify(mockNavController).navigate(
            ListRepositoriesFragmentDirections.navigateToDetailItem(
                item
            )
        )

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

private fun isEnable(value: Boolean): ViewAction? {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(TextView::class.java)
        }

        override fun perform(
            uiController: UiController,
            view: View
        ) {
            view.isEnabled = value
        }

        override fun getDescription(): String {
            return "Show / Hide View"
        }
    }
}


fun clickOnViewChild(viewId: Int) = object : ViewAction {
    override fun getConstraints() = null

    override fun getDescription() = "Click on a child view with specified id."

    override fun perform(uiController: UiController, view: View) =
        ViewActions.click().perform(uiController, view.findViewById<View>(viewId))
}
