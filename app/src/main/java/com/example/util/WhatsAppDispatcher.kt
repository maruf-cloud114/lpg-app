package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.model.CustomerOrder
import java.net.URLEncoder

object WhatsAppDispatcher {

    fun formatOrderMessage(order: CustomerOrder, isBangla: Boolean): String {
        val productName = if (isBangla) {
            order.items.firstOrNull()?.product?.nameBn ?: "১২ কেজি এলপিজি"
        } else {
            order.items.firstOrNull()?.product?.nameEn ?: "12kg LPG"
        }

        return if (isBangla) {
            """
*এলপিজি রিফিল অর্ডার*
পণ্য: $productName
নাম: ${order.customerName}
ফোন: ${order.customerPhone}
ঠিকানা: ${order.address}
            """.trimIndent()
        } else {
            """
*LPG Refill Order*
Product: $productName
Name: ${order.customerName}
Phone: ${order.customerPhone}
Address: ${order.address}
            """.trimIndent()
        }
    }

    fun dispatchOrderToWhatsApp(
        context: Context,
        rawPhoneNumber: String,
        message: String
    ) {
        var cleanPhone = rawPhoneNumber.replace("+", "").replace(" ", "").replace("-", "")
        if (!cleanPhone.startsWith("880") && cleanPhone.startsWith("0")) {
            cleanPhone = "88" + cleanPhone
        }

        val encodedMsg = try {
            URLEncoder.encode(message, "UTF-8")
        } catch (_: Exception) {
            message
        }

        val urlString = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMsg"
        val webUri = Uri.parse(urlString)

        // Try direct WhatsApp package
        try {
            val waIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                setPackage("com.whatsapp")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(waIntent)
            return
        } catch (_: Exception) {
            // Try WhatsApp Business
        }

        try {
            val wabIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                setPackage("com.whatsapp.w4b")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(wabIntent)
            return
        } catch (_: Exception) {
            // Try generic browser / link handler
        }

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
            return
        } catch (_: Exception) {
            // Fallback to standard share/SMS
        }

        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(shareIntent, "Send Order"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDialer(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open dialer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
