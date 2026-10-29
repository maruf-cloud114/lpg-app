package com.example.model

enum class CylinderBrand(
    val nameEn: String,
    val nameBn: String,
    val brandColorHex: Long = 0xFFE65100
) {
    BEXIMCO("Beximco", "বেক্সিমকো", 0xFFD32F2F),
    BASHUNDHARA("Bashundhara", "বসুন্ধরা", 0xFF1976D2),
    OMERA("Omera", "ওমেরা", 0xFF388E3C),
    JAMUNA("Jamuna", "যমুনা", 0xFF7B1FA2),
    DELTA("Delta", "ডেল্টা", 0xFFE64A19),
    AYGAS("Aygas", "আইগ্যাস", 0xFF0288D1),
    SENA("Sena", "সেনা", 0xFF2E7D32),
    BM("BM Energy", "বিএম", 0xFFF57C00),
    TOTAL("TotalEnergies", "টোটাল", 0xFFC2185B),
    OTHER("Other Brand", "অন্যান্য", 0xFF546E7A);

    fun getDisplayName(isBangla: Boolean): String {
        return if (isBangla) nameBn else nameEn
    }

    fun getFullLabel(isBangla: Boolean): String {
        return if (isBangla) "$nameBn ($nameEn)" else "$nameEn ($nameBn)"
    }
}
