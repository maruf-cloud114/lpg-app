package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.BusinessConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class FirebasePriceService(private val context: Context) {

    private val tag = "FirebasePriceService"

    private val firestore: FirebaseFirestore by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
        } catch (e: Exception) {
            Log.e(tag, "Failed to get named Firestore instance, falling back to default", e)
            FirebaseFirestore.getInstance()
        }
    }

    private var snapshotListener: ListenerRegistration? = null

    /**
     * Listens for real-time changes to the prices and contact numbers document in Firestore.
     * Updates will be pushed instantly across all devices.
     */
    fun startRealtimeSync(onPriceUpdate: (BusinessConfig) -> Unit) {
        stopRealtimeSync()
        try {
            val docRef = firestore.collection("config").document("prices")
            snapshotListener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Snapshot listener error", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val p12 = snapshot.getLong("price12kg")?.toInt() ?: 1450
                    val p35 = snapshot.getLong("price35kg")?.toInt() ?: 4200
                    val p45 = snapshot.getLong("price45kg")?.toInt() ?: 5400
                    val whatsapp = snapshot.getString("whatsappNumber") ?: "8801700000000"
                    val hotline = snapshot.getString("hotlinePhone") ?: "+8801700000000"
                    val lastUpdated = snapshot.getString("lastUpdated") ?: "Live"

                    val updatedConfig = BusinessConfig(
                        price12kg = p12,
                        price35kg = p35,
                        price45kg = p45,
                        whatsappNumber = whatsapp,
                        hotlinePhone = hotline,
                        lastUpdated = lastUpdated
                    )
                    Log.d(tag, "Realtime price update received from Firestore: 12kg=$p12, 35kg=$p35, 45kg=$p45")
                    onPriceUpdate(updatedConfig)
                } else if (snapshot != null && !snapshot.exists()) {
                    // Seed initial config document to Firestore so it exists immediately
                    seedDefaultPrices()
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error starting realtime sync", e)
        }
    }

    fun stopRealtimeSync() {
        snapshotListener?.remove()
        snapshotListener = null
    }

    /**
     * Actively fetches the latest prices from the Firebase server directly.
     * Called whenever customer opens or resumes the app (even from background).
     */
    suspend fun fetchPricesDirectlyFromServer(): BusinessConfig? {
        return try {
            val docRef = firestore.collection("config").document("prices")
            // Attempt to get fresh data from server first, fallback to cache
            val snapshot = try {
                docRef.get(Source.SERVER).await()
            } catch (_: Exception) {
                docRef.get(Source.DEFAULT).await()
            }

            if (snapshot.exists()) {
                val p12 = snapshot.getLong("price12kg")?.toInt() ?: 1450
                val p35 = snapshot.getLong("price35kg")?.toInt() ?: 4200
                val p45 = snapshot.getLong("price45kg")?.toInt() ?: 5400
                val whatsapp = snapshot.getString("whatsappNumber") ?: "8801700000000"
                val hotline = snapshot.getString("hotlinePhone") ?: "+8801700000000"
                val lastUpdated = snapshot.getString("lastUpdated") ?: "Live"

                BusinessConfig(
                    price12kg = p12,
                    price35kg = p35,
                    price45kg = p45,
                    whatsappNumber = whatsapp,
                    hotlinePhone = hotline,
                    lastUpdated = lastUpdated
                )
            } else {
                seedDefaultPrices()
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch prices from Firebase server", e)
            null
        }
    }

    /**
     * Publishes new prices and contact numbers to the Firebase Firestore server.
     * All connected devices will instantly receive this update.
     */
    suspend fun publishPricesToFirebase(config: BusinessConfig): Boolean {
        return try {
            val data = hashMapOf(
                "price12kg" to config.price12kg,
                "price35kg" to config.price35kg,
                "price45kg" to config.price45kg,
                "whatsappNumber" to config.whatsappNumber,
                "hotlinePhone" to config.hotlinePhone,
                "lastUpdated" to config.lastUpdated
            )
            val docRef = firestore.collection("config").document("prices")
            docRef.set(data).await()
            Log.d(tag, "Successfully published prices to Firebase server: $data")
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to publish prices to Firebase server", e)
            false
        }
    }

    private fun seedDefaultPrices() {
        try {
            val data = hashMapOf(
                "price12kg" to 1450,
                "price35kg" to 4200,
                "price45kg" to 5400,
                "whatsappNumber" to "8801700000000",
                "hotlinePhone" to "+8801700000000",
                "lastUpdated" to "Live"
            )
            firestore.collection("config").document("prices").set(data)
        } catch (e: Exception) {
            Log.e(tag, "Error seeding default prices", e)
        }
    }
}
