package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LpgRepository
import com.example.model.BusinessConfig
import com.example.model.CartItem
import com.example.model.CustomerOrder
import com.example.model.CustomerProfile
import com.example.model.CylinderBrand
import com.example.model.LpgProduct
import com.example.util.LanguageHelper
import com.example.util.WhatsAppDispatcher
import com.google.firebase.FirebaseApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("QuickGas LPG", appName)
    }

    @Test
    fun `test bangla digits and price formatting`() {
        assertEquals("১,৪৫০", LanguageHelper.toBanglaDigits("1,450"))
        assertEquals("৳ ১,৪৫০", LanguageHelper.formatTaka(1450, isBangla = true))
        assertEquals("৳ 1,450", LanguageHelper.formatTaka(1450, isBangla = false))
    }

    @Test
    fun `test simplified whatsapp order message generation in bangla and english`() {
        val product = LpgProduct.DEFAULT_PRODUCTS.first { it.weightKg == 12 }
        val item = CartItem(product = product, quantity = 1, exchangeBrand = CylinderBrand.BEXIMCO)
        val order = CustomerOrder(
            orderId = "#LPG-1001",
            items = listOf(item),
            exchangeBrand = CylinderBrand.BEXIMCO,
            customerName = "করিম উল্লাহ",
            customerPhone = "01700000000",
            address = "হাউস ১৪, রোড ৫, মিরপুর",
            isUrgent = false,
            specialNotes = "",
            itemsTotal = 1450,
            deliveryFee = 0,
            grandTotal = 1450,
            timestampFormatted = "Today"
        )

        val messageBn = WhatsAppDispatcher.formatOrderMessage(order, isBangla = true)
        assertTrue(messageBn.contains("এলপিজি রিফিল অর্ডার"))
        assertTrue(messageBn.contains("১২ কেজি এলপিজি"))
        assertTrue(messageBn.contains("করিম উল্লাহ"))
        assertTrue(messageBn.contains("01700000000"))
        assertTrue(messageBn.contains("হাউস ১৪, রোড ৫, মিরপুর"))

        val messageEn = WhatsAppDispatcher.formatOrderMessage(order, isBangla = false)
        assertTrue(messageEn.contains("LPG Refill Order"))
        assertTrue(messageEn.contains("12kg LPG"))
    }

    @Test
    fun `test admin password and repository business config storage`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LpgRepository(context)

        assertEquals("mArUf1357", repo.businessConfig.value.adminPassword)

        val newConfig = BusinessConfig(price12kg = 1480, price35kg = 4250, price45kg = 5500)
        repo.updateBusinessConfig(newConfig)
        assertEquals(1480, repo.businessConfig.value.price12kg)
        assertEquals(4250, repo.businessConfig.value.price35kg)
        assertEquals(5500, repo.businessConfig.value.price45kg)

        val profile = CustomerProfile(
            name = "Rahim",
            phone = "01800000000",
            address = "Dhanmondi 27"
        )
        repo.saveCustomerProfile(profile)
        assertEquals("Rahim", repo.customerProfile.value.name)
    }
}
