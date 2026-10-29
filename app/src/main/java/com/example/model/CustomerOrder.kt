package com.example.model

data class CartItem(
    val product: LpgProduct,
    val quantity: Int = 1,
    val exchangeBrand: CylinderBrand = CylinderBrand.BEXIMCO
) {
    fun subtotal(config: BusinessConfig): Int {
        return config.getPriceFor(product.type) * quantity
    }
}

data class CustomerOrder(
    val orderId: String,
    val items: List<CartItem>,
    val exchangeBrand: CylinderBrand,
    val customerName: String,
    val customerPhone: String,
    val address: String,
    val isUrgent: Boolean,
    val specialNotes: String,
    val itemsTotal: Int,
    val deliveryFee: Int,
    val grandTotal: Int,
    val timestampFormatted: String,
    val dispatchedToWhatsApp: Boolean = true
)
