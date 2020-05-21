package com.example.serviceexam

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.serviceexam.history.db.History
import com.example.serviceexam.history.db.HistoryDao
import com.example.serviceexam.history.db.HistoryDataBase
import com.example.serviceexam.history.rows.HistoryViewModel
import com.example.serviceexam.login.LoginFragment
import com.example.serviceexam.repositories.network.Properties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.model.Statement
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException
import java.lang.reflect.Type

class ListRepositoryTest {

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

    @Test
    fun showRepository() {
        val response = MockCalls().getListRepositories()
        assert(response != null)
        print(Gson().toJson(response).toString())
    }
}


@ExperimentalCoroutinesApi
class TestCoroutineRule : TestRule {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testCoroutineDispatcher)

            base.evaluate()

            Dispatchers.resetMain()
            testCoroutineScope.cleanupTestCoroutines()
        }
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest { block() }

}

@RunWith(MockitoJUnitRunner::class)
class DataBaseTest {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var historyDao: HistoryDao

    @Mock
    private lateinit var db: HistoryDataBase

    @Before
    fun createDb() {
        val context = Application()
        db = Room.inMemoryDatabaseBuilder(
            context, HistoryDataBase::class.java
        ).build()
        historyDao = db.historyDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() {
        testCoroutineRule.runBlockingTest {
            val photo: History = TestUtil.insertPhoto(
                1,
                "image",
                "MicaTest"
            )
            doReturn(emptyList<History>())
                .`when`(historyDao)
                .getAll()
            verify(historyDao).getAll()
        }
    }
}