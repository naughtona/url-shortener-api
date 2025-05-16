package urlShortener.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UrlShortenerServiceTest {
    private val urlShortenerService = UrlShortenerService()

    @Nested
    inner class ShortenUrl {
        @Test
        fun `shortenUrl generates short url for valid long url`() {
            val longUrl = "https://example.com"
            val shortUrl = urlShortenerService.shortenUrl(longUrl)

            assertTrue(shortUrl.startsWith("https://short.ly/"))
            assertEquals(longUrl, urlShortenerService.getLongUrl(shortUrl))
        }

        @Test
        fun `shortenUrl throws Exception for invalid long url input`() {
            val invalidLongUrl = "invalid-url"

            val exception = assertThrows(ResponseStatusException::class.java) {
                urlShortenerService.shortenUrl(invalidLongUrl)
            }

            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.statusCode.value())
            assertEquals("Invalid Long URL format", exception.reason)
        }

        @Test
        fun `shortenUrl returns same short url for same long url`() {
            val longUrl = "https://example.com"
            val firstShortUrl = urlShortenerService.shortenUrl(longUrl)
            val secondShortUrl = urlShortenerService.shortenUrl(longUrl)

            assertEquals(firstShortUrl, secondShortUrl)
        }

        @Test
        fun `shortenUrl handles empty long url input`() {
            val emptyLongUrl = ""

            val exception = assertThrows(ResponseStatusException::class.java) {
                urlShortenerService.shortenUrl(emptyLongUrl)
            }

            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.statusCode.value())
            assertEquals("Invalid Long URL format", exception.reason)
        }
    }

    @Nested
    inner class GetLongUrl {
        @Test
        fun `getLongUrl returns original url for valid short url`() {
            val longUrl = "https://example.com"
            val shortUrl = urlShortenerService.shortenUrl(longUrl)

            val resolvedUrl = urlShortenerService.getLongUrl(shortUrl)

            assertEquals(longUrl, resolvedUrl)
        }

        @Test
        fun `getLongUrl throws Exception for unfound short url`() {
            val invalidShortUrl = "https://short.ly/invalid"

            val exception = assertThrows(ResponseStatusException::class.java) {
                urlShortenerService.getLongUrl(invalidShortUrl)
            }

            assertEquals(HttpStatus.NOT_FOUND.value(), exception.statusCode.value())
            assertEquals("Short URL not found", exception.reason)
        }

        @Test
        fun `getLongUrl handles empty short url input`() {
            val emptyShortUrl = ""

            val exception = assertThrows(ResponseStatusException::class.java) {
                urlShortenerService.getLongUrl(emptyShortUrl)
            }

            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.statusCode.value())
            assertEquals("Invalid Short URL format", exception.reason)
        }

        @Test
        fun `getLongUrl throws Exception for short url with invalid prefix`() {
            val invalidPrefixShortUrl = "https://invalid.ly/abc123"

            val exception = assertThrows(ResponseStatusException::class.java) {
                urlShortenerService.getLongUrl(invalidPrefixShortUrl)
            }

            assertEquals(HttpStatus.BAD_REQUEST.value(), exception.statusCode.value())
            assertEquals("Invalid Short URL format", exception.reason)
        }
    }

}