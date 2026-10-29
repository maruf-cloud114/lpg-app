package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BusinessConfig
import com.example.ui.theme.FlameOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardDialog(
    config: BusinessConfig,
    isBangla: Boolean,
    isSyncing: Boolean,
    onDismiss: () -> Unit,
    onSaveConfig: (BusinessConfig, (Boolean) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var enteredPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    var price12 by remember { mutableStateOf(config.price12kg.toString()) }
    var price35 by remember { mutableStateOf(config.price35kg.toString()) }
    var price45 by remember { mutableStateOf(config.price45kg.toString()) }
    var whatsapp by remember { mutableStateOf(config.whatsappNumber) }
    var hotline by remember { mutableStateOf(config.hotlinePhone) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("admin_dashboard_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = FlameOrange,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "অ্যাডমিন প্যানেল" else "Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isAuthenticated) {
                    // Password Login Box
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = FlameOrange,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBangla) "অ্যাডমিন পাসওয়ার্ড লিখুন" else "Enter Admin Password",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = enteredPassword,
                                onValueChange = {
                                    enteredPassword = it
                                    passwordError = false
                                },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                isError = passwordError,
                                supportingText = if (passwordError) {
                                    { Text(if (isBangla) "ভুল পাসওয়ার্ড" else "Incorrect password") }
                                } else null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (enteredPassword == "mArUf1357") {
                                        isAuthenticated = true
                                    } else {
                                        passwordError = true
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                modifier = Modifier.fillMaxWidth().testTag("admin_login_button")
                            ) {
                                Text(if (isBangla) "লগইন" else "Login")
                            }
                        }
                    }
                } else {
                    // Authenticated Price Controls
                    Text(
                        text = if (isBangla) "সিলিন্ডার মূল্য নির্ধারণ (৳ BDT)" else "Set Cylinder Prices (৳ BDT)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = price12,
                        onValueChange = { price12 = it },
                        label = { Text("12kg Price (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("admin_price_12")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = price35,
                        onValueChange = { price35 = it },
                        label = { Text("35kg Price (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("admin_price_35")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = price45,
                        onValueChange = { price45 = it },
                        label = { Text("45kg Price (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("admin_price_45")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isBangla) "যোগাযোগ নম্বর" else "Contact Numbers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text(if (isBangla) "অর্ডার হোয়াটসঅ্যাপ নম্বর" else "WhatsApp Order Number") },
                        placeholder = { Text("8801700000000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("admin_whatsapp_number")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = hotline,
                        onValueChange = { hotline = it },
                        label = { Text(if (isBangla) "কল হটলাইন নম্বর" else "Direct Call Hotline") },
                        placeholder = { Text("+8801700000000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("admin_hotline_number")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val newConfig = config.copy(
                                price12kg = price12.toIntOrNull() ?: config.price12kg,
                                price35kg = price35.toIntOrNull() ?: config.price35kg,
                                price45kg = price45.toIntOrNull() ?: config.price45kg,
                                whatsappNumber = whatsapp.ifBlank { config.whatsappNumber },
                                hotlinePhone = hotline.ifBlank { config.hotlinePhone },
                                lastUpdated = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                            )
                            onSaveConfig(newConfig) { serverSuccess ->
                                val msg = if (serverSuccess) {
                                    if (isBangla) "ফায়ারবেস সার্ভারে সফলভাবে সেভ ও সিঙ্ক হয়েছে!" else "Prices & numbers published to Firebase server!"
                                } else {
                                    if (isBangla) "মূল্য লোকালি সংরক্ষিত হয়েছে (সার্ভার অফলাইন)" else "Saved locally (Firebase offline)"
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        },
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_save_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isBangla) "সার্ভারে সিঙ্ক হচ্ছে..." else "Syncing to Firebase...")
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "ফায়ারবেস সার্ভারে সেভ করুন" else "Publish to Firebase Server",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isBangla) "বাতিল" else "Cancel")
                    }
                }
            }
        }
    }
}
