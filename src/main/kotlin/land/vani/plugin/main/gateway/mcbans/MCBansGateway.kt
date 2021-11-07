package land.vani.plugin.main.gateway.mcbans

import land.vani.plugin.main.gateway.mcbans.model.MCBansLookupResponse
import java.util.UUID

interface MCBansGateway {
    suspend fun lookupPlayer(uuid: UUID): MCBansLookupResponse?
}
