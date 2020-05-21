package com.example.serviceexam

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.serviceexam.history.db.History
import com.example.serviceexam.history.db.HistoryDao
import com.example.serviceexam.history.db.HistoryDataBase
import com.example.serviceexam.main.MainFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.IOException
import java.sql.Statement

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class ServiceTest {

    //@get:Rule
    //val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testNavigationToListRepository() {
        val mockNavController = mock(NavController::class.java)
        val fragmentScenario = launchFragmentInContainer<MainFragment>()
        fragmentScenario.moveToState(Lifecycle.State.CREATED)
        fragmentScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        onView(ViewMatchers.withId(R.id.btnTakePhoto)).perform(ViewActions.click())
        verify(mockNavController).navigate(R.id.navigateToCamera)
    }
}

/*@RunWith(JUnit4::class)
class DataBaseTest {
    private lateinit var historyDato: HistoryDao
    private lateinit var db: HistoryDataBase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, HistoryDataBase::class.java
        ).build()
        historyDato = db.historyDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    suspend fun addPhotoToHistory() {
        val photo: History = TestUtil.insertPhoto(
            1,
            "image",
            "MicaTest"
        )
        historyDato.insert(photo)
        val byName = historyDato.getAll()
        assert(byName.value?.isNotEmpty()!! )
    }
}*/
