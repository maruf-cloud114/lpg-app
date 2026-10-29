package com.example.model

data class BusinessConfig(
    val price12kg: Int = 1450,
    val price35kg: Int = 4200,
    val price45kg: Int = 5400,
    val whatsappNumber: String = "8801700000000",
    val hotlinePhone: String = "+8801700000000",
    val adminPassword: String = "mArUf1357",
    val lastUpdated: String = "Live"
) {
    fun getPriceFor(type: LpgCylinderType): Int {
        return when (type) {
            LpgCylinderType.KG_12 -> price12kg
            LpgCylinderType.KG_35 -> price35kg
            LpgCylinderType.KG_45 -> price45kg
        }
    }
}
