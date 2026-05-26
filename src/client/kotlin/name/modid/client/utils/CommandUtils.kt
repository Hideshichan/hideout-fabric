package name.modid.client.utils


import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import name.modid.client.config.ConfigScreen
import name.modid.client.features.GemstoneSell
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component


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

            dispatcher.register(
                literal("bestsell").executes {

                    val results = GemstoneSell.calculateAll()

                    Minecraft.getInstance().player?.displayClientMessage(Component.literal("[${GeneralUtils.AQUA}HIDEOUT${GeneralUtils.RESET}] Best way to sell each gemstone (per flawless):"), false)

                    results.forEach {

                        val gem = it.gemstone

                        Minecraft.getInstance().player?.displayClientMessage(
                            Component.literal(
                                "${gem.color}${gem.name}${GeneralUtils.RESET}\n" +

                                "  §7Insta Sell: ${it.instaBest} " +
                                "(+${GemstoneSell.formatCoins(it.instaDifference)})\n" +

                                "  §aSell Offer: ${it.offerBest} " +
                                "(+${GemstoneSell.formatCoins(it.offerDifference)})"
                            ),
                            false
                        )
                    }

                    1
                }

                    // /bestsell <gemstone>
                    .then(
                        argument("gemstone", StringArgumentType.word())
                            .executes { ctx ->

                                val gem = GemstoneSell.fromString(
                                    StringArgumentType.getString(ctx, "gemstone")
                                ) ?: run {

                                    Minecraft.getInstance().player?.displayClientMessage(
                                        Component.literal("${GeneralUtils.RED}Unknown gemstone"),
                                        false
                                    )
                                    return@executes 0
                                }

                                val result = GemstoneSell.calculateGem(gem)

                                Minecraft.getInstance().player?.displayClientMessage(
                                    Component.literal(
                                        "${gem.color}${gem.name}${GeneralUtils.RESET}\n" +

                                                "  §7Insta Sell: ${result.instaBest} " +
                                                "(+${GemstoneSell.formatCoins(result.instaDifference)})\n" +

                                                "  §aSell Offer: ${result.offerBest} " +
                                                "(+${GemstoneSell.formatCoins(result.offerDifference)})"
                                    ),
                                    false
                                )

                                1
                            }

                            // /bestsell <gemstone> <amount>
                            .then(
                                argument("amount", IntegerArgumentType.integer(1))
                                    .executes { ctx ->

                                        val gem = GemstoneSell.fromString(
                                            StringArgumentType.getString(ctx, "gemstone")
                                        ) ?: run {

                                            Minecraft.getInstance().player?.displayClientMessage(
                                                Component.literal("${GeneralUtils.RED}Unknown gemstone"),
                                                false
                                            )
                                            return@executes 0
                                        }

                                        val amount =
                                            IntegerArgumentType.getInteger(ctx, "amount")

                                        val result =
                                            GemstoneSell.calculateGem(gem, amount)

                                        Minecraft.getInstance().player?.displayClientMessage(
                                            Component.literal(
                                                "${gem.color}${gem.name}${GeneralUtils.RESET}\n" +

                                                        "  §7Insta Sell: ${result.instaBest} " +
                                                        "(+${GemstoneSell.formatCoins(result.instaDifference)})\n" +

                                                        "  §aSell Offer: ${result.offerBest} " +
                                                        "(+${GemstoneSell.formatCoins(result.offerDifference)})"
                                            ),
                                            false
                                        )

                                        1
                                    }
                            )
                    )
            )
        }
    }
}