package land.vani.plugin.domain.repository

import land.vani.plugin.domain.model.MCBansInfo
import land.vani.plugin.domain.model.Player

interface MCBansRepository {
    suspend fun lookupPlayer(player: Player): MCBansInfo
}
