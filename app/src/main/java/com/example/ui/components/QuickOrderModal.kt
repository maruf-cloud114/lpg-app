package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BusinessConfig
import com.example.model.CustomerProfile
import com.example.model.LpgProduct
import com.example.ui.theme.WhatsAppGreen
import com.example.util.LanguageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickOrderModal(
    product: LpgProduct,
    config: BusinessConfig,
    savedProfile: CustomerProfile,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirmOrder: (product: LpgProduct, name: String, phone: String, address: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var customerName by remember { mutableStateOf(savedProfile.name) }
    var customerPhone by remember { mutableStateOf(savedProfile.phone) }
    var deliveryAddress by remember { mutableStateOf(savedProfile.address) }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    val currentPrice = config.getPriceFor(product.type)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("quick_order_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = if (isBangla) product.nameBn else product.nameEn,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LanguageHelper.formatTaka(currentPrice, isBangla),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            OutlinedTextField(
                value = customerName,
                onValueChange = {
                    customerName = it
                    nameError = false
                },
                label = { Text(if (isBangla) "আপনার নাম *" else "Your Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = nameError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_customer_name")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Phone Field
            OutlinedTextField(
                value = customerPhone,
                onValueChange = {
                    customerPhone = it
                    phoneError = false
                },
                label = { Text(if (isBangla) "মোবাইল নম্বর *" else "Phone Number *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_customer_phone")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Address Field
            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = {
                    deliveryAddress = it
                    addressError = false
                },
                label = { Text(if (isBangla) "ডেলিভারি ঠিকানা *" else "Delivery Address *") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                isError = addressError,
                minLines = 2,
                modifier = Modifier.fillMaxWidth().testTag("input_delivery_address")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Place Order Button
            Button(
                onClick = {
                    nameError = customerName.isBlank()
                    phoneError = customerPhone.isBlank() || customerPhone.length < 6
                    addressError = deliveryAddress.isBlank()

                    if (!nameError && !phoneError && !addressError) {
                        onConfirmOrder(product, customerName.trim(), customerPhone.trim(), deliveryAddress.trim())
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("place_order_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "অর্ডার করুন" else "Place Order",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        }
    }
}
