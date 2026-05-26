package name.modid.client.features

import name.modid.client.utils.GeneralUtils
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.BazaarAPI

object GemstoneSell {

    enum class Gemstone(
        val color: String
    ) {
        AMBER(GeneralUtils.GOLD),
        JADE(GeneralUtils.GREEN),
        JASPER(GeneralUtils.LIGHT_PURPLE),
        RUBY(GeneralUtils.RED),
        SAPPHIRE(GeneralUtils.AQUA),
        TOPAZ(GeneralUtils.YELLOW),
        AMETHYST(GeneralUtils.DARK_PURPLE),
        PERIDOT(GeneralUtils.DARK_GREEN),
        ONYX(GeneralUtils.DARK_GRAY),
        AQUAMARINE(GeneralUtils.DARK_BLUE),
        CITRINE(GeneralUtils.DARK_RED),
        OPAL(GeneralUtils.WHITE)
    }

    enum class SellMethod {
        FINE,
        FLAWLESS
    }

    data class GemSellResult(
        val gemstone: Gemstone,
        val fineValue: Double,
        val flawlessValue: Double,
        val difference: Double,
        val bestMethod: SellMethod
    )

    fun calculateGem(gemstone: Gemstone, amount: Int = 80): GemSellResult {

        val finePrice = getPrice("FINE_${gemstone.name}_GEM")
        val flawlessPrice = getPrice("FLAWLESS_${gemstone.name}_GEM")

        val fineValue = finePrice * amount
        val flawlessValue = flawlessPrice * (amount / 80.0)

        val difference = kotlin.math.abs(flawlessValue - fineValue)

        val bestMethod =
            if (flawlessValue > fineValue)
                SellMethod.FLAWLESS
            else
                SellMethod.FINE

        return GemSellResult(
            gemstone = gemstone,
            fineValue = fineValue,
            flawlessValue = flawlessValue,
            difference = difference,
            bestMethod = bestMethod
        )
    }

    fun calculateAll(): List<GemSellResult> {
        return Gemstone.entries.map { calculateGem(it) }
    }

    private fun getPrice(id: String): Double {
        return BazaarAPI.products
            .firstOrNull { it.productId.equals(id, true) }
            ?.sellPrice
            ?: 0.0
    }

    fun fromString(input: String): Gemstone? {
        return Gemstone.entries.firstOrNull {
            it.name.equals(input, ignoreCase = true)
        }
    }

    fun formatCoins(value: Double): String {
        return "%,.0f".format(value)
    }
}