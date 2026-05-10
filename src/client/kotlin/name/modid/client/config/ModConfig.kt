package name.modid.client.config

data class ModConfig(
    var debugChatLogging: Boolean = false,

    var announceShaftSpawn: Boolean = true,
    var transferOnShaftSpawn: Boolean = true,
    var titleOnShaftSpawn: Boolean = false,

    var announceOnShaftEnter: Boolean = true,
    var onlyVanguardLittle: Boolean = false,

    var announceCorpses: Boolean = true,
    var corpseAutoWarp: Boolean = false,
    var playersToWarp: MutableList<String> = mutableListOf(),
    var minLapis: Int = 0,
    var minUmber: Int = 0,
    var minTungsten: Int = 0
)
