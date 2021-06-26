package com.example.microguide.udf.var2.presentation

import app.cash.turbine.test
import com.example.microguide.CoroutineTestRule
import com.example.microguide.data.model.Address
import com.example.microguide.data.model.Company
import com.example.microguide.data.model.Geo
import com.example.microguide.data.model.UserModel
import com.example.microguide.data.repository.PlaceholderRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class Udf2ViewModelTest {

    @get:Rule
    val testRule = CoroutineTestRule()

    private val repository: PlaceholderRepository = mockk()

    private val viewModel = Udf2ViewModel(repository)

    private val userModel = UserModel(
        id = 1,
        name = "Bob",
        username = "Bob",
        email = "Bob@bob.bob",
        address = Address(
            street = "Shchetinkina",
            suite = "1",
            city = "Krsk",
            zipcode = "660075",
            geo = Geo(
                lat = "56.024792",
                lng = "92.840212"
            )
        ),
        phone = "8 800 555 35 35",
        website = "google.com",
        company = Company(
            name = "poop",
            catchPhrase = "we like poop",
            bs = "We graduate retarded kids"
        )
    )

    @Test
    fun `create viewModel EXPECT initial state is Input`() = testRule.runBlockingTest {
        viewModel.state.test {
            assertEquals(ScreenState.Input, expectItem())
        }
    }

    @Test
    fun `get user EXPECT correct order of states`() = testRule.runBlockingTest {
        coEvery { repository.getUserById(any()) } returns userModel

        viewModel.state.test {
            viewModel.getUser("42")

            assertEquals(ScreenState.Input, expectItem())
            assertEquals(ScreenState.Loading, expectItem())
            assertEquals(ScreenState.Content(userModel), expectItem())
        }
    }

    @Test
    fun `get user unsuccessful EXPECT correct order of states`() = testRule.runBlockingTest {
        coEvery { repository.getUserById(any()) } throws IOException()

        viewModel.state.test {
            viewModel.getUser("42")

            assertEquals(ScreenState.Input, expectItem())
            assertEquals(ScreenState.Loading, expectItem())
            assertEquals(ScreenState.Error, expectItem())
        }
    }

    @Test
    fun `retry after Error state EXPECT state is Input`() {
        coEvery { repository.getUserById(any()) } throws IOException()
        viewModel.getUser("42")

        viewModel.retry()

        assertEquals(viewModel.state.value, ScreenState.Input)
    }
}