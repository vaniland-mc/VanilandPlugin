package land.vani.plugin.core

import land.vani.mcorouhlin.permission.Permission
import land.vani.mcorouhlin.permission.PermissionDefault

enum class Permissions(
    override val node: String,
    override val description: String? = null,
    override val children: Map<Permission, Boolean> = mapOf(),
    override val default: PermissionDefault? = null,
) : Permission {
    PORTAL_WARP(
        "vaniland.portal",
        "Allows you to use the portal warp command.",
        default = PermissionDefault.OP,
    ),
    ADMIN(
        "vaniland.admin",
        "VanilandPlugin admin command",
        default = PermissionDefault.OP
    ),
    ;
}
