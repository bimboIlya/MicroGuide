package com.example.microguide.data.repository

import com.example.microguide.CoroutineTestRule
import com.example.microguide.data.model.PostModel
import com.example.microguide.data.network.PlaceholderApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException

// околобесполезные тесты проксирования вызовов к апишке)
// привел для примера тестирования корутин
class PlaceholderRepositoryImplTest {

    @get:Rule
    val testRule = CoroutineTestRule()

    private val api: PlaceholderApi = mockk(relaxed = true)
    private val repository = PlaceholderRepositoryImpl(api, testRule.dispatcher)

    private val postModel = PostModel(userId = 1, id = 1, title = "poop", body = "omegapoop")

    @Test
    fun `get post by id EXPECT post from api`() = testRule.runBlockingTest {
        repository.getPostById(42)

        coVerify { api.getPostById(42) }
    }

    @Test
    fun `get user by post id EXPECT post from api`() = testRule.runBlockingTest {
        coEvery { api.getPostById(42) } returns postModel

        repository.getUserByPostId(42)

        coVerify { api.getUserById(1) }
    }

    @Test
    fun `get comments by post ids EXPECT comments from api`() = testRule.runBlockingTest {
        coEvery { api.getCommentsByPostId(any()) } returns emptyList()

        repository.getCommentsByPostsId(1, 2)

        coVerifyAll {
            api.getCommentsByPostId(1)
            api.getCommentsByPostId(2)
        }
    }

    @Test(expected = IOException::class)
    fun `load with error EXPECT io exception`() = testRule.runBlockingTest {
        repository.loadWithError()
    }

    @Test
    fun `get user by id EXPECT user from api`() = testRule.runBlockingTest {
        repository.getUserById(1)

        coVerify { api.getUserById(1) }
    }
}