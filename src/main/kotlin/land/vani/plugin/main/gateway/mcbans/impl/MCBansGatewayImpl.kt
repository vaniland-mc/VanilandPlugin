package land.vani.plugin.main.gateway.mcbans.impl

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import land.vani.plugin.main.config.MCBansConfig
import land.vani.plugin.main.gateway.mcbans.MCBansGateway
import land.vani.plugin.main.gateway.mcbans.model.MCBansLookupResponse
import java.util.UUID
import java.util.concurrent.TimeUnit

class MCBansGatewayImpl(
    private val client: HttpClient,
    private val config: MCBansConfig,
) : MCBansGateway {
    @Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
    override suspend fun lookupPlayer(uuid: UUID): MCBansLookupResponse? {
        // キャッシュにヒットしたらそのまま返す
        cache.getIfPresent(uuid)?.let { return it }

        val response = client.submitForm<HttpResponse>(
            url = "https://api.mcbans.com/v3/${config.apiKey}",
            formParameters = Parameters.build {
                append("player_uuid", "$uuid")
                append("admin", "console")
                append("admin_uuid", "fb7afb6de9ea47399893616134617808")
                append("exec", "playerLookup")
            }
        )

        return if (response.status != HttpStatusCode.OK) {
            null
        } else response.receive()
    }

    companion object {
        private val cache: Cache<UUID, MCBansLookupResponse> = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build()
    }
}
