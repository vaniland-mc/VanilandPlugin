package land.vani.plugin.main.listener.group

import com.github.syari.spigot.api.event.Events
import com.github.ucchyocean.lc3.bukkit.event.LunaChatBukkitChannelCreateEvent
import com.github.ucchyocean.lc3.bukkit.event.LunaChatBukkitChannelMemberChangedEvent
import com.github.ucchyocean.lc3.bukkit.event.LunaChatBukkitChannelRemoveEvent
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.MetaNode

fun Events.registerGroupIntegration(luckPerms: LuckPerms) {
    val groupManager = luckPerms.groupManager
    val userManager = luckPerms.userManager

    event<LunaChatBukkitChannelCreateEvent> { event ->
        if (groupManager.getGroup(event.channelName) != null) {
            event.isCancelled = true
            event.member.sendMessage("&c同名のチャンネルが既に存在します")
            return@event
        }
        groupManager.createAndLoadGroup(event.channelName)
        groupManager.modifyGroup(event.channelName) { group ->
            group.data().add(MetaNode.builder("receive-mail", "true").build())
        }
    }

    event<LunaChatBukkitChannelRemoveEvent> { event ->
        val group = groupManager.getGroup(event.channelName) ?: return@event

        if (group.cachedData.metaData.getMetaValue("admin-managed") == "true") return@event
        groupManager.deleteGroup(group)
    }

    event<LunaChatBukkitChannelMemberChangedEvent> { event ->
        val before = event.membersBefore
        val after = event.membersAfter
        val group = groupManager.getGroup(event.channelName) ?: return@event

        if (before.size < after.size) {
            // メンバー追加
            val addedMember = after.find { !before.contains(it) } ?: return@event
            val user = userManager.getUser(addedMember.name) ?: return@event
            userManager.modifyUser(user.uniqueId) {
                it.data().add(InheritanceNode.builder(group).build())
            }
        } else {
            // メンバー削除
            val removedMember = before.find { !after.contains(it) } ?: return@event
            val user = userManager.getUser(removedMember.name) ?: return@event
            userManager.modifyUser(user.uniqueId) {
                it.data().remove(InheritanceNode.builder(group).build())
            }
        }
    }
}
