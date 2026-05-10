package name.modid.client.config

import com.google.gson.GsonBuilder
import net.minecraft.client.Minecraft
import java.io.File

object ConfigManager {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val file = File(
        Minecraft.getInstance().gameDirectory,
        "config/hideout/config.json"
    )

    var config = ModConfig()

    fun load() {

        if (!file.exists()) {
            save()
            return
        }

        config = gson.fromJson(
            file.readText(),
            ModConfig::class.java
        )
    }

    fun save() {

        file.parentFile.mkdirs()

        file.writeText(
            gson.toJson(config)
        )
    }
}