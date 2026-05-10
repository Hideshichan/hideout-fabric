package name.modid.client

import name.modid.client.utils.CommandUtils
import name.modid.client.config.ConfigManager
import name.modid.client.features.MineshaftEnter
import name.modid.client.features.MineshaftSpawn
import net.fabricmc.api.ClientModInitializer
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI


object HideoutClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CommandUtils.init()
		//ShaftRoutes.init()
		ConfigManager.load()

		val listeners = listOf(
			MineshaftSpawn,
			MineshaftEnter
		)
		listeners.forEach {
			SkyBlockAPI.eventBus.register(it)
		}
	}

}