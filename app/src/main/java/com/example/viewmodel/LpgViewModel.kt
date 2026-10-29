package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.LpgRepository
import com.example.model.BusinessConfig
import com.example.model.CartItem
import com.example.model.CustomerOrder
import com.example.model.CustomerProfile
import com.example.model.CylinderBrand
import com.example.model.LpgProduct
import com.example.util.AppLanguage
import com.example.util.WhatsAppDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LpgViewModel(private val repository: LpgRepository) : ViewModel() {

    val language: StateFlow<AppLanguage> = repository.language
    val businessConfig: StateFlow<BusinessConfig> = repository.businessConfig
    val customerProfile: StateFlow<CustomerProfile> = repository.customerProfile

    private val _quickOrderProduct = MutableStateFlow<LpgProduct?>(null)
    val quickOrderProduct: StateFlow<LpgProduct?> = _quickOrderProduct.asStateFlow()

    private val _isAdminOpen = MutableStateFlow(false)
    val isAdminOpen: StateFlow<Boolean> = _isAdminOpen.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        // Fetch latest prices directly from Firebase server immediately on start
        syncPricesFromFirebase()
    }

    /**
     * Actively queries the Firebase Firestore server for live prices and phone numbers.
     * Can be triggered on startup, manual refresh, or when resuming from background.
     */
    fun syncPricesFromFirebase() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncWithFirebaseServer()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun toggleLanguage() {
        val newLang = if (language.value == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
        repository.updateLanguage(newLang)
    }

    fun openQuickOrder(product: LpgProduct) {
        _quickOrderProduct.value = product
    }

    fun dismissQuickOrder() {
        _quickOrderProduct.value = null
    }

    fun openAdmin() {
        _isAdminOpen.value = true
    }

    fun closeAdmin() {
        _isAdminOpen.value = false
    }

    fun saveBusinessConfig(config: BusinessConfig, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            val success = repository.publishAndSaveBusinessConfig(config)
            _isSyncing.value = false
            onComplete(success)
        }
    }

    fun placeOrder(
        context: Context,
        product: LpgProduct,
        name: String,
        phone: String,
        address: String
    ) {
        val config = businessConfig.value
        val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
        val orderId = "#LPG-${(1000..9999).random()}"

        val item = CartItem(product = product, quantity = 1, exchangeBrand = CylinderBrand.BEXIMCO)
        val order = CustomerOrder(
            orderId = orderId,
            items = listOf(item),
            exchangeBrand = CylinderBrand.BEXIMCO,
            customerName = name,
            customerPhone = phone,
            address = address,
            isUrgent = false,
            specialNotes = "",
            itemsTotal = config.getPriceFor(product.type),
            deliveryFee = 0,
            grandTotal = config.getPriceFor(product.type),
            timestampFormatted = timeStr,
            dispatchedToWhatsApp = true
        )

        // Save profile locally so future refills take literally 1 tap
        repository.saveCustomerProfile(
            CustomerProfile(name = name, phone = phone, address = address)
        )
        repository.addOrder(order)

        // Dispatch streamlined message to WhatsApp
        val isBangla = (language.value == AppLanguage.BANGLA)
        val simpleMsg = WhatsAppDispatcher.formatOrderMessage(order, isBangla)
        WhatsAppDispatcher.dispatchOrderToWhatsApp(context, config.whatsappNumber, simpleMsg)

        _quickOrderProduct.value = null
    }

    class Factory(private val repository: LpgRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LpgViewModel::class.java)) {
                return LpgViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
