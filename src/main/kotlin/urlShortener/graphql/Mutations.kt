package urlShortener.graphql

import urlShortener.service.UrlShortenerService
import com.netflix.dgs.codegen.generated.types.ShortenedUrlResponse
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import org.slf4j.LoggerFactory

@DgsComponent
class Mutations(
    private val urlShortenerService: UrlShortenerService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @DgsMutation
    fun shortenUrl(longUrl: String): ShortenedUrlResponse {
        logger.info("Executing shortenUrl mutation with longUrl: $longUrl")
        val shortUrl = urlShortenerService.shortenUrl(longUrl)
        return ShortenedUrlResponse(shortUrl)
    }
}