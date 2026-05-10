package name.modid.client.features

import name.modid.client.utils.GeneralUtils
import name.modid.client.config.ConfigManager
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.MineshaftFoundEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import net.minecraft.network.chat.Component

object MineshaftSpawn {
    @Subscription
    fun onMineshaftSpawn(event: MineshaftFoundEvent) {
        if (ConfigManager.config.announceShaftSpawn) McClient.sendCommand("/pc Mineshaft Spawned!")
        if (ConfigManager.config.transferOnShaftSpawn) GeneralUtils.delay(500) { McClient.sendCommand("/pc !pt") }
        if (ConfigManager.config.titleOnShaftSpawn) {McClient.setTitle(Component.literal("Mineshaft Spawned!"), Component.literal(""), 0.25f, 1f, 0.25f)}
    }
}