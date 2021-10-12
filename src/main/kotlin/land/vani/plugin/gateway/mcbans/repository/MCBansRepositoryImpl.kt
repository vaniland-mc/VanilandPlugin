package land.vani.plugin.gateway.mcbans.repository

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import land.vani.plugin.domain.model.MCBansInfo
import land.vani.plugin.domain.model.Player
import land.vani.plugin.domain.repository.MCBansRepository
import land.vani.plugin.gateway.mcbans.config.MCBansConfig
import land.vani.plugin.gateway.mcbans.model.LookupResponse
import java.util.UUID
import java.util.concurrent.TimeUnit

class MCBansRepositoryImpl(
    private val client: HttpClient,
    private val config: MCBansConfig
): MCBansRepository {
    @Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
    override suspend fun lookupPlayer(player: Player): MCBansInfo {
        // キャッシュにヒットしたらそのまま返す
        cache.getIfPresent(player.uuid)?.let { return it }

        val response = client.submitForm<LookupResponse>(
            url = "https://api.mcbans.com/v3/11afe8828d77c30927e91595b497e6acb77eb11a",
            formParameters = Parameters.build {
                append("player_uuid", "67b32f9e-0dd1-47ee-a7f3-65ee643a1688")
                append("admin", "console")
                append("admin_uuid", "fb7afb6de9ea47399893616134617808")
                append("exec", "playerLookup")
            }
        )

        return MCBansInfo(
            reputation = response.reputation,
            localBans = response.local,
            globalBans = response.global
        )
    }

    companion object {
        private val cache: Cache<UUID, MCBansInfo> = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build()
    }
}
