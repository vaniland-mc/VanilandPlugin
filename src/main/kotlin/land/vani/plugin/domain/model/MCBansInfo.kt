package land.vani.plugin.domain.model

/**
 * MCBansでのBAN履歴など
 *
 * @property reputation 評価値
 */
data class MCBansInfo(
    val reputation: Double,
    val localBans: List<String>,
    val globalBans: List<String>
)
