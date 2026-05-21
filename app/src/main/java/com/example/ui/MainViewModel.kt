package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = PrintRepository(db)

    // Room reactive flows
    val clients: StateFlow<List<Client>> = repository.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val printers: StateFlow<List<Printer>> = repository.allPrinters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<PrintOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val catalogItems: StateFlow<List<CatalogItem>> = repository.allCatalogItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Tab state
    private val _currentTab = MutableStateFlow(0) // 0: Dashboard, 1: Clientes/Imp, 2: Plataformas, 3: Preços/Custos
    val currentTab = _currentTab.asStateFlow()

    // Dialog & Form states
    private val _isClientDialogVisible = MutableStateFlow(false)
    val isClientDialogVisible = _isClientDialogVisible.asStateFlow()
    private val _editingClient = MutableStateFlow<Client?>(null)
    val editingClient = _editingClient.asStateFlow()

    private val _isPrinterDialogVisible = MutableStateFlow(false)
    val isPrinterDialogVisible = _isPrinterDialogVisible.asStateFlow()
    private val _editingPrinter = MutableStateFlow<Printer?>(null)
    val editingPrinter = _editingPrinter.asStateFlow()

    private val _isOrderDialogVisible = MutableStateFlow(false)
    val isOrderDialogVisible = _isOrderDialogVisible.asStateFlow()
    private val _editingOrder = MutableStateFlow<PrintOrder?>(null)
    val editingOrder = _editingOrder.asStateFlow()

    // Platform syncing states
    private val _platformConnections = MutableStateFlow(PlatformSyncSimulator.defaultConnections)
    val platformConnections = _platformConnections.asStateFlow()

    private val _selectedSyncPlatform = MutableStateFlow("Mercado Livre")
    val selectedSyncPlatform = _selectedSyncPlatform.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _syncedExternalOrders = MutableStateFlow<List<ExternalPlatformOrder>>(emptyList())
    val syncedExternalOrders = _syncedExternalOrders.asStateFlow()

    // Filament search states
    private val _searchMaterial = MutableStateFlow("PLA")
    val searchMaterial = _searchMaterial.asStateFlow()

    private val _isSearchingPrices = MutableStateFlow(false)
    val isSearchingPrices = _isSearchingPrices.asStateFlow()

    private val _filamentOffers = MutableStateFlow<List<FilamentOffer>>(emptyList())
    val filamentOffers = _filamentOffers.asStateFlow()

    init {
        // Run filament price search initially for PLA so the user sees data immediately on opening the price tab
        searchFilamentPrices("PLA")

        // Start live printing simulation progression
        startPrintingSimulation()
    }

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    // --- Client Operations ---
    fun openAddClientDialog() {
        _editingClient.value = null
        _isClientDialogVisible.value = true
    }

    fun openEditClientDialog(client: Client) {
        _editingClient.value = client
        _isClientDialogVisible.value = true
    }

    fun dismissClientDialog() {
        _isClientDialogVisible.value = false
        _editingClient.value = null
    }

    fun saveClient(name: String, phone: String, email: String, address: String, note: String) {
        viewModelScope.launch {
            val client = _editingClient.value?.copy(
                name = name,
                phone = phone,
                email = email,
                address = address,
                note = note
            ) ?: Client(name = name, phone = phone, email = email, address = address, note = note)

            if (client.id == 0L) {
                repository.insertClient(client)
            } else {
                repository.updateClient(client)
            }
            dismissClientDialog()
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    // --- Printer Operations ---
    fun openAddPrinterDialog() {
        _editingPrinter.value = null
        _isPrinterDialogVisible.value = true
    }

    fun openEditPrinterDialog(printer: Printer) {
        _editingPrinter.value = printer
        _isPrinterDialogVisible.value = true
    }

    fun dismissPrinterDialog() {
        _isPrinterDialogVisible.value = false
        _editingPrinter.value = null
    }

    fun savePrinter(name: String, model: String, status: String, ipAddress: String) {
        viewModelScope.launch {
            val printer = _editingPrinter.value?.copy(
                name = name,
                model = model,
                status = status,
                ipAddress = ipAddress
            ) ?: Printer(name = name, model = model, status = status, ipAddress = ipAddress)

            if (printer.id == 0L) {
                repository.insertPrinter(printer)
            } else {
                repository.updatePrinter(printer)
            }
            dismissPrinterDialog()
        }
    }

    fun deletePrinter(printer: Printer) {
        viewModelScope.launch {
            repository.deletePrinter(printer)
        }
    }

    // --- Order Operations ---
    fun openAddOrderDialog() {
        _editingOrder.value = null
        _isOrderDialogVisible.value = true
    }

    fun openEditOrderDialog(order: PrintOrder) {
        _editingOrder.value = order
        _isOrderDialogVisible.value = true
    }

    fun dismissOrderDialog() {
        _isOrderDialogVisible.value = false
        _editingOrder.value = null
    }

    fun saveOrder(
        clientName: String,
        itemName: String,
        quantity: Int,
        filamentType: String,
        filamentColor: String,
        weightGrams: Float,
        printTimeHours: Float,
        priceCharged: Double,
        status: String,
        assignedPrinterId: Long?,
        deadlineDays: Int
    ) {
        viewModelScope.launch {
            val printerObj = assignedPrinterId?.let { repository.getPrinterById(it) }
            val matchingClient = clients.value.firstOrNull { it.name.trim().lowercase() == clientName.trim().lowercase() }
            val clientId = matchingClient?.id

            val order = _editingOrder.value?.copy(
                clientId = clientId,
                clientName = clientName,
                itemName = itemName,
                quantity = quantity,
                filamentType = filamentType,
                filamentColor = filamentColor,
                weightGrams = weightGrams,
                printTimeHours = printTimeHours,
                priceCharged = priceCharged,
                status = status,
                assignedPrinterId = assignedPrinterId,
                printerName = printerObj?.name ?: "",
                deadline = System.currentTimeMillis() + (deadlineDays * 24L * 3600L * 1000L)
            ) ?: PrintOrder(
                clientId = clientId,
                clientName = clientName,
                itemName = itemName,
                quantity = quantity,
                filamentType = filamentType,
                filamentColor = filamentColor,
                weightGrams = weightGrams,
                printTimeHours = printTimeHours,
                priceCharged = priceCharged,
                status = status,
                assignedPrinterId = assignedPrinterId,
                printerName = printerObj?.name ?: "",
                deadline = System.currentTimeMillis() + (deadlineDays * 24L * 3600L * 1000L)
            )

            if (order.id == 0L) {
                repository.insertOrder(order)
            } else {
                repository.updateOrder(order)
            }

            // Sync printer activity status based on assignment
            updateMachineStatuses()
            dismissOrderDialog()
        }
    }

    // Fast status update (Kanban slide action or single-tap progress edit)
    fun updateOrderStatus(order: PrintOrder, newStatus: String) {
        viewModelScope.launch {
            var updatedProg = order.printingProgress
            if (newStatus == "READY" || newStatus == "DELIVERED") {
                updatedProg = 1.0f
            } else if (newStatus == "WAITING" || newStatus == "QUEUE") {
                updatedProg = 0.0f
            }

            val updated = order.copy(
                status = newStatus,
                printingProgress = updatedProg
            )
            repository.updateOrder(updated)
            updateMachineStatuses()
        }
    }

    fun deleteOrder(order: PrintOrder) {
        viewModelScope.launch {
            repository.deleteOrder(order)
            updateMachineStatuses()
        }
    }

    // Helper to align printers with actual assignments in database
    private suspend fun updateMachineStatuses() {
        val currentOrders = db.printOrderDao().getAllOrders().stateIn(viewModelScope).value
        val currentPrinters = db.printerDao().getAllPrinters().stateIn(viewModelScope).value

        for (printer in currentPrinters) {
            val printingJobs = currentOrders.filter { it.assignedPrinterId == printer.id && it.status == "PRINTING" }
            val resolvedStatus = if (printingJobs.isNotEmpty()) "PRINTING" else "IDLE"
            if (printer.status != resolvedStatus) {
                repository.updatePrinter(printer.copy(status = resolvedStatus))
            }
        }
    }

    // --- Platforms Sync Operations ---
    fun selectSyncPlatform(platformName: String) {
        _selectedSyncPlatform.value = platformName
        _syncedExternalOrders.value = emptyList() // Clear old sync to prompt scanning
    }

    fun togglePlatformConnection(platformName: String) {
        _platformConnections.value = _platformConnections.value.map {
            if (it.platformName == platformName) {
                it.copy(isConnected = !it.isConnected)
            } else it
        }
    }

    fun syncPlatformOrders() {
        val platformName = _selectedSyncPlatform.value
        val conn = _platformConnections.value.firstOrNull { it.platformName == platformName }
        if (conn == null || !conn.isConnected) {
            // Can only sync if credential exists & connected
            return
        }

        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val results = PlatformSyncSimulator.fetchOrdersFromApi(platformName)
                // Filter out those already imported (match by platformOrderId in current database)
                val currentOrders = orders.value
                val filtered = results.map { ext ->
                    val alreadyImported = currentOrders.any { o -> o.platformSource == ext.platform && o.platformOrderId == ext.id }
                    ext.copy(isImported = alreadyImported)
                }
                _syncedExternalOrders.value = filtered
            } catch (e: Exception) {
                // Recoverable gracefully
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun importExternalOrder(extOrder: ExternalPlatformOrder, targetPrinterId: Long?) {
        viewModelScope.launch {
            // First select or dynamically create a client based on the order info
            val clientPhone = extOrder.clientPhone
            var resolvedClientId: Long? = null

            val matchingClient = clients.value.firstOrNull { it.phone == clientPhone }
            if (matchingClient != null) {
                resolvedClientId = matchingClient.id
            } else {
                // Auto create client profile
                val newClient = Client(
                    name = extOrder.clientName,
                    phone = extOrder.clientPhone,
                    email = "${extOrder.clientName.lowercase().replace(" ", "")}@plataforma.com",
                    address = extOrder.clientAddress,
                    note = "Criado via importação de pedidos ${extOrder.platform}"
                )
                resolvedClientId = repository.insertClient(newClient)
            }

            val printerObj = targetPrinterId?.let { repository.getPrinterById(it) }

            val importedOrder = PrintOrder(
                clientId = resolvedClientId,
                clientName = extOrder.clientName,
                itemName = extOrder.itemName,
                quantity = 1,
                filamentType = "PLA", // Default starting
                filamentColor = "Preto", // Default starting
                weightGrams = extOrder.weightGrams,
                printTimeHours = extOrder.printTimeHours,
                priceCharged = extOrder.priceCharged,
                platformSource = extOrder.platform,
                platformOrderId = extOrder.id,
                status = if (targetPrinterId != null) "PRINTING" else "QUEUE",
                printingProgress = 0.0f,
                assignedPrinterId = targetPrinterId,
                printerName = printerObj?.name ?: "",
                createdAt = System.currentTimeMillis()
            )

            repository.insertOrder(importedOrder)
            updateMachineStatuses()

            // Update local synced check
            _syncedExternalOrders.value = _syncedExternalOrders.value.map {
                if (it.id == extOrder.id) it.copy(isImported = true) else it
            }
        }
    }

    // --- Filament Prices Search ---
    fun selectSearchMaterial(material: String) {
        _searchMaterial.value = material
        searchFilamentPrices(material)
    }

    fun searchFilamentPrices(material: String) {
        viewModelScope.launch {
            _isSearchingPrices.value = true
            try {
                val results = FilamentPriceService.getCheapestFilaments(material)
                _filamentOffers.value = results
            } catch (e: Exception) {
                // Recoverable gracefully
            } finally {
                _isSearchingPrices.value = false
            }
        }
    }

    // --- Sub-Tab for prices & calculations ---
    private val _pricingSubTab = MutableStateFlow(0) // 0: Calculadora, 1: Catálogos, 2: Gastos/Compras, 3: Cotação AI
    val pricingSubTab = _pricingSubTab.asStateFlow()

    fun selectPricingSubTab(subIndex: Int) {
        _pricingSubTab.value = subIndex
    }

    // --- 3D Printing Cost Calculator States ---
    private val _calcWeightGrams = MutableStateFlow("150.0")
    val calcWeightGrams = _calcWeightGrams.asStateFlow()

    private val _calcPrintTimeHours = MutableStateFlow("4.5")
    val calcPrintTimeHours = _calcPrintTimeHours.asStateFlow()

    private val _calcFilamentPriceRoll = MutableStateFlow("120.0")
    val calcFilamentPriceRoll = _calcFilamentPriceRoll.asStateFlow()

    private val _calcPrinterPowerW = MutableStateFlow("350")
    val calcPrinterPowerW = _calcPrinterPowerW.asStateFlow()

    private val _calcElectricityCostKwh = MutableStateFlow("0.85")
    val calcElectricityCostKwh = _calcElectricityCostKwh.asStateFlow()

    private val _calcLaborCostHour = MutableStateFlow("15.0")
    val calcLaborCostHour = _calcLaborCostHour.asStateFlow()

    private val _calcProfitMarginPercent = MutableStateFlow("50")
    val calcProfitMarginPercent = _calcProfitMarginPercent.asStateFlow()

    private val _calcMiscCostPercent = MutableStateFlow("15")
    val calcMiscCostPercent = _calcMiscCostPercent.asStateFlow()

    fun setCalcWeightGrams(w: String) { _calcWeightGrams.value = w }
    fun setCalcPrintTimeHours(t: String) { _calcPrintTimeHours.value = t }
    fun setCalcFilamentPriceRoll(p: String) { _calcFilamentPriceRoll.value = p }
    fun setCalcPrinterPowerW(w: String) { _calcPrinterPowerW.value = w }
    fun setCalcElectricityCostKwh(e: String) { _calcElectricityCostKwh.value = e }
    fun setCalcLaborCostHour(l: String) { _calcLaborCostHour.value = l }
    fun setCalcProfitMarginPercent(p: String) { _calcProfitMarginPercent.value = p }
    fun setCalcMiscCostPercent(m: String) { _calcMiscCostPercent.value = m }

    fun fillCalculatorFromCatalog(item: CatalogItem) {
        _calcWeightGrams.value = item.weightGrams.toString()
        _calcPrintTimeHours.value = item.printTimeHours.toString()
        _calcFilamentPriceRoll.value = if (item.filamentType == "PETG") "135.0" else "120.0"
        _pricingSubTab.value = 0 // Switch directly to calculator
    }

    // --- Catalog Operations ---
    fun saveCatalogItem(name: String, description: String, weightGrams: Float, printTimeHours: Float, filamentType: String, defaultPrice: Double) {
        viewModelScope.launch {
            val item = CatalogItem(
                name = name,
                description = description,
                weightGrams = weightGrams,
                printTimeHours = printTimeHours,
                filamentType = filamentType,
                defaultPrice = defaultPrice
            )
            repository.insertCatalogItem(item)
        }
    }

    fun deleteCatalogItem(item: CatalogItem) {
        viewModelScope.launch {
            repository.deleteCatalogItem(item)
        }
    }

    // --- Expense Operations ---
    fun saveExpense(description: String, category: String, amount: Double, qty: Int) {
        viewModelScope.launch {
            val expense = Expense(
                description = description,
                category = category,
                amount = amount,
                qty = qty,
                date = System.currentTimeMillis()
            )
            repository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // --- Real-time print simulation loop ---
    private fun startPrintingSimulation() {
        viewModelScope.launch {
            while (true) {
                delay(6000) // Trigger progression step every 6 seconds
                val activeJobs = orders.value.filter { it.status == "PRINTING" }
                if (activeJobs.isNotEmpty()) {
                    activeJobs.forEach { job ->
                        val increment = (0.01f + Random.nextFloat() * 0.04f) // Increments between 1% and 5%
                        val nextProgress = (job.printingProgress + increment).coerceAtMost(1.0f)
                        if (nextProgress >= 1.0f) {
                            // Finish printing, move to Post Processing!
                            val updatedJob = job.copy(
                                printingProgress = 1.0f,
                                status = "POST_PROCESS"
                            )
                            repository.updateOrder(updatedJob)
                        } else {
                            // Adjust progress
                            val updatedJob = job.copy(printingProgress = nextProgress)
                            repository.updateOrder(updatedJob)
                        }
                    }
                    updateMachineStatuses()
                }
            }
        }
    }
}
