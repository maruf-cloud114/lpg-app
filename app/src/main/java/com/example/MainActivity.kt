package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.data.LpgRepository
import com.example.model.LpgProduct
import com.example.ui.components.AdminDashboardDialog
import com.example.ui.components.ContactCard
import com.example.ui.components.CylinderCard
import com.example.ui.components.QuickOrderModal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import com.example.util.WhatsAppDispatcher
import com.example.viewmodel.LpgViewModel

class MainActivity : ComponentActivity() {

    private val repository by lazy { LpgRepository(applicationContext) }
    private val viewModel: LpgViewModel by viewModels { LpgViewModel.Factory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                QuickGasApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Always sync latest prices and phone numbers from Firebase when app returns from background
        viewModel.syncPricesFromFirebase()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickGasApp(viewModel: LpgViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Synchronize directly with Firebase server whenever customer opens or switches back to app
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_START) {
                viewModel.syncPricesFromFirebase()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val language by viewModel.language.collectAsState()
    val isBangla = (language == AppLanguage.BANGLA)

    val businessConfig by viewModel.businessConfig.collectAsState()
    val customerProfile by viewModel.customerProfile.collectAsState()
    val quickOrderProduct by viewModel.quickOrderProduct.collectAsState()
    val isAdminOpen by viewModel.isAdminOpen.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(FlameOrange)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBangla) "কুইকগ্যাস এলপিজি" else "QuickGas LPG",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Manual sync status or trigger
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = FlameOrange,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 6.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = { viewModel.syncPricesFromFirebase() },
                            modifier = Modifier.size(36.dp).padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Prices",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Language Switch Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(end = 4.dp)
                            .testTag("language_toggle_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Toggle Language",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "ENG" else "বাংলা",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Admin Dashboard Button (secured with mArUf1357)
                    IconButton(
                        onClick = { viewModel.openAdmin() },
                        modifier = Modifier.testTag("admin_portal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Portal",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Grid of 3 Cylinders: 12kg, 35kg, 45kg
            val products = LpgProduct.DEFAULT_PRODUCTS
            items(products.size) { index ->
                val product = products[index]
                val currentPrice = businessConfig.getPriceFor(product.type)

                CylinderCard(
                    product = product,
                    currentPrice = currentPrice,
                    isBangla = isBangla,
                    onPlaceOrderClick = { viewModel.openQuickOrder(product) }
                )
            }

            // Call for Package & Business Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                ContactCard(
                    config = businessConfig,
                    isBangla = isBangla,
                    onCallPackageClick = {
                        WhatsAppDispatcher.openDialer(context, businessConfig.hotlinePhone)
                    },
                    onCallBusinessClick = {
                        WhatsAppDispatcher.openDialer(context, businessConfig.hotlinePhone)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Lightweight Quick Order Modal
    quickOrderProduct?.let { product ->
        QuickOrderModal(
            product = product,
            config = businessConfig,
            savedProfile = customerProfile,
            isBangla = isBangla,
            onDismiss = { viewModel.dismissQuickOrder() },
            onConfirmOrder = { p, name, phone, address ->
                viewModel.placeOrder(context, p, name, phone, address)
            }
        )
    }

    // Business Admin Dashboard
    if (isAdminOpen) {
        AdminDashboardDialog(
            config = businessConfig,
            isBangla = isBangla,
            isSyncing = isSyncing,
            onDismiss = { viewModel.closeAdmin() },
            onSaveConfig = { newConfig, onComplete ->
                viewModel.saveBusinessConfig(newConfig, onComplete)
            }
        )
    }
}
