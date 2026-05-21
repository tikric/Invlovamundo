package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// --- THEME COLOR SPECIFICATION ---
val CharcoalBg = Color(0xFF121417)
val CardGray = Color(0xFF1A1D24)
val LaserCyan = Color(0xFF00E5FF)
val HotOrange = Color(0xFFFF5722)
val LightMetal = Color(0xFFECEFF1)
val MutedSlate = Color(0xFF78909C)
val ElectricPurple = Color(0xFFD500F9)
val MintEmerald = Color(0xFF00E676)
val InfoBlue = Color(0xFF29B6F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintFlowScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Observe DB States
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val printers by viewModel.printers.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    // Dialog state controllers
    val isClientDialogVisible by viewModel.isClientDialogVisible.collectAsStateWithLifecycle()
    val editingClient by viewModel.editingClient.collectAsStateWithLifecycle()

    val isPrinterDialogVisible by viewModel.isPrinterDialogVisible.collectAsStateWithLifecycle()
    val editingPrinter by viewModel.editingPrinter.collectAsStateWithLifecycle()

    val isOrderDialogVisible by viewModel.isOrderDialogVisible.collectAsStateWithLifecycle()
    val editingOrder by viewModel.editingOrder.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_main_scaffold"),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CharcoalBg,
                    titleContentColor = LightMetal
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Custom 3D printer hotend extruder icon
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Emblema Extrusora",
                            tint = HotOrange,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "PrintFlow 3D",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                color = LightMetal
                            )
                            Text(
                                text = "Controle de Produção & Filamento",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Light,
                                color = MutedSlate
                            )
                        }
                    }
                },
                actions = {
                    // Show printer stats at top
                    val activePrintersCount = printers.count { it.status == "PRINTING" }
                    Surface(
                        color = if (activePrintersCount > 0) LaserCyan.copy(alpha = 0.15f) else CardGray,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (activePrintersCount > 0) LaserCyan else MutedSlate)
                            )
                            Text(
                                text = "$activePrintersCount Ativas",
                                color = if (activePrintersCount > 0) LaserCyan else MutedSlate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CharcoalBg,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Produção", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CharcoalBg,
                        selectedTextColor = LaserCyan,
                        indicatorColor = LaserCyan,
                        unselectedIconColor = MutedSlate,
                        unselectedTextColor = MutedSlate
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Clientes") },
                    label = { Text("Clientes/Imp.", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CharcoalBg,
                        selectedTextColor = LaserCyan,
                        indicatorColor = LaserCyan,
                        unselectedIconColor = MutedSlate,
                        unselectedTextColor = MutedSlate
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Plataformas") },
                    label = { Text("Integrar", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CharcoalBg,
                        selectedTextColor = LaserCyan,
                        indicatorColor = LaserCyan,
                        unselectedIconColor = MutedSlate,
                        unselectedTextColor = MutedSlate
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Custos e Catálogo") },
                    label = { Text("Custos/Catálogo", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CharcoalBg,
                        selectedTextColor = LaserCyan,
                        indicatorColor = LaserCyan,
                        unselectedIconColor = MutedSlate,
                        unselectedTextColor = MutedSlate
                    )
                )
            }
        },
        floatingActionButton = {
            if (currentTab == 0) {
                FloatingActionButton(
                    onClick = { viewModel.openAddOrderDialog() },
                    containerColor = HotOrange,
                    contentColor = Color.White,
                    modifier = Modifier
                        .testTag("add_order_fab")
                        .padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Novo Pedido",
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else if (currentTab == 1) {
                // Dual floating choices for clients or printers handled gracefully inside UI or directly via view actions
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CharcoalBg, Color(0xFF0D0F12))
                    )
                )
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> ProductionDashboardView(viewModel = viewModel, orders = orders, printers = printers)
                1 -> ClientsAndPrintersView(viewModel = viewModel, clients = clients, printers = printers)
                2 -> PlatformsIntegrationView(viewModel = viewModel)
                3 -> FilamentPriceSearchView(viewModel = viewModel)
            }
        }
    }

    // --- DIALOGS REGISTRY ---
    if (isClientDialogVisible) {
        AddEditClientDialog(
            client = editingClient,
            onDismiss = { viewModel.dismissClientDialog() },
            onSave = { name, phone, email, address, note ->
                viewModel.saveClient(name, phone, email, address, note)
            }
        )
    }

    if (isPrinterDialogVisible) {
        AddEditPrinterDialog(
            printer = editingPrinter,
            onDismiss = { viewModel.dismissPrinterDialog() },
            onSave = { name, model, status, ipAddress ->
                viewModel.savePrinter(name, model, status, ipAddress)
            }
        )
    }

    if (isOrderDialogVisible) {
        AddEditOrderDialog(
            order = editingOrder,
            clients = clients,
            printers = printers,
            onDismiss = { viewModel.dismissOrderDialog() },
            onSave = { clientName, itemName, q, type, col, we, ti, pr, st, pri, days ->
                viewModel.saveOrder(clientName, itemName, q, type, col, we, ti, pr, st, pri, days)
            }
        )
    }
}

// ==========================================
// TAB 0: PRODUCTION DASHBOARD VIEW
// ==========================================
@Composable
fun ProductionDashboardView(viewModel: MainViewModel, orders: List<PrintOrder>, printers: List<Printer>) {
    var selectedFilter by remember { mutableStateOf("TODOS") } // "TODOS", "WAITING", "QUEUE", "PRINTING", "POST_PROCESS", "READY"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Stats Overview cards row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stats 1: Total Orders
            StatCard(
                title = "Total Pedidos",
                value = "${orders.size}",
                caption = "${orders.count { it.status == "PRINTING" }} Imprimindo",
                icon = Icons.Default.List,
                accentColor = LaserCyan,
                modifier = Modifier.weight(1f)
            )
            // Stats 2: Total Revenue
            val totalRevenue = orders.sumOf { it.priceCharged }
            val formattedRevenue = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(totalRevenue)
            StatCard(
                title = "Faturamento",
                value = formattedRevenue.replace("R$", "R$"),
                caption = "Pedidos prontos: ${orders.count { it.status == "READY" || it.status == "DELIVERED" }}",
                icon = Icons.Default.Check,
                accentColor = MintEmerald,
                modifier = Modifier.weight(1.3f)
            )
        }

        // Section header
        Text(
            text = "Filtrar Produção",
            color = LightMetal,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Status filters slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf(
                "TODOS" to "Todos",
                "WAITING" to "Aguard. Arq",
                "QUEUE" to "NaFila",
                "PRINTING" to "Imprimindo",
                "POST_PROCESS" to "Pós-Proc",
                "READY" to "Pronto",
                "DELIVERED" to "Entregue"
            )

            filterOptions.forEach { (key, label) ->
                val isSelected = selectedFilter == key
                val color = getStatusColor(key)
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = key },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CardGray,
                        labelColor = MutedSlate,
                        selectedContainerColor = color.copy(alpha = 0.25f),
                        selectedLabelColor = color
                    ),
                    border = BorderStroke(1.dp, if (isSelected) color else Color.Transparent)
                )
            }
        }

        // Filter and display orders list
        val filteredOrders = if (selectedFilter == "TODOS") orders else orders.filter { it.status == selectedFilter }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Vazio",
                        tint = MutedSlate,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = "Sem pedidos nesta categoria.",
                        color = MutedSlate,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Text(
                text = "${filteredOrders.size} Pedido(s) Encontrado(s)",
                color = MutedSlate,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderListItemCard(
                        order = order,
                        onStatusAdvance = { nextStatus ->
                            viewModel.updateOrderStatus(order, nextStatus)
                        },
                        onEdit = {
                            viewModel.openEditOrderDialog(order)
                        },
                        onDelete = {
                            viewModel.deleteOrder(order)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    caption: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardGray),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = MutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Normal)
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = LightMetal,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(caption, color = MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
fun OrderListItemCard(
    order: PrintOrder,
    onStatusAdvance: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = getStatusColor(order.status)
    val statusLabel = getStatusLabel(order.status)

    Card(
        colors = CardDefaults.cardColors(containerColor = CardGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Hot bar indicator matching platform sources
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (order.platformSource != "MANUAL") {
                        val platformColor = when (order.platformSource) {
                            "MERCADO_LIVRE" -> Color(0xFFFFF159)
                            "SHOPEE" -> Color(0xFFEE4D2D)
                            else -> Color(0xFF00ADF0) // Nuvemshop
                        }
                        Surface(
                            color = platformColor,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = order.platformSource.replace("_", " "),
                                color = if (order.platformSource == "MERCADO_LIVRE") Color.Black else Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = order.itemName,
                        color = LightMetal,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 160.dp)
                    )
                }

                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Cliente: ${order.clientName}", color = LightMetal.copy(alpha = 0.85f), fontSize = 12.sp)
                    Text(
                        "Filamento: ${order.filamentType} ${order.filamentColor} • ${order.weightGrams}g",
                        color = MutedSlate,
                        fontSize = 11.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(order.priceCharged)
                    Text(text = formattedPrice, color = LaserCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Tempo: ${order.printTimeHours}h", color = MutedSlate, fontSize = 11.sp)
                }
            }

            // Real-time printing progress animation
            if (order.status == "PRINTING") {
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = LaserCyan,
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            Text(
                                "Imprimindo na: ${order.printerName.ifEmpty { "Impressora" }}",
                                color = LaserCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                        Text(
                            "${(order.printingProgress * 100).toInt()}%",
                            color = LaserCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { order.printingProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = LaserCyan,
                        trackColor = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.DarkGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Action triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bottom-left: actions edit / delete
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Editar",
                        color = MutedSlate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onEdit() }
                    )
                    Text(
                        "Excluir",
                        color = Color.Red.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onDelete() }
                    )
                }

                // Bottom-right: fast state trigger buttons
                val nextActionLabel = getNextStatusActionLabel(order.status)
                val nextStatusValue = getNextStatusValue(order.status)

                if (nextStatusValue != null) {
                    Button(
                        onClick = { onStatusAdvance(nextStatusValue) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = statusColor.copy(alpha = 0.15f),
                            contentColor = statusColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.5f))
                    ) {
                        Text(nextActionLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MintEmerald, modifier = Modifier.size(16.dp))
                        Text("Produção Concluída", color = MintEmerald, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// Helper color translators
fun getStatusColor(status: String): Color {
    return when (status) {
        "WAITING" -> Color(0xFF90A4AE) // Steel Gray
        "QUEUE" -> HotOrange // Fila de Impressão
        "PRINTING" -> LaserCyan // Pulsing Laser Cyan
        "POST_PROCESS" -> ElectricPurple // Pós-Processamento
        "READY" -> MintEmerald // Ready
        "DELIVERED" -> InfoBlue // Delivered
        else -> Color.Gray
    }
}

fun getStatusLabel(status: String): String {
    return when (status) {
        "WAITING" -> "Ag. Arquivo"
        "QUEUE" -> "Fila Impressão"
        "PRINTING" -> "Imprimindo"
        "POST_PROCESS" -> "Pós-Processo"
        "READY" -> "Pronto p/ Entrega"
        "DELIVERED" -> "Entregue"
        else -> status
    }
}

fun getNextStatusActionLabel(status: String): String {
    return when (status) {
        "WAITING" -> "Iniciar Estudo/Fila"
        "QUEUE" -> "Iniciar Impressão"
        "PRINTING" -> "Ir p/ Acabamento"
        "POST_PROCESS" -> "Marcar Pronto"
        "READY" -> "Marcar Entregue"
        else -> ""
    }
}

fun getNextStatusValue(status: String): String? {
    return when (status) {
        "WAITING" -> "QUEUE"
        "QUEUE" -> "PRINTING"
        "PRINTING" -> "POST_PROCESS"
        "POST_PROCESS" -> "READY"
        "READY" -> "DELIVERED"
        else -> null
    }
}


// ==========================================
// TAB 1: CLIENTS & PRINTERS DISPLAY
// ==========================================
@Composable
fun ClientsAndPrintersView(viewModel: MainViewModel, clients: List<Client>, printers: List<Printer>) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Clientes, 1: Impressoras
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Dual option pill toggle selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(CardGray, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            listOf("Cadastros Clientes", "Parque de Impressoras").forEachIndexed { index, title ->
                val isSelected = activeSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) LaserCyan else Color.Transparent)
                        .clickable { activeSubTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) CharcoalBg else MutedSlate,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (activeSubTab == 0) {
            // CLIENTS SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diretório de Clientes (${clients.size})",
                    color = LightMetal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = { viewModel.openAddClientDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = LaserCyan, contentColor = CharcoalBg),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Add Cliente", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (clients.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Nenhum cliente cadastrado.", color = MutedSlate)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(clients, key = { it.id }) { client ->
                        ClientCardItem(
                            client = client,
                            onEdit = { viewModel.openEditClientDialog(client) },
                            onDelete = { viewModel.deleteClient(client) },
                            onContact = {
                                val cleanPhone = client.phone.replace("[^0-9]".toRegex(), "")
                                val whatsappUrl = "https://api.whatsapp.com/send?phone=55$cleanPhone&text=Ol%C3%A1%20${Uri.encode(client.name)}!%20Aqui%20%C3%A9%20da%20Impress%C3%A3o%203D%20para%20atualizar%20sobre%20seu%20projeto."
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        } else {
            // PRINTERS PARQUE SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Máquinas Conectadas (${printers.size})",
                    color = LightMetal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = { viewModel.openAddPrinterDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = LaserCyan, contentColor = CharcoalBg),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Add Máquina", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (printers.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma impressora no parque.", color = MutedSlate)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(printers, key = { it.id }) { printer ->
                        PrinterCardItem(
                            printer = printer,
                            onEdit = { viewModel.openEditPrinterDialog(printer) },
                            onDelete = { viewModel.deletePrinter(printer) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClientCardItem(client: Client, onEdit: () -> Unit, onDelete: () -> Unit, onContact: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(client.name, color = LightMetal, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                // Whatsapp trigger
                IconButton(
                    onClick = onContact,
                    modifier = Modifier
                        .size(34.dp)
                        .background(MintEmerald.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send, // Serves as Whatsapp send emblem
                        contentDescription = "Contactar Whatsapp",
                        tint = MintEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Tel: ${client.phone}", color = LightMetal.copy(alpha = 0.8f), fontSize = 12.sp)
            Text("Email: ${client.email}", color = MutedSlate, fontSize = 11.sp)
            Text("Endereço: ${client.address}", color = MutedSlate, fontSize = 11.sp)

            if (client.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nota: ${client.note}",
                        color = MutedSlate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Editar",
                    color = LaserCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onEdit() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Excluir",
                    color = Color.Red.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onDelete() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PrinterCardItem(printer: Printer, onEdit: () -> Unit, onDelete: () -> Unit) {
    val statusColor = if (printer.status == "PRINTING") LaserCyan else MutedSlate
    val statusText = if (printer.status == "PRINTING") "ATIVO - IMPRIMINDO" else "OCIOSO"

    Card(
        colors = CardDefaults.cardColors(containerColor = CardGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = HotOrange, modifier = Modifier.size(20.dp))
                    Column {
                        Text(printer.name, color = LightMetal, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("Modelo: ${printer.model}", color = MutedSlate, fontSize = 11.sp)
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (printer.ipAddress.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("IP/Painel de Monitoramento: ${printer.ipAddress}", color = MutedSlate, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Editar",
                    color = LaserCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onEdit() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Excluir",
                    color = Color.Red.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onDelete() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}


// ==========================================
// TAB 2: PLATFORMS INTEGRATION VIEW
// ==========================================
@Composable
fun PlatformsIntegrationView(viewModel: MainViewModel) {
    val connections by viewModel.platformConnections.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedSyncPlatform.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncedOrders by viewModel.syncedExternalOrders.collectAsStateWithLifecycle()
    val printers by viewModel.printers.collectAsStateWithLifecycle()

    var showCredentialSetup by remember { mutableStateOf(false) }
    var selectedToImportPrinter by remember { mutableStateOf<ExternalPlatformOrder?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Integração com Plataformas de Vendas",
            color = LightMetal,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Conecte sua conta do Mercado Livre ou Shopee para ver canais de vendas integrados e puxar pedidos diretamente para a sua esteira física.",
            color = MutedSlate,
            fontSize = 11.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Choose Platform Selector Slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            connections.forEach { conn ->
                val isSelected = selectedPlatform == conn.platformName
                val platformAccentColor = when (conn.platformName) {
                    "Mercado Livre" -> Color(0xFFFFF159)
                    "Shopee" -> Color(0xFFEE4D2D)
                    else -> Color(0xFF00ADF0)
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectSyncPlatform(conn.platformName) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) platformAccentColor.copy(alpha = 0.12f) else CardGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 0.5.dp,
                        color = if (isSelected) platformAccentColor else Color.Transparent
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = conn.platformName,
                            fontWeight = FontWeight.Bold,
                            color = LightMetal,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (conn.isConnected) MintEmerald else Color.Red)
                            )
                            Text(
                                text = if (conn.isConnected) "Conectado" else "Desconectado",
                                color = if (conn.isConnected) MintEmerald else Color.Red,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Connection configuration panel
        val activeConn = connections.firstOrNull { it.platformName == selectedPlatform }
        if (activeConn != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardGray),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Status de Integração: ${activeConn.platformName}",
                            color = LightMetal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = activeConn.isConnected,
                            onCheckedChange = { viewModel.togglePlatformConnection(activeConn.platformName) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CharcoalBg,
                                checkedTrackColor = LaserCyan
                            )
                        )
                    }

                    if (activeConn.isConnected) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loja Conectada: ${activeConn.storeName}", color = LightMetal.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("Token API Reduzido: ${activeConn.token}", color = MutedSlate, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Trigger sync scan button
                        Button(
                            onClick = { viewModel.syncPlatformOrders() },
                            colors = ButtonDefaults.buttonColors(containerColor = LaserCyan, contentColor = CharcoalBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CharcoalBg, strokeWidth = 2.dp)
                                    Text("Buscando API...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("Puxar e Sincronizar Pedidos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Por favor, ative a chave de conexão para carregar conexões simuladas da API segura de vendas de ${activeConn.platformName}.",
                            color = MutedSlate,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Sycned external orders list
        if (syncedOrders.isNotEmpty() && activeConn?.isConnected == true) {
            Text(
                "Resultados Encontrados (${syncedOrders.size} vendas)",
                color = LightMetal,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                syncedOrders.forEach { ext ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardGray),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ext.id,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = LaserCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                                Surface(
                                    color = MintEmerald.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        ext.statusText,
                                        color = MintEmerald,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(ext.itemName, color = LightMetal, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Cliente Final: ${ext.clientName}", color = LightMetal.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("Endereço: ${ext.clientAddress}", color = MutedSlate, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val price = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(ext.priceCharged)
                                Text("Valor: $price", color = LightMetal, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                if (ext.isImported) {
                                    Surface(
                                        color = MutedSlate.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MintEmerald, modifier = Modifier.size(12.dp))
                                            Text("Importado", color = MintEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { selectedToImportPrinter = ext },
                                        colors = ButtonDefaults.buttonColors(containerColor = HotOrange, contentColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Importar p/ Fila", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (isSyncing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LaserCyan)
            }
        }
    }

    // Modal dialog to choose target Printer machine when importing platform order
    selectedToImportPrinter?.let { extOrder ->
        Dialog(onDismissRequest = { selectedToImportPrinter = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardGray),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LaserCyan.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = LaserCyan, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Encaminhar para Máscara de Produção",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LightMetal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Deseja alocar '${extOrder.itemName}' diretamente do ${extOrder.platform.replace("_", " ")} à alguma impressora disponível ou apenas guardar na fila?",
                        fontSize = 11.sp,
                        color = MutedSlate,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Choice 1: Save in baseline queue (No printer)
                    Button(
                        onClick = {
                            viewModel.importExternalOrder(extOrder, null)
                            selectedToImportPrinter = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(38.dp)
                    ) {
                        Text("Apenas Deixar na Fila (Sem máquina)", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Machine lists
                    printers.forEach { printer ->
                        val isAvailable = printer.status == "IDLE"
                        Button(
                            onClick = {
                                viewModel.importExternalOrder(extOrder, printer.id)
                                selectedToImportPrinter = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAvailable) LaserCyan else MutedSlate.copy(alpha = 0.3f),
                                contentColor = if (isAvailable) CharcoalBg else LightMetal.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(38.dp)
                        ) {
                            Text("Imprimir na: ${printer.name} (${if (isAvailable) "Livre" else "Ocupada"})", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = { selectedToImportPrinter = null },
                        border = BorderStroke(0.5.dp, MutedSlate),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text("Cancelar", fontSize = 11.sp, color = LightMetal)
                    }
                }
            }
        }
    }
}


// ==========================================
// TAB 3: AI FILAMENT PRICING INDEX SEARCH
// ==========================================
@Composable
fun FilamentPriceSearchView(viewModel: MainViewModel) {
    val subTab by viewModel.pricingSubTab.collectAsStateWithLifecycle()
    
    val catalogItems by viewModel.catalogItems.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    
    // Calculator values
    val cWeight by viewModel.calcWeightGrams.collectAsStateWithLifecycle()
    val cTime by viewModel.calcPrintTimeHours.collectAsStateWithLifecycle()
    val cFilamentPrice by viewModel.calcFilamentPriceRoll.collectAsStateWithLifecycle()
    val cPower by viewModel.calcPrinterPowerW.collectAsStateWithLifecycle()
    val cElectricity by viewModel.calcElectricityCostKwh.collectAsStateWithLifecycle()
    val cLabor by viewModel.calcLaborCostHour.collectAsStateWithLifecycle()
    val cProfit by viewModel.calcProfitMarginPercent.collectAsStateWithLifecycle()
    val cMisc by viewModel.calcMiscCostPercent.collectAsStateWithLifecycle()
    
    // AI offers values
    val materialSelected by viewModel.searchMaterial.collectAsStateWithLifecycle()
    val isSearchingPrices by viewModel.isSearchingPrices.collectAsStateWithLifecycle()
    val offers by viewModel.filamentOffers.collectAsStateWithLifecycle()
    
    val urlHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Custos, Ativos e Catálogos",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = "Controle financeiro, calculadora de custos de impressão e catálogo de bicos/serviços.",
            color = MutedSlate,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Sub-Tab Row Choice
        ScrollableTabRow(
            selectedTabIndex = subTab,
            containerColor = Color.Transparent,
            contentColor = LaserCyan,
            edgePadding = 0.dp,
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            val tabsLabel = listOf("Calculadora 3D", "Catálogo", "Compras e Gastos", "Cotação AI Web")
            tabsLabel.forEachIndexed { idx, title ->
                Tab(
                    selected = subTab == idx,
                    onClick = { viewModel.selectPricingSubTab(idx) },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    selectedContentColor = LaserCyan,
                    unselectedContentColor = MutedSlate
                )
            }
        }

        // Sub-Tab content routing
        when (subTab) {
            0 -> CostCalculatorSubTab(
                viewModel = viewModel,
                weight = cWeight,
                time = cTime,
                filamentPrice = cFilamentPrice,
                power = cPower,
                electricity = cElectricity,
                labor = cLabor,
                profit = cProfit,
                misc = cMisc
            )
            1 -> CatalogSubTab(viewModel = viewModel, items = catalogItems)
            2 -> ExpensesSubTab(viewModel = viewModel, items = expenses)
            3 -> FilamentWebSearchSubTab(
                viewModel = viewModel,
                materialSelected = materialSelected,
                isSearching = isSearchingPrices,
                offers = offers,
                urlHandler = urlHandler
            )
        }
    }
}

@Composable
fun CostCalculatorSubTab(
    viewModel: MainViewModel,
    weight: String,
    time: String,
    filamentPrice: String,
    power: String,
    electricity: String,
    labor: String,
    profit: String,
    misc: String
) {
    val wGrams = weight.toFloatOrNull() ?: 0.0f
    val tHrs = time.toFloatOrNull() ?: 0.0f
    val fPrice = filamentPrice.toDoubleOrNull() ?: 0.0
    val pW = power.toFloatOrNull() ?: 0.0f
    val eCost = electricity.toDoubleOrNull() ?: 0.0
    val hLabor = labor.toDoubleOrNull() ?: 0.0
    val pPercent = profit.toDoubleOrNull() ?: 0.0
    val mPercent = misc.toDoubleOrNull() ?: 0.0

    // Cost formulas
    val filamentCost = (wGrams / 1000.0) * fPrice
    val energyCost = (pW / 1000.0) * tHrs * eCost
    val laborCost = tHrs * hLabor
    val directCost = filamentCost + energyCost + laborCost
    
    // Safety failures and wear overhead
    val miscCost = directCost * (mPercent / 100.0)
    val totalCost = directCost + miscCost
    
    // Desired profitability markup
    val profitEarned = totalCost * (pPercent / 100.0)
    val finalPrice = totalCost + profitEarned

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = CardGray,
            border = BorderStroke(1.dp, LaserCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Preço de Venda Recomendado", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Surface(
                        color = LaserCyan.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Lucro: +${pPercent.toInt()}%",
                            color = LaserCyan,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Text(
                    text = String.format("R$ %.2f", finalPrice),
                    color = LaserCyan,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Divider(color = MutedSlate.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Filamento", color = MutedSlate, fontSize = 10.sp)
                        Text(String.format("R$ %.2f", filamentCost), color = LightMetal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Energia Máq.", color = MutedSlate, fontSize = 10.sp)
                        Text(String.format("R$ %.2f", energyCost), color = LightMetal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Mão de Obra", color = MutedSlate, fontSize = 10.sp)
                        Text(String.format("R$ %.2f", laborCost), color = LightMetal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        String.format("Custos: R$ %.2f (+%d%% desgaste)", totalCost, mPercent.toInt()),
                        color = MutedSlate,
                        fontSize = 11.sp
                    )
                    Text(
                        String.format("Ganho: R$ %.2f", profitEarned),
                        color = MintEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    viewModel.openAddOrderDialog()
                },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HotOrange, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Lançar como Pedido", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    viewModel.saveCatalogItem(
                        name = "Cálculo #${System.currentTimeMillis().toString().takeLast(4)}",
                        description = "Estimativa gerada na calculadora 3D",
                        weightGrams = wGrams,
                        printTimeHours = tHrs,
                        filamentType = "PLA",
                        defaultPrice = finalPrice
                    )
                },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, LaserCyan),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = LaserCyan)
            ) {
                Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Preencher no Catálogo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Text("Ajustar Parâmetros de Entrada", color = LightMetal, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { viewModel.setCalcWeightGrams(it) },
                    label = { Text("Peso da Peça (g)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { viewModel.setCalcPrintTimeHours(it) },
                    label = { Text("Tempo de Impressão (h)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = filamentPrice,
                    onValueChange = { viewModel.setCalcFilamentPriceRoll(it) },
                    label = { Text("Filamento 1kg (R$)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = labor,
                    onValueChange = { viewModel.setCalcLaborCostHour(it) },
                    label = { Text("Trabalho Mão Obra (R$/h)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = power,
                    onValueChange = { viewModel.setCalcPrinterPowerW(it) },
                    label = { Text("Potência Máquina (Watts)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = electricity,
                    onValueChange = { viewModel.setCalcElectricityCostKwh(it) },
                    label = { Text("Preço Luz (R$/KWh)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = profit,
                    onValueChange = { viewModel.setCalcProfitMarginPercent(it) },
                    label = { Text("Margem de Lucro (%)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = misc,
                    onValueChange = { viewModel.setCalcMiscCostPercent(it) },
                    label = { Text("Desperdício / Falhas (%)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CatalogSubTab(viewModel: MainViewModel, items: List<CatalogItem>) {
    var isFormExpanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("150") }
    var time by remember { mutableStateOf("4") }
    var filamentType by remember { mutableStateOf("PLA") }
    var price by remember { mutableStateOf("95") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modelos Pré-cadastrados", color = LightMetal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = { isFormExpanded = !isFormExpanded },
                colors = ButtonDefaults.buttonColors(containerColor = if (isFormExpanded) CardGray else LaserCyan, contentColor = if (isFormExpanded) LightMetal else CharcoalBg),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(if (isFormExpanded) "Fechar Form" else "+ Novo Modelo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (isFormExpanded) {
            Surface(
                color = CardGray,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, HotOrange.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cadastrar modelo no catálogo", color = HotOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome do Produto (Ex: Vaso Geométrico)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descrição breve do produto") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Peso total (g)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Tempo impressão (h)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Preço Venda (R$)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Material", color = MutedSlate, fontSize = 10.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("PLA", "PETG").forEach { type ->
                                    val checked = filamentType == type
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (checked) HotOrange else CharcoalBg)
                                            .clickable { filamentType = type }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(type, color = if (checked) Color.White else MutedSlate, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (name.isNotEmpty()) {
                                viewModel.saveCatalogItem(
                                    name = name,
                                    description = desc,
                                    weightGrams = weight.toFloatOrNull() ?: 150f,
                                    printTimeHours = time.toFloatOrNull() ?: 4f,
                                    filamentType = filamentType,
                                    defaultPrice = price.toDoubleOrNull() ?: 95.0
                                )
                                name = ""
                                desc = ""
                                weight = "150"
                                time = "4"
                                price = "95"
                                isFormExpanded = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HotOrange),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Efetivar Cadastro de Modelo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum item cadastrado no catálogo.", color = MutedSlate, fontSize = 12.sp)
            }
        } else {
            items.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, color = LightMetal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (item.description.isNotEmpty()) {
                                    Text(item.description, color = MutedSlate, fontSize = 11.sp, maxLines = 2)
                                }
                            }
                            Text(
                                text = String.format("R$ %.2f", item.defaultPrice),
                                color = LaserCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Divider(color = MutedSlate.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(color = CharcoalBg, shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        "${item.filamentType} - ${item.weightGrams.toInt()}g",
                                        color = MutedSlate,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text("⏱ ${item.printTimeHours} hrs", color = MutedSlate, fontSize = 11.sp)
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = { viewModel.fillCalculatorFromCatalog(item) },
                                    modifier = Modifier.size(32.dp).background(CharcoalBg, CircleShape)
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = "Calcular Custo", tint = LaserCyan, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.deleteCatalogItem(item) },
                                    modifier = Modifier.size(32.dp).background(CharcoalBg, CircleShape)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpensesSubTab(viewModel: MainViewModel, items: List<Expense>) {
    var isFormExpanded by remember { mutableStateOf(false) }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("FILAMENTO") }
    var amount by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }

    val totalSpent = items.sumOf { it.amount * it.qty }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = CardGray,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(0.5.dp, MintEmerald.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Gasto/Investido em Insumos", color = MutedSlate, fontSize = 11.sp)
                    Text(
                        text = String.format("R$ %.2f", totalSpent),
                        color = MintEmerald,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                
                Button(
                    onClick = { isFormExpanded = !isFormExpanded },
                    colors = ButtonDefaults.buttonColors(containerColor = HotOrange),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(if (isFormExpanded) "Fechar" else "Lançar Despesa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isFormExpanded) {
            Surface(
                color = CardGray,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, LaserCyan.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Entrada de Produto / Registro de Gasto", color = LaserCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Item comprado (Ex: Filamento PLA Azul)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Preço Unit. (R$)") },
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = qty,
                            onValueChange = { qty = it },
                            label = { Text("Quantidade") },
                            modifier = Modifier.weight(0.8f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true
                        )
                    }

                    Text("Categoria do Gasto", color = MutedSlate, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("FILAMENTO", "EQUIPAMENTO", "ENERGIA", "OUTROS").forEach { cat ->
                            val active = category == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (active) LaserCyan else CharcoalBg)
                                    .clickable { category = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    cat, 
                                    color = if (active) CharcoalBg else MutedSlate, 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (desc.isNotEmpty()) {
                                viewModel.saveExpense(
                                    description = desc,
                                    category = category,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    qty = qty.toIntOrNull() ?: 1
                                )
                                desc = ""
                                amount = ""
                                qty = "1"
                                isFormExpanded = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserCyan, contentColor = CharcoalBg),
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Registrar Gasto Financeiro", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Text("Histórico de Compras e Despesas", color = LightMetal, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Sem despesas cadastradas.", color = MutedSlate, fontSize = 12.sp)
            }
        } else {
            items.forEach { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardGray)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = when (expense.category) {
                                    "FILAMENTO" -> HotOrange.copy(alpha = 0.15f)
                                    "EQUIPAMENTO" -> LaserCyan.copy(alpha = 0.15f)
                                    "ENERGIA" -> MintEmerald.copy(alpha = 0.15f)
                                    else -> MutedSlate.copy(alpha = 0.15f)
                                },
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val icon = when (expense.category) {
                                        "FILAMENTO" -> Icons.Default.ShoppingCart
                                        "EQUIPAMENTO" -> Icons.Default.Build
                                        "ENERGIA" -> Icons.Default.Add
                                        else -> Icons.Default.Info
                                    }
                                    Icon(
                                        icon, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(15.dp),
                                        tint = when (expense.category) {
                                            "FILAMENTO" -> HotOrange
                                            "EQUIPAMENTO" -> LaserCyan
                                            "ENERGIA" -> MintEmerald
                                            else -> LightMetal
                                        }
                                    )
                                }
                            }
                            
                            Column {
                                Text(expense.description, color = LightMetal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(expense.category, color = MutedSlate, fontSize = 10.sp)
                                    Text("• Qtd: ${expense.qty}", color = MutedSlate, fontSize = 10.sp)
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = String.format("R$ %.2f", expense.amount * expense.qty),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { viewModel.deleteExpense(expense) },
                                modifier = Modifier.size(28.dp).background(CharcoalBg, CircleShape)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Red, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilamentWebSearchSubTab(
    viewModel: MainViewModel,
    materialSelected: String,
    isSearching: Boolean,
    offers: List<FilamentOffer>,
    urlHandler: androidx.compose.ui.platform.UriHandler
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Pesquisar Filamento Barato (Web)",
            color = LightMetal,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Vincule menores preços cotados e use-os para calibrar os preços calculados na primeira aba.",
            color = MutedSlate,
            fontSize = 11.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filamentMaterials = listOf("PLA", "ABS", "PETG", "TPU")
            filamentMaterials.forEach { mat ->
                val isSelected = materialSelected == mat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) HotOrange else CardGray)
                        .clickable { viewModel.selectSearchMaterial(mat) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        mat,
                        color = if (isSelected) Color.White else MutedSlate,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Surface(
            color = CardGray,
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = LaserCyan, modifier = Modifier.size(18.dp))
                val descriptionText = when (materialSelected) {
                    "PLA" -> "PLA: Fácil de imprimir, biodegradável, ecológico, excelente acabamento estético e sem odor."
                    "ABS" -> "ABS: Alta resistência mecânica e térmica, ideal para peças funcionais. Exige mesa quente fechada."
                    "PETG" -> "PETG: Resistência do ABS aliada à facilidade do PLA. Impermeável e tenaz contra impactos severos."
                    else -> "TPU Flexible: Filamento super maleável, perfeito para juntas, capinhas de celular e vedações."
                }
                Text(descriptionText, fontSize = 11.sp, color = MutedSlate)
            }
        }

        Button(
            onClick = { viewModel.searchFilamentPrices(materialSelected) },
            colors = ButtonDefaults.buttonColors(containerColor = LaserCyan, contentColor = CharcoalBg),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CharcoalBg, strokeWidth = 2.dp)
                    Text("Gerando Novo Índice de Preços (AI)...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Gerar Cotações no Brasil (AI)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Melhores Opções de $materialSelected encontradas (1kg)",
                color = LightMetal,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                color = MintEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Menor Preço BR",
                    color = MintEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(color = LaserCyan)
                    Text("Analisando cotações com o Gemini...", color = MutedSlate, fontSize = 11.sp)
                }
            }
        } else if (offers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Sem cotações ativas. Toque no botão acima.", color = MutedSlate, fontSize = 12.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                offers.forEachIndexed { index, offer ->
                    FilamentOfferCard(
                        rank = index + 1,
                        offer = offer,
                        onClickBuy = {
                            urlHandler.openUri(offer.link)
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MutedSlate, modifier = Modifier.size(12.dp))
                Text(
                    "Valores informativos para calibração de orçamentos aproximados domésticos.",
                    color = MutedSlate,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
fun FilamentOfferCard(rank: Int, offer: FilamentOffer, onClickBuy: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardGray),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (rank == 1) 1.dp else 0.dp,
            color = if (rank == 1) MintEmerald.copy(alpha = 0.5f) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank counter emblem
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> MintEmerald.copy(alpha = 0.2f)
                            2 -> HotOrange.copy(alpha = 0.2f)
                            else -> MutedSlate.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    color = when (rank) {
                        1 -> MintEmerald
                        2 -> HotOrange
                        else -> LightMetal
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = offer.filamentName,
                    color = LightMetal,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Loja: ${offer.storeName}",
                        color = MutedSlate,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "★ ${offer.rating}",
                        color = Color(0xFFFFD54F),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val priceString = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(offer.price)
                Text(
                    text = priceString,
                    color = if (rank == 1) MintEmerald else LaserCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Button(
                    onClick = onClickBuy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rank == 1) MintEmerald else MutedSlate.copy(alpha = 0.2f),
                        contentColor = if (rank == 1) CharcoalBg else LightMetal
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text("Ir à loja", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


// ==========================================
// FORM DIALOG 1: ADD / EDIT CLIENT
// ==========================================
@Composable
fun AddEditClientDialog(client: Client?, onDismiss: () -> Unit, onSave: (String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf(client?.name ?: "") }
    var phone by remember { mutableStateOf(client?.phone ?: "") }
    var email by remember { mutableStateOf(client?.email ?: "") }
    var address by remember { mutableStateOf(client?.address ?: "") }
    var note by remember { mutableStateOf(client?.note ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            border = BorderStroke(0.5.dp, LaserCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (client == null) "Cadastrar Novo Cliente" else "Editar Cliente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LightMetal
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefone / WhatsApp") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Endereço de Entrega") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Observações (Preferências)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = MutedSlate)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && phone.isNotEmpty()) {
                                onSave(name, phone, email, address, note)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserCyan, contentColor = CharcoalBg),
                        shape = RoundedCornerShape(8.dp),
                        enabled = name.isNotBlank() && phone.isNotBlank()
                    ) {
                        Text("Salvar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ==========================================
// FORM DIALOG 2: ADD / EDIT PRINTER
// ==========================================
@Composable
fun AddEditPrinterDialog(printer: Printer?, onDismiss: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf(printer?.name ?: "") }
    var model by remember { mutableStateOf(printer?.model ?: "") }
    var status by remember { mutableStateOf(printer?.status ?: "IDLE") }
    var ipAddress by remember { mutableStateOf(printer?.ipAddress ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            border = BorderStroke(0.5.dp, LaserCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (printer == null) "Cadastrar Nova Impressora" else "Editar Impressora",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LightMetal
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Amigável (ex: Extrusora A)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Fabricante / Modelo (ex: Bambu Lab P1S)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("IP de Rede (ex: 192.168.1.150)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Status drop selection simulator
                Text("Status Inicial", color = LightMetal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statusList = listOf("IDLE" to "Ociosa", "MAINTENANCE" to "Em Manutenção")
                    statusList.forEach { (key, title) ->
                        val isSelected = status == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LaserCyan else Color.Transparent)
                                .border(0.5.dp, MutedSlate, RoundedCornerShape(8.dp))
                                .clickable { status = key }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(title, color = if (isSelected) CharcoalBg else MutedSlate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = MutedSlate)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && model.isNotEmpty()) {
                                onSave(name, model, status, ipAddress)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserCyan, contentColor = CharcoalBg),
                        shape = RoundedCornerShape(8.dp),
                        enabled = name.isNotBlank() && model.isNotBlank()
                    ) {
                        Text("Salvar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ==========================================
// FORM DIALOG 3: ADD / EDIT PRINT ORDER
// ==========================================
@Composable
fun AddEditOrderDialog(
    order: PrintOrder?,
    clients: List<Client>,
    printers: List<Printer>,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, String, String, Float, Float, Double, String, Long?, Int) -> Unit
) {
    var clientName by remember { mutableStateOf(order?.clientName ?: "") }
    var itemName by remember { mutableStateOf(order?.itemName ?: "") }
    var quantity by remember { mutableStateOf(order?.quantity?.toString() ?: "1") }
    var filamentType by remember { mutableStateOf(order?.filamentType ?: "PLA") }
    var filamentColor by remember { mutableStateOf(order?.filamentColor ?: "Preto") }
    var weightGrams by remember { mutableStateOf(order?.weightGrams?.toString() ?: "150") }
    var printTimeHours by remember { mutableStateOf(order?.printTimeHours?.toString() ?: "4") }
    var priceCharged by remember { mutableStateOf(order?.priceCharged?.toString() ?: "80") }
    var status by remember { mutableStateOf(order?.status ?: "WAITING") }
    var assignedPrinterId by remember { mutableStateOf(order?.assignedPrinterId) }
    var deadlineDays by remember { mutableStateOf("3") }

    var expandedClients by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            border = BorderStroke(0.5.dp, LaserCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (order == null) "Cadastrar Novo Trabalho de Impressão" else "Editar Trabalho de Impressão",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LightMetal
                )

                // Simple client chooser dropdown or input
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = {
                            clientName = it
                            expandedClients = clients.any { c -> c.name.contains(it, ignoreCase = true) }
                        },
                        label = { Text("Nome do Cliente") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LightMetal,
                            unfocusedTextColor = LightMetal,
                            focusedBorderColor = LaserCyan,
                            unfocusedBorderColor = MutedSlate
                        ),
                        trailingIcon = {
                            IconButton(onClick = { expandedClients = !expandedClients }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LaserCyan)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Overlay options mapping
                    if (expandedClients && clients.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CardGray,
                            border = BorderStroke(0.5.dp, MutedSlate),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 66.dp)
                                .heightIn(max = 140.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column {
                                clients.forEach { c ->
                                    Text(
                                        text = c.name,
                                        color = LightMetal,
                                        fontSize = 13.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                clientName = c.name
                                                expandedClients = false
                                            }
                                            .padding(10.dp)
                                    )
                                    Divider(color = Color.DarkGray, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Peça / Arquivo STL a Imprimir") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightMetal,
                        unfocusedTextColor = LightMetal,
                        focusedBorderColor = LaserCyan,
                        unfocusedBorderColor = MutedSlate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = filamentType,
                        onValueChange = { filamentType = it },
                        label = { Text("Material") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LaserCyan),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = filamentColor,
                        onValueChange = { filamentColor = it },
                        label = { Text("Cor") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LaserCyan),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightGrams,
                        onValueChange = { weightGrams = it },
                        label = { Text("Peso (g)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LaserCyan),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantidade") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LaserCyan),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = printTimeHours,
                        onValueChange = { printTimeHours = it },
                        label = { Text("Tempo Estimado (h)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LaserCyan),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = priceCharged,
                        onValueChange = { priceCharged = it },
                        label = { Text("Valor Cobrado (R$)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LaserCyan),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = deadlineDays,
                    onValueChange = { deadlineDays = it },
                    label = { Text("Prazo de Entrega (Dias)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LaserCyan),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Assign to Printer Machine directly
                Text("Alocar Máquina Atribuição (Opcional)", color = LightMetal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (assignedPrinterId == null) LaserCyan else Color.Transparent)
                            .border(0.5.dp, MutedSlate, RoundedCornerShape(8.dp))
                            .clickable { assignedPrinterId = null }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Guardar na Fila (Sem máquina)", color = if (assignedPrinterId == null) CharcoalBg else MutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    printers.forEach { p ->
                        val isSelected = assignedPrinterId == p.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LaserCyan else Color.Transparent)
                                .border(0.5.dp, MutedSlate, RoundedCornerShape(8.dp))
                                .clickable { assignedPrinterId = p.id }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(p.name, color = if (isSelected) CharcoalBg else MutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Initial phase selector
                Text("Etapa de Produção Inicial", color = LightMetal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val stepsList = listOf(
                        "WAITING" to "Ag. Arquivo",
                        "QUEUE" to "Na Fila",
                        "PRINTING" to "Imprimindo",
                        "POST_PROCESS" to "Pós-Processo",
                        "READY" to "Pronto",
                        "DELIVERED" to "Entregue"
                    )

                    stepsList.forEach { (key, title) ->
                        val isSelected = status == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LaserCyan else Color.Transparent)
                                .border(0.5.dp, MutedSlate, RoundedCornerShape(8.dp))
                                .clickable { status = key }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(title, color = if (isSelected) CharcoalBg else MutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = MutedSlate)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val qVal = quantity.toIntOrNull() ?: 1
                            val weVal = weightGrams.toFloatOrNull() ?: 150f
                            val tiVal = printTimeHours.toFloatOrNull() ?: 4f
                            val prVal = priceCharged.toDoubleOrNull() ?: 80.0
                            val daysVal = deadlineDays.toIntOrNull() ?: 3

                            if (clientName.isNotEmpty() && itemName.isNotEmpty()) {
                                onSave(clientName, itemName, qVal, filamentType, filamentColor, weVal, tiVal, prVal, status, assignedPrinterId, daysVal)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HotOrange, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        enabled = clientName.isNotBlank() && itemName.isNotBlank()
                    ) {
                        Text("Adicionar Produção", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
