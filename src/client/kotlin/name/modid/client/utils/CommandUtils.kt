package name.modid.client.utils


import name.modid.client.config.ConfigScreen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.Minecraft


object CommandUtils {

    fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

            // Main command
            dispatcher.register(literal("hideout").executes {

                try {

                    Minecraft.getInstance().execute {
                        Minecraft.getInstance().setScreen(
                            ConfigScreen.create(
                                Minecraft.getInstance().screen
                            )
                        )
                    }


                } catch (e: Exception) {

                    e.printStackTrace()
                }

                1
            })
        }
        /*
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

            dispatcher.register(literal("testrandom").executes { ctx ->

                GeneralUtils.debugChat("players: ${LocationAPI.playerCount}")
                1

            })
        }*/
    }
}