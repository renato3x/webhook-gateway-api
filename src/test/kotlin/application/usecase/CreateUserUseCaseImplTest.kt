package application.usecase

import dev.renato3x.application.usecase.CreateUserUseCaseImpl
import dev.renato3x.domain.command.CreateUserCommand
import dev.renato3x.domain.exception.UsernameAlreadyExistsException
import dev.renato3x.domain.model.User
import dev.renato3x.domain.port.out.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateUserUseCaseImplTest {
    private val userRepository = mockk<UserRepository>()
    private val useCase = CreateUserUseCaseImpl(userRepository)

    @Test
    fun `should throw an exception when username already exists`() = runTest {
        val command = CreateUserCommand(username = "johndoe")
        val existingUser = User(
            id = 1,
            username = "johndoe",
            apiKey = Uuid.random()
        )

        coEvery { userRepository.findByUsername("johndoe") } returns existingUser

        assertFailsWith<UsernameAlreadyExistsException> {
            useCase.execute(command)
        }

        coVerify(exactly = 0) { userRepository.save(any()) }
        coVerify(exactly = 1) { userRepository.findByUsername("johndoe") }
    }

    @Test
    fun `should create and return user when username is available`() = runTest {
        val command = CreateUserCommand(username = "johndoe")
        val savedUser = User(
            id = 1,
            username = "johndoe",
            apiKey = Uuid.random()
        )

        coEvery { userRepository.findByUsername("johndoe") } returns null
        coEvery { userRepository.save(any()) } returns savedUser

        val result = useCase.execute(command)

        assertEquals("johndoe", result.username)
        assertEquals(1, result.id)

        coVerify(exactly = 1) { userRepository.save(any())  }
        coVerify(exactly = 1) { userRepository.findByUsername("johndoe") }
    }
}