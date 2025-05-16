package urlShortener.graphql

import com.netflix.dgs.codegen.generated.types.ResolvedUrlResponse
import urlShortener.service.UrlShortenerService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import org.slf4j.LoggerFactory

@DgsComponent
class Queries(
    private val urlShortenerService: UrlShortenerService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

    }

    @DgsQuery
    fun resolveUrl(urlWithShortKey: String): ResolvedUrlResponse {
        logger.info("Executing resolveUrl query with urlWithShortKey: $urlWithShortKey")
        val longUrl = urlShortenerService.getLongUrl(urlWithShortKey)
        return ResolvedUrlResponse(longUrl)
    }
}