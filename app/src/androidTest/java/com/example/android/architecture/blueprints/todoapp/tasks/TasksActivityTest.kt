package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 2/9/2021, Tue
 */
@RunWith(AndroidJUnit4::class) //because you're using AndroidX test code
@LargeTest //signifies these are end-to-end tests, testing a large portion of the code
class TasksActivityTest {
    //tests mimic how the complete app runs and simulate real usage

    private lateinit var repository: TasksRepository

    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        repository = ServiceLocator.provideTasksRepository(ApplicationProvider.getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }

    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    @Test
    fun editTask() = runBlocking {
        // Set initial state.
        repository.saveTask(Task("TITLE1", "DESCRIPTION"))

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario) // LOOK HERE


        // Espresso code will go here.
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save.
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed.
        onView(withText("TITLE1")).check(doesNotExist())


        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

    /*
    * Note that in this end-to-end test, you don't verify integration with the repository,
    * the navigation controller, or any other components at all. This is what's known as a [black box] test.
    * The test is not supposed to know how things are implemented internally, only the outcome for a given input.
    * */


    @Test
    fun createOneTask_deleteTask() {

        // 1. Start TasksActivity.
        val scenario = ActivityScenario.launch(TasksActivity::class.java)
        //val scenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        // 2. Add an active task by clicking on the FAB and saving a new task.
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE ME"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION ME"))
        onView(withId(R.id.save_task_fab)).perform(click())

        //verify is visible
        onView(withText("NEW TITLE ME")).check(matches(isDisplayed()))

        // 3. Open the new task in a details view.
        onView(withText("NEW TITLE ME")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("NEW TITLE ME")))

        // 4. Click delete task in menu.
        onView(withId(R.id.menu_delete)).perform(click())

        // 5. Verify it was deleted.
        onView(withText("NEW TITLE ME")).check(doesNotExist())

        // 6. Make sure the activity is closed.

        // Make sure the activity is closed before resetting the db:
        scenario.close()
    }


}