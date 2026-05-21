package com.example.data

import kotlinx.coroutines.delay
import kotlin.random.Random

data class PlatformConnection(
    val platformName: String, // "Mercado Livre", "Shopee", "Nuvemshop"
    val storeName: String,
    val token: String,
    val isConnected: Boolean = true
)

data class ExternalPlatformOrder(
    val id: String,
    val platform: String, // "MERCADO_LIVRE", "SHOPEE", "NUVEMSHOP"
    val itemName: String,
    val clientName: String,
    val clientPhone: String,
    val clientAddress: String,
    val weightGrams: Float,
    val printTimeHours: Float,
    val priceCharged: Double,
    val statusText: String, // e.g. "Pagamento Confirmado", "Pendente"
    val isImported: Boolean = false
)

object PlatformSyncSimulator {

    // Predefined connections
    val defaultConnections = listOf(
        PlatformConnection("Mercado Livre", "Inova3D Store", "ml_tok_928131...", true),
        PlatformConnection("Shopee", "Oficina3D Oficial", "shp_tok_089182...", true),
        PlatformConnection("Nuvemshop", "Arte3D Prime", "nv_tok_556112...", false)
    )

    // Generate simulated orders that have been purchased online but not yet imported into production
    fun getPendingExternalOrders(platform: String): List<ExternalPlatformOrder> {
        val orders = mutableListOf<ExternalPlatformOrder>()
        when (platform) {
            "MERCADO_LIVRE" -> {
                orders.add(
                    ExternalPlatformOrder(
                        id = "ML-998127391",
                        platform = "MERCADO_LIVRE",
                        itemName = "Suporte de Parede Alexa Echo Dot",
                        clientName = "Bruno Menezes",
                        clientPhone = "(11) 96123-4567",
                        clientAddress = "Av. Paulista, 1500 - São Paulo, SP",
                        weightGrams = 65f,
                        printTimeHours = 2.0f,
                        priceCharged = 45.90,
                        statusText = "Pagamento Aprovado"
                    )
                )
                orders.add(
                    ExternalPlatformOrder(
                        id = "ML-998127402",
                        platform = "MERCADO_LIVRE",
                        itemName = "Estatueta Yoda Star Wars 15cm",
                        clientName = "Carolina Toledo",
                        clientPhone = "(19) 98822-1100",
                        clientAddress = "Rua das Flores, 88 - Campinas, SP",
                        weightGrams = 210f,
                        printTimeHours = 7.5f,
                        priceCharged = 119.00,
                        statusText = "Pagamento Aprovado"
                    )
                )
            }
            "SHOPEE" -> {
                orders.add(
                    ExternalPlatformOrder(
                        id = "SHP-2026A8F9",
                        platform = "SHOPEE",
                        itemName = "Protetor Cabo Organizador Spiral",
                        clientName = "Renato Oliveira",
                        clientPhone = "(21) 97412-8899",
                        clientAddress = "Rua do Catete, 120 - Rio de Janeiro, RJ",
                        weightGrams = 35f,
                        printTimeHours = 1.2f,
                        priceCharged = 25.00,
                        statusText = "Aguardando Envio"
                    )
                )
                orders.add(
                    ExternalPlatformOrder(
                        id = "SHP-2026A9G0",
                        platform = "SHOPEE",
                        itemName = "Luminária de Mesa Nuvem LED",
                        clientName = "Juliana Fraga",
                        clientPhone = "(31) 98111-2233",
                        clientAddress = "Av. do Contorno, 8000 - Belo Horizonte, MG",
                        weightGrams = 320f,
                        printTimeHours = 12.0f,
                        priceCharged = 159.90,
                        statusText = "Aguardando Envio"
                    )
                )
            }
            "NUVEMSHOP" -> {
                orders.add(
                    ExternalPlatformOrder(
                        id = "NUV-4401",
                        platform = "NUVEMSHOP",
                        itemName = "Engrenagem Dupla Reposição Delrin (PETG)",
                        clientName = "Eng. Rodolfo Castro",
                        clientPhone = "(12) 99182-3746",
                        clientAddress = "Avenida Brasil, 32 - São José dos Campos, SP",
                        weightGrams = 90f,
                        printTimeHours = 3.5f,
                        priceCharged = 80.00,
                        statusText = "Pago"
                    )
                )
            }
        }
        return orders
    }

    // Simulate standard network delay of fetching orders online
    suspend fun fetchOrdersFromApi(platformName: String): List<ExternalPlatformOrder> {
        delay(1200) // Simulate REST delay
        val platformEnum = when (platformName) {
            "Mercado Livre" -> "MERCADO_LIVRE"
            "Shopee" -> "SHOPEE"
            else -> "NUVEMSHOP"
        }
        return getPendingExternalOrders(platformEnum)
    }
}
