package name.modid

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Hideout : ModInitializer {
    private val logger = LoggerFactory.getLogger("hideout")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}
}