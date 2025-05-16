package urlShortener.graphql

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import urlShortener.service.UrlShortenerService

class QueriesTest {
    private val urlShortenerService = mock(UrlShortenerService::class.java)
    private val queries = Queries(urlShortenerService)

    @Test
    fun resolveUrlReturnsLongUrlWhenShortKeyExists() {
        val shortKey = "abc123"
        val longUrl = "https://example.com"
        `when`(urlShortenerService.getLongUrl(shortKey)).thenReturn(longUrl)

        val response = queries.resolveUrl(shortKey)

        assertEquals(longUrl, response.longUrl)
    }

    @Test
    fun `resolveUrl throws NotFound Exception WhenShortKeyDoesNotExist`() {
        val shortUrl = "http://short.ly/123abc"
        `when`(urlShortenerService.getLongUrl(shortUrl)).thenThrow(ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Short URL not found"
        ))

        val exception = assertThrows(ResponseStatusException::class.java) {
            queries.resolveUrl(shortUrl)
        }

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.statusCode.value())
        assertEquals("Short URL not found", exception.reason)
    }
}