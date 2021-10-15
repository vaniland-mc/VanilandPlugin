package land.vani.plugin.gateway.mcbans

import land.vani.plugin.gateway.mcbans.model.MCBansLookupResponse
import java.util.UUID

interface MCBansGateway {
    suspend fun lookupPlayer(uuid: UUID): MCBansLookupResponse?
}
