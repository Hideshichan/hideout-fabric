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
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI


object MineshaftEnter {

    private fun warpAndDisband() {
        if (warped) return

        warped = true
        waitingForPlayers = false

        McClient.sendCommand("/p warp")

        GeneralUtils.delay(1000L) {
            McClient.sendCommand("/p disband")
        }
    }

    @Subscription
    fun onEnterMineshaft(event: MineshaftEnteredEvent) {
        val type = event.type
        val variant = event.variant

        //println("shaft: $type")

        // MAYHEM! Your Suspicious Scrap chance was buffed by your Mineshaft Mayhem perk!
        // MAYHEM! Your Pickaxe Ability cooldown was reduced from your Mineshaft Mayhem perk!
        // MAYHEM! You received a ☘ Mining Fortune buff from your Mineshaft Mayhem perk!
        // MAYHEM! You received a ⸕ Mining Speed buff from your Mineshaft Mayhem perk!
        // MAYHEM! You received a ❄ Cold Resistance buff from your Mineshaft Mayhem perk!

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
        val corpses = event.corpses
        val totalLapis = corpses.count { it.type == CorpseType.LAPIS }
        val totalUmber = corpses.count { it.type == CorpseType.UMBER }
        val totalTungsten = corpses.count { it.type == CorpseType.TUNGSTEN }
        val isMineshaftOwner = LocationAPI.playerCount == 1
        //println("corpses: $corpses")
        // corpses: [Corpse(type=Umber, looted=false), Corpse(type=Tungsten, looted=false)]

        if (ConfigManager.config.announceCorpses && PartyAPI.inParty) McClient.sendCommand("/pc $totalLapis Lapis, $totalUmber Umber, $totalTungsten Tungsten")

        // TODO: replace subtitle with a check for if you should mine, based on user config
        if (ConfigManager.config.displayLapis) McClient.setTitle(Component.literal("${GeneralUtils.BLUE}${totalLapis}L"), Component.literal(if (totalLapis == 4) "Mine this" else ""), 0.25f, 1f, 0.25f)


        // if config = 0, means disabled
        val lapisCheck = (totalLapis >= ConfigManager.config.minLapis && ConfigManager.config.minLapis > 0)
        val umberCheck = (totalUmber >= ConfigManager.config.minUmber && ConfigManager.config.minUmber > 0)
        val tungstenCheck = (totalTungsten >= ConfigManager.config.minTungsten && ConfigManager.config.minTungsten > 0)

        // want to warp for ANY req met
        if (ConfigManager.config.corpseAutoWarp && (lapisCheck || umberCheck || tungstenCheck)) {

            if (!isMineshaftOwner) return

            val playersToWarp = ConfigManager.config.playersToWarp
            waitingForPlayers = true
            warped = false
            playersJoined = 0

            if (ConfigManager.config.announceCorpses) {
                GeneralUtils.delay(1000) {McClient.sendCommand("/p ${playersToWarp.joinToString(separator = " ")}")}
            }
            else {
                McClient.sendCommand("/p ${playersToWarp.joinToString(separator = " ")}")
            }


            GeneralUtils.delay(12000L) {
                if (warped) return@delay
                McClient.sendCommand("/pc Warping in 3 seconds.")
                GeneralUtils.delay(3000L) {

                    if (ConfigManager.config.debugChatLogging) GeneralUtils.debugChat("waitingForPlayers: $waitingForPlayers, warped: $warped, playersJoined: $playersJoined, inParty: ${PartyAPI.inParty}")

                    if (waitingForPlayers && PartyAPI.inParty && !warped) {
                        if (playersJoined > 0) {
                            warpAndDisband()
                        }
                        else {
                            waitingForPlayers = false
                            McClient.sendCommand("/p disband")
                        }
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
            warpAndDisband()
        }
    }
}