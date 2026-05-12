package name.modid.client.config

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object ConfigScreen {

    fun create(parent: Screen?): Screen {

        // =========================
        // General Options
        // =========================

        val debugChatLoggingOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Debug Chat Logging"))
            .binding(
                false,
                { ConfigManager.config.debugChatLogging },
                { ConfigManager.config.debugChatLogging = it }
            )
            .controller(BooleanControllerBuilder::create)
            .build()

        // =========================
        // Shaft Options
        // =========================

        val announceShaftSpawnOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Announce Mineshaft Spawn"))
            .binding(
                true,
                { ConfigManager.config.announceShaftSpawn },
                { ConfigManager.config.announceShaftSpawn = it }
            )
            .controller(BooleanControllerBuilder::create)
            .build()

        val titleOnShaftSpawnOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Title on shaft spawn"))
            .binding(
                true,
                { ConfigManager.config.titleOnShaftSpawn },
                { ConfigManager.config.titleOnShaftSpawn = it }
            )
            .controller(BooleanControllerBuilder::create)
            .build()

        val transferOnShaftSpawnOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Transfer On Mineshaft Spawn"))
            .binding(
                true,
                { ConfigManager.config.transferOnShaftSpawn },
                { ConfigManager.config.transferOnShaftSpawn = it }
            )
            .controller(BooleanControllerBuilder::create)
            .build()

        val onlyVanguardLittleOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Only announce Vanguard and Littlefoot's den"))
            .binding(
                false,
                { ConfigManager.config.onlyVanguardLittle },
                { ConfigManager.config.onlyVanguardLittle = it }
            )
            .available(ConfigManager.config.announceOnShaftEnter)
            .controller(BooleanControllerBuilder::create)
            .build()

        val announceOnShaftEnterOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Announce entered shaft"))
            .binding(
                true,
                { ConfigManager.config.announceOnShaftEnter },
                {
                    ConfigManager.config.announceOnShaftEnter = it

                    onlyVanguardLittleOption.setAvailable(it)
                }
            )
            .listener { _, value ->
                onlyVanguardLittleOption.setAvailable(value)
            }
            .controller(BooleanControllerBuilder::create)
            .build()

        // =========================
        // Corpse Options
        // =========================

        val announceCorpsesOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Announce corpses into party chat"))
            .binding(
                true,
                { ConfigManager.config.announceCorpses },
                { ConfigManager.config.announceCorpses = it }
            )
            .controller(BooleanControllerBuilder::create)
            .build()

        val displayLapisOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Display Lapis"))
            .binding(
                true,
                { ConfigManager.config.displayLapis },
                { ConfigManager.config.displayLapis = it }
            )
            .controller(BooleanControllerBuilder::create)
            .build()

        // TODO: config for each shaft on min lapis to mine

        val playersToWarpOption = Option.createBuilder<String>()
            .name(Component.literal("List of players to warp"))
            .description(
                OptionDescription.of(
                    Component.literal("Comma separated list of usernames")
                )
            )
            .binding(
                "",
                {
                    ConfigManager.config.playersToWarp.joinToString(", ")
                },
                {
                    ConfigManager.config.playersToWarp =
                        it.split(",")
                            .map(String::trim)
                            .filter(String::isNotEmpty)
                            .toMutableList()
                }
            )
            .available(ConfigManager.config.corpseAutoWarp)
            .controller(StringControllerBuilder::create)
            .build()

        val minLapisOption = Option.createBuilder<Int>()
            .name(Component.literal("Minimum amount of lapis corpses to warp"))
            .description(
                OptionDescription.of(
                    Component.literal("Minimum amount of lapis corpses to warp. 0 to disable")
                )
            )
            .binding(
                0,
                { ConfigManager.config.minLapis },
                { ConfigManager.config.minLapis = it }
            )
            .available(ConfigManager.config.corpseAutoWarp)
            .controller(IntegerFieldControllerBuilder::create)
            .build()

        val minUmberOption = Option.createBuilder<Int>()
            .name(Component.literal("Minimum amount of umber corpses to warp"))
            .description(
                OptionDescription.of(
                    Component.literal("Minimum amount of umber corpses to warp. 0 to disable")
                )
            )
            .binding(
                0,
                { ConfigManager.config.minUmber },
                { ConfigManager.config.minUmber = it }
            )
            .available(ConfigManager.config.corpseAutoWarp)
            .controller(IntegerFieldControllerBuilder::create)
            .build()

        val minTungstenOption = Option.createBuilder<Int>()
            .name(Component.literal("Minimum amount of tungsten corpses to warp"))
            .description(
                OptionDescription.of(
                    Component.literal("Minimum amount of tungsten corpses to warp. 0 to disable")
                )
            )
            .binding(
                0,
                { ConfigManager.config.minTungsten },
                { ConfigManager.config.minTungsten = it }
            )
            .available(ConfigManager.config.corpseAutoWarp)
            .controller(IntegerFieldControllerBuilder::create)
            .build()

        val corpseAutoWarpOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Corpse auto warp"))
            .description(
                OptionDescription.of(
                    Component.literal("Will auto warp if ANY corpse conditions are met")
                )
            )
            .binding(
                false,
                { ConfigManager.config.corpseAutoWarp },
                { ConfigManager.config.corpseAutoWarp = it }
            )
            .listener { _, value ->

                playersToWarpOption.setAvailable(value)
                minLapisOption.setAvailable(value)
                minUmberOption.setAvailable(value)
                minTungstenOption.setAvailable(value)

            }
            .controller(BooleanControllerBuilder::create)
            .build()

        // =========================
        // Categories
        // =========================

        val generalCategory = ConfigCategory.createBuilder()
            .name(Component.literal("General"))
            .option(debugChatLoggingOption)
            .build()

        val shaftsCategory = ConfigCategory.createBuilder()
            .name(Component.literal("Shafts"))

            .group(OptionGroup.createBuilder()
                .name(Component.literal("Spawning"))
                .option(announceShaftSpawnOption)
                .option(titleOnShaftSpawnOption)
                .option(transferOnShaftSpawnOption)

                .build()
            )

            .group(OptionGroup.createBuilder()
                .name(Component.literal("Entered"))
                .option(announceOnShaftEnterOption)
                .option(onlyVanguardLittleOption)

                .build())

            .build()

        val corpsesCategory = ConfigCategory.createBuilder()
            .name(Component.literal("Corpses"))

            .group(OptionGroup.createBuilder()
                .name(Component.literal("General"))
                .option(announceCorpsesOption)
                .option(displayLapisOption)
                .build()
            )

           .group(OptionGroup.createBuilder()
                .name(Component.literal("Auto warp"))
                .description(OptionDescription.of(Component.literal("Config setting relating to auto warp")))
                .option(corpseAutoWarpOption)
                .option(playersToWarpOption)
                .option(minLapisOption)
                .option(minUmberOption)
                .option(minTungstenOption)

                .build()
           )

           .build()

        // =========================
        // Build Screen
        // =========================

        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Hideout Config"))

            .category(generalCategory)
            .category(shaftsCategory)
            .category(corpsesCategory)

            .save(ConfigManager::save)

            .build()
            .generateScreen(parent)
    }
}