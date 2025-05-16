package urlShortener.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

@Service
class UrlShortenerService {

    private val shortKeyToLongUrlMap = ConcurrentHashMap<String, String>()
    private val longUrlToShortKeyMap = ConcurrentHashMap<String, String>()
    private val seededRandom = Random(RANDOM_SEED)

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val SHORTENED_URL_PREFIX = "https://short.ly/"
        private const val MAX_RETRIES = 5
        private const val RANDOM_SEED = 42
        private const val SHORT_URL_LENGTH = 6
        private const val INVALID_LONG_URL_MESSAGE = "Invalid Long URL format"
        private const val INVALID_SHORT_URL_MESSAGE = "Invalid Short URL format"
        private const val SHORT_URL_NOT_FOUND_MESSAGE = "Short URL not found"
        private const val HTTP_PREFIX = "http://"
        private const val HTTPS_PREFIX = "https://"
        private val ALLOWED_CHARS = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }

    @Synchronized
    fun shortenUrl(longUrl: String): String {
        validateLongUrl(longUrl)
        idempotentCheck(longUrl)?.let { return it }

        logger.info("Shortening URL: $longUrl")
        val shortKey = generateShortKey()
        shortKeyToLongUrlMap[shortKey] = longUrl
        longUrlToShortKeyMap[longUrl] = shortKey

        val shortUrl = makeShortUrl(shortKey)
        logger.info("Generated short URL: $shortKey for long URL: $longUrl")

        return shortUrl
    }

    fun getLongUrl(urlWithShortKey: String): String {
        validateUrlWithShortKey(urlWithShortKey)
        val shortKey = urlWithShortKey.removePrefix(SHORTENED_URL_PREFIX)

        return shortKeyToLongUrlMap[shortKey] ?:
        throw ResponseStatusException(HttpStatus.NOT_FOUND, SHORT_URL_NOT_FOUND_MESSAGE)
    }

    private fun validateLongUrl(longUrl: String) {
        if (!longUrl.startsWith(HTTP_PREFIX) && !longUrl.startsWith(HTTPS_PREFIX)) {
            logger.error("Invalid long URL format: $longUrl")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_LONG_URL_MESSAGE)
        }
    }

    private fun validateUrlWithShortKey(urlWithShortKey: String) {
        if (!urlWithShortKey.startsWith(SHORTENED_URL_PREFIX)) {
            logger.error("Invalid short URL format: $urlWithShortKey")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_SHORT_URL_MESSAGE)
        }
    }

    private fun makeShortUrl(shortKey: String) = SHORTENED_URL_PREFIX + shortKey

    private fun generateShortKey(): String {
        var shortKey: String
        var retries = 0
        do {
            shortKey = generateRandomString()
            if (shortKeyToLongUrlMap.containsKey(shortKey)) {
                logger.info("Collision detected for short key: $shortKey, retrying...")
                retries++
            } else {
                break
            }
        } while (retries < MAX_RETRIES)

        if (retries == MAX_RETRIES) {
            throw IllegalStateException("Failed to generate unique short key after $MAX_RETRIES attempts")
        }

        return shortKey
    }

    private fun generateRandomString() = (1..SHORT_URL_LENGTH)
        .map { ALLOWED_CHARS.random(seededRandom) }
        .joinToString("")

    private fun idempotentCheck(longUrl: String): String? {
        return if (longUrlToShortKeyMap.containsKey(longUrl)) {
            logger.info("URL already shortened: $longUrl")
            val shortKey =
                longUrlToShortKeyMap[longUrl] ?: throw IllegalStateException("Short key not found for long URL")
            makeShortUrl(shortKey)
        } else {
            null
        }
    }

}