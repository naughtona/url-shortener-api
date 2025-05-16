package urlShortener.graphql

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import urlShortener.service.UrlShortenerService

class MutationsTest {
    private val urlShortenerService = mock(UrlShortenerService::class.java)
    private val mutations = Mutations(urlShortenerService)

    @Test
    fun `shortenUrl returns ShortenedUrlResponse when valid long url provided`() {
        val longUrl = "https://example.com"
        val shortUrl = "https://short.ly/abc123"
        `when`(urlShortenerService.shortenUrl(longUrl)).thenReturn(shortUrl)

        val response = mutations.shortenUrl(longUrl)

        assertEquals(shortUrl, response.shortUrl)
    }

    @Test
    fun `shortenUrl throws Exception when long url is invalid`() {
        val invalidLongUrl = "invalid-url"
        `when`(urlShortenerService.shortenUrl(invalidLongUrl))
            .thenThrow(ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid Long URL format"
            ))

        val exception = assertThrows(ResponseStatusException::class.java) {
            mutations.shortenUrl(invalidLongUrl)
        }

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.statusCode.value())
        assertEquals("Invalid Long URL format", exception.reason)
    }
}