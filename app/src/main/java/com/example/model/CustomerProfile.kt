package com.example.model

data class CustomerProfile(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val areaOrLandmark: String = "",
    val preferredBrand: CylinderBrand = CylinderBrand.BEXIMCO,
    val emptyReady: Boolean = true
) {
    val isComplete: Boolean
        get() = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
}
