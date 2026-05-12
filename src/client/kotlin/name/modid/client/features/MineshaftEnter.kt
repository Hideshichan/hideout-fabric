package name.modid.client.features

import name.modid.client.config.ConfigManager
import name.modid.client.utils.GeneralUtils
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft.CorpseType
import tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft.MineshaftType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.CorpseSpawnEvent
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.MineshaftEnteredEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI


object MineshaftEnter {

    @Subscription
    fun onEnterMineshaft(event: MineshaftEnteredEvent) {
        val type = event.type
        val variant = event.variant

        //println("shaft: $type")

        // Type title
        McClient.setTitle(Component.literal(type.toString()), Component.literal(""), 0.25f, 1f, 0.25f)
        // Announce shaft
        if (ConfigManager.config.announceOnShaftEnter && !ConfigManager.config.onlyVanguardLittle) {
            if (type != MineshaftType.VANGUARD && type != MineshaftType.LITTLE) {
                McClient.sendCommand("/pc Entered $type $variant Mineshaft!")
            }
            else {
                McClient.sendCommand("/pc Entered $type Mineshaft!")
            }
        }
        else if (ConfigManager.config.announceOnShaftEnter && ConfigManager.config.onlyVanguardLittle && (type == MineshaftType.LITTLE || type == MineshaftType.VANGUARD)) McClient.sendCommand("/pc Entered $type Mineshaft!")

    }

    private var waitingForPlayers = false
    private val otherJoinedRegex = Regex("^(?:\\[.+] )?(?<member>[a-zA-Z0-9_]+) joined the party\\.$")
    private var playersJoined = 0
    private var warped = false

    @Subscription
    fun onCorpseLoad(event: CorpseSpawnEvent) {
        var totalLapis = 0
        var totalUmber = 0
        var totalTungsten  = 0
        val corpses = event.corpses
        //println("corpses: $corpses")
        // corpses: [Corpse(type=Umber, looted=false), Corpse(type=Tungsten, looted=false)]

        for (corpse in corpses) {
            if (corpse.type == CorpseType.LAPIS) totalLapis++
            if (corpse.type == CorpseType.UMBER) totalUmber++
            if (corpse.type == CorpseType.TUNGSTEN) totalTungsten++
        }
        if (ConfigManager.config.announceCorpses) McClient.sendCommand("/pc $totalLapis Lapis, $totalUmber Umber, $totalTungsten Tungsten")

        // TODO: replace subtitle with a check for if you should mine, based on user config
        if (ConfigManager.config.displayLapis) McClient.setTitle(Component.literal("${GeneralUtils.BLUE}${totalLapis}L"), Component.literal(if (totalLapis == 4) "Mine this" else ""), 0.25f, 1f, 0.25f)


        // if config = 0, means disabled
        val lapisCheck = (totalLapis >= ConfigManager.config.minLapis && ConfigManager.config.minLapis > 0)
        val umberCheck = (totalUmber >= ConfigManager.config.minUmber && ConfigManager.config.minUmber > 0)
        val tungstenCheck = (totalTungsten >= ConfigManager.config.minTungsten && ConfigManager.config.minTungsten > 0)

        // want to warp for ANY req met
        if (ConfigManager.config.corpseAutoWarp && (lapisCheck || umberCheck || tungstenCheck)) {

            val playersToWarp = ConfigManager.config.playersToWarp
            waitingForPlayers = true
            warped = false

            if (ConfigManager.config.announceCorpses) {
                GeneralUtils.delay(1000) {McClient.sendCommand("/p ${playersToWarp.joinToString(separator = " ")}")}
            }
            else {
                McClient.sendCommand("/p ${playersToWarp.joinToString(separator = " ")}")
            }


            GeneralUtils.delay(15000L) {
                if (waitingForPlayers && PartyAPI.inParty && !warped) {
                    if (playersJoined > 0) {
                        McClient.sendCommand("/p warp")
                        warped = true
                    }
                    waitingForPlayers = false
                    GeneralUtils.delay(1000L) {
                        McClient.sendCommand("/p disband")
                    }
                }
            }
        }
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent) {

        if (!waitingForPlayers) return
        if (!otherJoinedRegex.matches(event.text)) return

        playersJoined++

        val requiredPlayers =
            ConfigManager.config.playersToWarp.map { it.lowercase() }

        val currentMembers =
            PartyAPI.members.mapNotNull { it.name?.lowercase() }

        val everyoneJoined =
            requiredPlayers.all { it in currentMembers }

        if (everyoneJoined) {

            waitingForPlayers = false

            McClient.sendCommand("/p warp")
            warped = true

            GeneralUtils.delay(1000L) {
                McClient.sendCommand("/p disband")
            }
        }
    }
}