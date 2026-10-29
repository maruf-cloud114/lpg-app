package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BusinessConfig
import com.example.model.CustomerOrder
import com.example.model.CustomerProfile
import com.example.model.CylinderBrand
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class LpgRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("quickgas_lpg_prefs", Context.MODE_PRIVATE)

    private val firebasePriceService = FirebasePriceService(context)

    private val _businessConfig = MutableStateFlow(loadBusinessConfig())
    val businessConfig: StateFlow<BusinessConfig> = _businessConfig.asStateFlow()

    private val _customerProfile = MutableStateFlow(loadCustomerProfile())
    val customerProfile: StateFlow<CustomerProfile> = _customerProfile.asStateFlow()

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _orderHistory = MutableStateFlow(loadOrderHistory())
    val orderHistory: StateFlow<List<CustomerOrder>> = _orderHistory.asStateFlow()

    init {
        // Start continuous real-time listener for Firestore updates
        firebasePriceService.startRealtimeSync { onlineConfig ->
            updateBusinessConfig(onlineConfig)
        }
    }

    fun updateLanguage(newLang: AppLanguage) {
        _language.value = newLang
        prefs.edit().putString(KEY_LANG, newLang.name).apply()
    }

    /**
     * Actively fetches the latest prices and phone numbers from the Firebase server.
     * Called whenever customer opens the app or returns from the background.
     */
    suspend fun syncWithFirebaseServer(): Boolean {
        val onlineConfig = firebasePriceService.fetchPricesDirectlyFromServer()
        if (onlineConfig != null) {
            updateBusinessConfig(onlineConfig)
            return true
        }
        return false
    }

    /**
     * Publishes new prices and contact numbers to the Firebase Firestore server.
     * Reflects instantly across all customer devices.
     */
    suspend fun publishAndSaveBusinessConfig(config: BusinessConfig): Boolean {
        updateBusinessConfig(config)
        return firebasePriceService.publishPricesToFirebase(config)
    }

    fun updateBusinessConfig(config: BusinessConfig) {
        _businessConfig.value = config
        prefs.edit()
            .putInt(KEY_PRICE_12, config.price12kg)
            .putInt(KEY_PRICE_35, config.price35kg)
            .putInt(KEY_PRICE_45, config.price45kg)
            .putString(KEY_WHATSAPP, config.whatsappNumber)
            .putString(KEY_HOTLINE, config.hotlinePhone)
            .putString(KEY_ADMIN_PASSWORD, config.adminPassword)
            .putString(KEY_LAST_UPDATED, config.lastUpdated)
            .apply()
    }

    fun saveCustomerProfile(profile: CustomerProfile) {
        _customerProfile.value = profile
        prefs.edit()
            .putString(KEY_CUST_NAME, profile.name)
            .putString(KEY_CUST_PHONE, profile.phone)
            .putString(KEY_CUST_ADDR, profile.address)
            .apply()
    }

    fun addOrder(order: CustomerOrder) {
        val currentList = _orderHistory.value.toMutableList()
        currentList.add(0, order)
        if (currentList.size > 10) {
            currentList.removeAt(currentList.lastIndex)
        }
        _orderHistory.value = currentList
        saveOrderHistory(currentList)
    }

    private fun loadBusinessConfig(): BusinessConfig {
        return BusinessConfig(
            price12kg = prefs.getInt(KEY_PRICE_12, 1450),
            price35kg = prefs.getInt(KEY_PRICE_35, 4200),
            price45kg = prefs.getInt(KEY_PRICE_45, 5400),
            whatsappNumber = prefs.getString(KEY_WHATSAPP, "8801700000000") ?: "8801700000000",
            hotlinePhone = prefs.getString(KEY_HOTLINE, "+8801700000000") ?: "+8801700000000",
            adminPassword = prefs.getString(KEY_ADMIN_PASSWORD, "mArUf1357") ?: "mArUf1357",
            lastUpdated = prefs.getString(KEY_LAST_UPDATED, "Live") ?: "Live"
        )
    }

    private fun loadCustomerProfile(): CustomerProfile {
        return CustomerProfile(
            name = prefs.getString(KEY_CUST_NAME, "") ?: "",
            phone = prefs.getString(KEY_CUST_PHONE, "") ?: "",
            address = prefs.getString(KEY_CUST_ADDR, "") ?: "",
            areaOrLandmark = "",
            preferredBrand = CylinderBrand.BEXIMCO,
            emptyReady = true
        )
    }

    private fun loadLanguage(): AppLanguage {
        val langStr = prefs.getString(KEY_LANG, AppLanguage.BANGLA.name)
        return try {
            AppLanguage.valueOf(langStr ?: AppLanguage.BANGLA.name)
        } catch (_: Exception) {
            AppLanguage.BANGLA
        }
    }

    private fun saveOrderHistory(orders: List<CustomerOrder>) {
        val array = JSONArray()
        orders.take(10).forEach { order ->
            val obj = JSONObject()
            obj.put("id", order.orderId)
            obj.put("name", order.customerName)
            obj.put("phone", order.customerPhone)
            obj.put("addr", order.address)
            obj.put("time", order.timestampFormatted)
            array.put(obj)
        }
        prefs.edit().putString(KEY_ORDERS_JSON, array.toString()).apply()
    }

    private fun loadOrderHistory(): List<CustomerOrder> {
        val jsonStr = prefs.getString(KEY_ORDERS_JSON, null) ?: return emptyList()
        val list = mutableListOf<CustomerOrder>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CustomerOrder(
                        orderId = obj.optString("id", "#LPG-001"),
                        items = emptyList(),
                        exchangeBrand = CylinderBrand.BEXIMCO,
                        customerName = obj.optString("name", ""),
                        customerPhone = obj.optString("phone", ""),
                        address = obj.optString("addr", ""),
                        isUrgent = false,
                        specialNotes = "",
                        itemsTotal = 0,
                        deliveryFee = 0,
                        grandTotal = 0,
                        timestampFormatted = obj.optString("time", "Recent"),
                        dispatchedToWhatsApp = true
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore parse errors on corrupt cache
        }
        return list
    }

    companion object {
        private const val KEY_LANG = "pref_lang"
        private const val KEY_PRICE_12 = "pref_price_12"
        private const val KEY_PRICE_35 = "pref_price_35"
        private const val KEY_PRICE_45 = "pref_price_45"
        private const val KEY_WHATSAPP = "pref_whatsapp"
        private const val KEY_HOTLINE = "pref_hotline"
        private const val KEY_ADMIN_PASSWORD = "pref_admin_password"
        private const val KEY_LAST_UPDATED = "pref_last_updated"

        private const val KEY_CUST_NAME = "pref_cust_name"
        private const val KEY_CUST_PHONE = "pref_cust_phone"
        private const val KEY_CUST_ADDR = "pref_cust_addr"
        private const val KEY_ORDERS_JSON = "pref_orders_json"
    }
}
