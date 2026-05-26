package name.modid.client.features

import name.modid.client.utils.GeneralUtils
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.BazaarAPI

object GemstoneSell {

    enum class Gemstone(val color: String) {
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

        val fineInstaSell: Double,
        val fineSellOffer: Double,

        val flawlessInstaSell: Double,
        val flawlessSellOffer: Double,

        val instaDifference: Double,
        val instaBest: SellMethod,

        val offerDifference: Double,
        val offerBest: SellMethod
    )

    fun calculateGem(gemstone: Gemstone, amount: Int = 80): GemSellResult {

        val fine = BazaarAPI.getProduct("FINE_${gemstone.name}_GEM")
        val flawless = BazaarAPI.getProduct("FLAWLESS_${gemstone.name}_GEM")

        val fineInsta = fine?.sellPrice ?: 0.0
        val fineOffer = fine?.buyPrice ?: 0.0

        val flawlessInsta = flawless?.sellPrice ?: 0.0
        val flawlessOffer = flawless?.buyPrice ?: 0.0

        // --- INSTA CALC ---
        val fineInstaValue = fineInsta * amount
        val flawlessInstaValue = flawlessInsta * (amount / 80.0)

        val instaDifference = kotlin.math.abs(fineInstaValue - flawlessInstaValue)

        val instaBest =
            if (flawlessInstaValue > fineInstaValue)
                SellMethod.FLAWLESS
            else
                SellMethod.FINE

        // --- OFFER CALC ---
        val fineOfferValue = fineOffer * amount
        val flawlessOfferValue = flawlessOffer * (amount / 80.0)

        val offerDifference = kotlin.math.abs(fineOfferValue - flawlessOfferValue)

        val offerBest =
            if (flawlessOfferValue > fineOfferValue)
                SellMethod.FLAWLESS
            else
                SellMethod.FINE

        return GemSellResult(
            gemstone = gemstone,

            fineInstaSell = fineInsta,
            fineSellOffer = fineOffer,

            flawlessInstaSell = flawlessInsta,
            flawlessSellOffer = flawlessOffer,

            instaDifference = instaDifference,
            instaBest = instaBest,

            offerDifference = offerDifference,
            offerBest = offerBest
        )
    }

    fun calculateAll(): List<GemSellResult> {
        return Gemstone.entries.map { calculateGem(it) }
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