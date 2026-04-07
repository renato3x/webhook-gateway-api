package domain.value

import dev.renato3x.domain.exception.DomainException
import dev.renato3x.domain.value.Url
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UrlTest {
    @Test
    fun `should create valid url`() {
        val url = Url("https://example.com")
        assertEquals(url.value, "https://example.com")
    }

    @Test
    fun `should throw when url is blank`() {
        assertFailsWith<DomainException> { Url("") }
    }

    @Test
    fun `should throw when url has no scheme`() {
        assertFailsWith<DomainException> { Url("example.com") }
    }

    @Test
    fun `should throw when scheme is not http or https`() {
        assertFailsWith<DomainException> { Url("ftp://example.com") }
    }

    @Test
    fun `should throw when url points to localhost`() {
        assertFailsWith<DomainException> { Url("http://localhost:8080/webhook") }
    }
}
