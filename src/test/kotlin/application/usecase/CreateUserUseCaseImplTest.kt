package application.usecase

import dev.renato3x.application.usecase.CreateUserUseCaseImpl
import dev.renato3x.domain.command.CreateUserCommand
import dev.renato3x.domain.exception.EmailAlreadyExistsException
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
    fun `should throw an exception when email already exists`() = runTest {
        val command = CreateUserCommand(
            username = "johndoe",
            email = "john_doe@mail.com"
        )
        val existingUser = User(
            id = 1,
            username = "johndoe123",
            email = "john_doe@mail.com",
            apiKey = Uuid.random()
        )

        coEvery { userRepository.findByEmail(existingUser.email!!) } returns existingUser
        coEvery { userRepository.findByUsername(command.username) } returns null

        assertFailsWith<EmailAlreadyExistsException> {
            useCase.execute(command)
        }

        coVerify(exactly = 0) { userRepository.save(any()) }
        coVerify(exactly = 1) { userRepository.findByEmail(existingUser.email!!) }
        coVerify(exactly = 1) { userRepository.findByUsername(command.username) }
    }

    @Test
    fun `should throw an exception when username already exists`() = runTest {
        val command = CreateUserCommand(
            username = "johndoe",
            email = "john_doe@mail.com"
        )
        val existingUser = User(
            id = 1,
            username = "johndoe",
            email = "john_doe123@mail.com",
            apiKey = Uuid.random()
        )

        coEvery { userRepository.findByUsername(existingUser.username) } returns existingUser

        assertFailsWith<UsernameAlreadyExistsException> {
            useCase.execute(command)
        }

        coVerify(exactly = 0) { userRepository.save(any()) }
        coVerify(exactly = 1) { userRepository.findByUsername(existingUser.username) }
        coVerify(exactly = 0) { userRepository.findByEmail(any()) }
    }

    @Test
    fun `should create and return user when username is available`() = runTest {
        val command = CreateUserCommand(
            username = "johndoe",
            email = "john_doe@mail.com"
        )
        val savedUser = User(
            id = 1,
            username = "johndoe",
            email = "john_doe@mail.com",
            apiKey = Uuid.random()
        )

        coEvery { userRepository.findByUsername(command.username) } returns null
        coEvery { userRepository.findByEmail(command.email!!) } returns null
        coEvery { userRepository.save(any()) } returns savedUser

        val result = useCase.execute(command)

        assertEquals(command.username, result.username)
        assertEquals(command.email, result.email)
        assertEquals(savedUser.id, result.id)

        coVerify(exactly = 1) { userRepository.save(any())  }
        coVerify(exactly = 1) { userRepository.findByUsername(command.username) }
        coVerify(exactly = 1) { userRepository.findByEmail(command.email!!) }
    }

    @Test
    fun `should create and return user when username is available and does not have an email`() = runTest {
        val command = CreateUserCommand(username = "johndoe", email = null)
        val savedUser = User(
            id = 1,
            username = "johndoe",
            apiKey = Uuid.random()
        )

        coEvery { userRepository.findByUsername(command.username) } returns null
        coEvery { userRepository.save(any()) } returns savedUser

        val result = useCase.execute(command)

        assertEquals(command.username, result.username)
        assertEquals(null, result.email)
        assertEquals(savedUser.id, result.id)

        coVerify(exactly = 1) { userRepository.save(any())  }
        coVerify(exactly = 1) { userRepository.findByUsername(command.username) }
        coVerify(exactly = 0) { userRepository.findByEmail(any()) }
    }
}