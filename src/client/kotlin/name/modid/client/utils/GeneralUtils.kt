package name.modid.client.utils

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import java.util.Timer
import kotlin.concurrent.schedule

object GeneralUtils {

    const val BLACK = "§0"
    const val DARK_BLUE = "§1"
    const val DARK_GREEN = "§2"
    const val DARK_AQUA = "§3"
    const val DARK_RED = "§4"
    const val DARK_PURPLE = "§5"
    const val GOLD = "§6"
    const val GRAY = "§7"
    const val DARK_GRAY = "§8"
    const val BLUE = "§9"
    const val GREEN = "§a"
    const val AQUA = "§b"
    const val RED = "§c"
    const val LIGHT_PURPLE = "§d"
    const val YELLOW = "§e"
    const val WHITE = "§f"

    const val BOLD = "§l"
    const val ITALIC = "§o"
    const val UNDERLINE = "§n"
    const val RESET = "§r"

    private val timer = Timer()

    fun delay(ms: Long, task: () -> Unit) {

        timer.schedule(ms) {

            Minecraft.getInstance().execute {
                task()
            }
        }
    }


    fun debugChat(message: String) {
        Minecraft.getInstance().player?.displayClientMessage(
            Component.literal("${AQUA}[HO DEBUG]$RESET $message"),
            false
        )
    }
}