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
        if (ConfigManager.config.debugChatLogging) GeneralUtils.debugChat("Shaft type: $type, Variant = $variant")

        // TODO: this, put buff in should mine subtitle?
        // MAYHEM! Your Suspicious Scrap chance was buffed by your Mineshaft Mayhem perk!
        // MAYHEM! Your Pickaxe Ability cooldown was reduced from your Mineshaft Mayhem perk!
        // MAYHEM! You received a ☘ Mining Fortune buff from your Mineshaft Mayhem perk!
        // MAYHEM! You received a ⸕ Mining Speed buff from your Mineshaft Mayhem perk!
        // MAYHEM! You received a ❄ Cold Resistance buff from your Mineshaft Mayhem perk!

        // /^MAYHEM! You(r?) (received a )?(Suspicious Scrap chance|Pickaxe Ability cooldown|☘ Mining Fortune buff|⸕ Mining Speed buff|❄ Cold Resistance buff)?( was reduced| was buffed by)?( from)? your Mineshaft Mayhem perk!$/
        // https://regex101.com/?regex=%5EMAYHEM%21+You%28r%3F%29+%28received+a+%29%3F%28Suspicious+Scrap+chance%7CPickaxe+Ability+cooldown%7C%E2%98%98+Mining+Fortune+buff%7C%E2%B8%95+Mining+Speed+buff%7C%E2%9D%84+Cold+Resistance+buff%29%3F%28+was+reduced%7C+was+buffed+by%29%3F%28+from%29%3F+your+Mineshaft+Mayhem+perk%21%24&testString=MAYHEM%21+Your+Suspicious+Scrap+chance+was+buffed+by+your+Mineshaft+Mayhem+perk%21%0AMAYHEM%21+Your+Pickaxe+Ability+cooldown+was+reduced+from+your+Mineshaft+Mayhem+perk%21%0AMAYHEM%21+You+received+a+%E2%98%98+Mining+Fortune+buff+from+your+Mineshaft+Mayhem+perk%21%0AMAYHEM%21+You+received+a+%E2%B8%95+Mining+Speed+buff+from+your+Mineshaft+Mayhem+perk%21%0AMAYHEM%21+You+received+a+%E2%9D%84+Cold+Resistance+buff+from+your+Mineshaft+Mayhem+perk%21&flags=gm&flavor=pcre2&delimiter=%2F

        // regex.matches(message)
        // buff = group3

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

        if (ConfigManager.config.debugChatLogging) GeneralUtils.debugChat("Corpse total: $corpses, Is Owner of mineshaft (guess): $isMineshaftOwner")

        if (ConfigManager.config.announceCorpses && PartyAPI.inParty) McClient.sendCommand("/pc $totalLapis Lapis, $totalUmber Umber, $totalTungsten Tungsten")

        // TODO: replace subtitle with a check for if you should mine, based on user config
        if (ConfigManager.config.displayLapis) McClient.setTitle(Component.literal("${GeneralUtils.BLUE}${totalLapis}L"), Component.literal(if (totalLapis == 4) "Mine this" else ""), 0.25f, 1f, 0.25f)


        // if config = 0, means disabled
        val lapisCheck = (totalLapis >= ConfigManager.config.minLapis && ConfigManager.config.minLapis > 0)
        val umberCheck = (totalUmber >= ConfigManager.config.minUmber && ConfigManager.config.minUmber > 0)
        val tungstenCheck = (totalTungsten >= ConfigManager.config.minTungsten && ConfigManager.config.minTungsten > 0)

        if (ConfigManager.config.debugChatLogging) {
            GeneralUtils.debugChat("Corpse Breakdown: Lapis: $totalLapis, Umber: $totalUmber, Tungsten: $totalTungsten")
            GeneralUtils.debugChat("Warp?: Lapis: $lapisCheck, umber: $umberCheck, Tungsten: $tungstenCheck")
        }

        // want to warp for ANY req met
        if (ConfigManager.config.corpseAutoWarp && (lapisCheck || umberCheck || tungstenCheck)) {

            if (!isMineshaftOwner) return

            val playersToWarp = ConfigManager.config.playersToWarp
            if (ConfigManager.config.debugChatLogging) GeneralUtils.debugChat("Players to warp: $playersToWarp")
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
                if (ConfigManager.config.debugChatLogging) GeneralUtils.debugChat("waitingForPlayers: $waitingForPlayers, warped: $warped, playersJoined: $playersJoined, inParty: ${PartyAPI.inParty}")
                if (warped) return@delay
                McClient.sendCommand("/pc Warping in 3 seconds.")
                GeneralUtils.delay(3000L) {

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

    // TODO: refactor into separate check for mineshaft mayhem or party join
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