package land.vani.plugin.gateway.mcbans.model

import kotlinx.serialization.Serializable

@Serializable
data class MCBansLookupResponse(
    val total: Int,
    val reputation: Double,
    val local: List<String>,
    val global: List<String>,
)
