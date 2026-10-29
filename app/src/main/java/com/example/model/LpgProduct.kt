package com.example.model

enum class LpgCylinderType(val weightKg: Int) {
    KG_12(12),
    KG_35(35),
    KG_45(45)
}

data class LpgProduct(
    val id: String,
    val type: LpgCylinderType,
    val weightKg: Int,
    val nameEn: String,
    val nameBn: String,
    val defaultPrice: Int
) {
    companion object {
        val DEFAULT_PRODUCTS = listOf(
            LpgProduct(
                id = "refill_12kg",
                type = LpgCylinderType.KG_12,
                weightKg = 12,
                nameEn = "12kg LPG",
                nameBn = "১২ কেজি এলপিজি",
                defaultPrice = 1450
            ),
            LpgProduct(
                id = "refill_35kg",
                type = LpgCylinderType.KG_35,
                weightKg = 35,
                nameEn = "35kg LPG",
                nameBn = "৩৫ কেজি এলপিজি",
                defaultPrice = 4200
            ),
            LpgProduct(
                id = "refill_45kg",
                type = LpgCylinderType.KG_45,
                weightKg = 45,
                nameEn = "45kg LPG",
                nameBn = "৪৫ কেজি এলপিজি",
                defaultPrice = 5400
            )
        )
    }
}
