package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 2/8/2021, Mon
 */

/**
 * A test double is an object that can stand in for a real object in a test
 * In particular, mocking instances of types that you don't own usually leads to brittle tests
 * that work only when you've understood the complexities of someone else's implementation of that type.
 * Use such mocks only as a last resort. It's OK to mock your own objects,
 * but keep in mind that mocks annotated using @Spy provide more fidelity
 * than mocks that stub out all functionality within a class.
 * ###################
 * When creating tests, you have the option of creating real objects or test doubles,
 * such as fake objects or mock objects
 * WHY NEED TEST DOUBLE
 * [Need to deal with creating or managing DB for a simple unit test]
 * [Long running test can be failed or succeed, such as Networking called Flaky Test]
 * [Hard to find why test case is Failed]
 */

/**
 * Fake is a lightweight implementation of an API that behaves like the real implementation,
 * but isn't suitable for production. Fakes can be used when you can't use a real implementation in your test
 *
 * Mock that tracks which of its method were called and passes or fails test depending on whether it's method were called
 * correctly. Mocks are used to test interactions between objects.
 *
 * Stub has no logic, and only returns what you tell it to return.  A StubTaskRepository could be programmed to return
 * certain combinations of tasks from getTasks for example
 *
 * Dummy is passed around but not in used, such as if you just need to provide it as a parameter. . If you had a NoOpTaskRepository,
 * it would just implement the TaskRepository with no code in any of the methods.
 *
 * Spy keeps tracks of some additional information,  if you made a SpyTaskRepository,
 * it might keep track of the number of times the addTask method was called.
 */

@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }


    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tasksRepository: DefaultTasksRepository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = DefaultTasksRepository(
                // TODO Dispatchers.Unconfined should be replaced with Dispatchers.Main
                //  this requires understanding more about coroutines + testing
                //  so we will keep this as Unconfined for now.
               // tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Unconfined
                tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Main

        /*
        * Dispatchers.Unconfined executes tasks immediately.
        * But, it doesn't include all of the other testing benefits of TestCoroutineDispatcher,
        *  such as being able to pause execution:
        * */
        )
    }

    //kotlinx-coroutines-test is the coroutines test library, specifically meant for testing coroutines.

    @Test
    fun getTask_requestAllTaskFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
        // runBlockingTest runs synchronously and immediately
        // it makes your coroutines run like non-coroutines, so it is meant for testing code

        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Result.Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
}
