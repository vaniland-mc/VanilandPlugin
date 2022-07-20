package land.vani.plugin.core.util

import org.bukkit.Location

fun Location.formattedString(): String =
    "${world.name}/$blockX/$blockY/$blockZ"
