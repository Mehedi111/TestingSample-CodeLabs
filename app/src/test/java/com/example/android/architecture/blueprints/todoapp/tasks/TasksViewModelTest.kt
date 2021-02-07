package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 2/7/2021, Sun
 */

/**
 * @see [https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics?authuser=1#7]
 */
/*
* The AndroidX Test libraries include classes and methods that provide you with versions of
* components like Applications and Activities that are meant for tests.
* When you have a local test where you need simulated
* Android framework classes (such as an Application Context),
* follow these steps to properly set up AndroidX Test:
* */

/*
* Pure view model tests usually go in the test source set because
* the view model code shouldn't rely on the Android framework or OS.
* As a local test, it will also run faster because you run the tests on your
* local machine and not on an emulator or device.
* */

/*
* AndroidX Test is a collection of libraries for testing.
* It includes classes and methods that give you versions of components like Applications and Activities,
* that are meant for tests.
*  As an example, this code you wrote is an example of an AndroidX Test function
*  for getting an application context.
* */

/*
* The simulated Android environment that AndroidX Test uses for local tests is provided by Robolectric.
* Robolectric is a library that creates a simulated Android environment for
*  tests and runs faster than booting up an emulator or running on a device.
* Without the Robolectric dependency, you'll get an error
* */


/**
 * A test runner is a JUnit component that runs tests. Without a test runner,
 * your tests would not run. There's a default test runner provided by JUnit
 * that you get automatically. @RunWith swaps out that default test runner.
 *
 * The AndroidJUnit4 test runner allows for AndroidX Test
 * to run your test differently depending on whether they are instrumented or local tests.
 */
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {
    //given, when and then or Arrange, Act, Assert

    @get: Rule
    //This rule runs all Architecture Components-related background jobs
    //in the same thread so that the test results happen synchronously
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    @Before
    fun setUp(){
        tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    }

    /**
     * NOTE: each test should have a fresh instance of the subject under test (the ViewModel in this case).
     */

    @Test
    fun addNewTask_setsNewTaskEvent() {
        // Given a fresh TasksViewModel
        //val taskViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        // Create observer - no need for it to do anything!
        val observer = Observer<Event<Unit>> {}

        //BOILERPLATE EXAMPLE
        /*try {

            taskViewModel.newTaskEvent.observeForever(observer)
            // When adding a new task
            taskViewModel.addNewTask()

            // Then the new task event is triggered
            val value = taskViewModel.newTaskEvent.value
            assertThat(value?.getContentIfNotHandled(), (not(nullValue())))


        } finally {
            // Whatever happens, don't forget to remove the observer!
            taskViewModel.newTaskEvent.removeObserver(observer)
        }*/

        // When adding a new task
        tasksViewModel.addNewTask()

        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        // Then the new task event is triggered
        assertThat(value.getContentIfNotHandled(), not(nullValue()))
    }


    @Test
    fun setFilterAllTask_tasksAddViewVisible(){
        //Given
        //val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        //when
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        //then
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
    }

}