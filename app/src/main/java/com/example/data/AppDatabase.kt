package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Client::class, Printer::class, PrintOrder::class, CatalogItem::class, Expense::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun printerDao(): PrinterDao
    abstract fun printOrderDao(): PrintOrderDao
    abstract fun catalogItemDao(): CatalogItemDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "print_flow_db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed mock data
                        val now = System.currentTimeMillis()
                        // Populate default clients
                        db.execSQL("INSERT INTO clients (id, name, phone, email, address, note) VALUES (1, 'Marcos Silva', '(11) 98765-4321', 'marcos@email.com', 'Rua Augusta, 1000 - SP', 'Cliente exigente, prefere acabados finos')")
                        db.execSQL("INSERT INTO clients (id, name, phone, email, address, note) VALUES (2, 'Ana Souza (Arte3D)', '(21) 99888-7766', 'ana.arte@email.com', 'Av. Atlântica, 450 - RJ', 'Trabalha com action figures e estátuas de colecionador')")
                        db.execSQL("INSERT INTO clients (id, name, phone, email, address, note) VALUES (3, 'Felipe Santos', '(31) 97777-6655', 'felipe.s@email.com', 'Rua Bahia, 200 - BH', 'Pedidos recorrentes de protótipos de engenharia')")

                        // Populate default printers
                        db.execSQL("INSERT INTO printers (id, name, model, status, ipAddress) VALUES (1, 'Bambu Lab P1S', 'Bambu Lab P1S', 'PRINTING', '192.168.1.150')")
                        db.execSQL("INSERT INTO printers (id, name, model, status, ipAddress) VALUES (2, 'Creality Ender 3 V3', 'Creality Ender 3 V3', 'IDLE', '192.168.1.162')")

                        // Populate initial orders
                        // Order 1: Vaso Espiral PLA Ouro (Printing)
                        db.execSQL("INSERT INTO print_orders (id, clientId, clientName, itemName, quantity, filamentType, filamentColor, weightGrams, printTimeHours, priceCharged, platformSource, platformOrderId, status, printingProgress, assignedPrinterId, printerName, createdAt, deadline) VALUES " +
                                "(1, 2, 'Ana Souza (Arte3D)', 'Vaso Espiral Low Poly', 1, 'PLA', 'Ouro Silk', 180.0, 4.5, 95.0, 'MANUAL', '', 'PRINTING', 0.65, 1, 'Bambu Lab P1S', " + now + ", " + (now + 24*3600*1000) + ")")

                        // Order 2: Suporte Headset (Queue)
                        db.execSQL("INSERT INTO print_orders (id, clientId, clientName, itemName, quantity, filamentType, filamentColor, weightGrams, printTimeHours, priceCharged, platformSource, platformOrderId, status, printingProgress, assignedPrinterId, printerName, createdAt, deadline) VALUES " +
                                "(2, 3, 'Felipe Santos', 'Suporte Headset Gamer', 2, 'PETG', 'Preto Carbono', 260.0, 8.0, 140.0, 'MANUAL', '', 'QUEUE', 0.0, 2, 'Creality Ender 3 V3', " + now + ", " + (now + 48*3600*1000) + ")")

                        // Order 3: Action Figure Batman (Post-processing)
                        db.execSQL("INSERT INTO print_orders (id, clientId, clientName, itemName, quantity, filamentType, filamentColor, weightGrams, printTimeHours, priceCharged, platformSource, platformOrderId, status, printingProgress, assignedPrinterId, printerName, createdAt, deadline) VALUES " +
                                "(3, 2, 'Ana Souza (Arte3D)', 'Action Figure Batman 30cm', 1, 'PLA', 'Cinza Metálico', 450.0, 18.5, 290.0, 'MANUAL', '', 'POST_PROCESS', 1.0, 1, 'Bambu Lab P1S', " + (now - 24*3600*1000) + ", " + (now + 12*3600*1000) + ")")

                        // Seed Products Catalog
                        db.execSQL("INSERT INTO catalog_items (id, name, description, weightGrams, printTimeHours, filamentType, defaultPrice) VALUES (1, 'Vaso Espiral Low Poly', 'Lindo vaso de decoração com efeito espiralado geométrico', 180.0, 4.5, 'PLA', 95.0)")
                        db.execSQL("INSERT INTO catalog_items (id, name, description, weightGrams, printTimeHours, filamentType, defaultPrice) VALUES (2, 'Suporte Headset Gamer', 'Suporte universal de alta resistência para headsets', 130.0, 4.0, 'PETG', 70.0)")
                        db.execSQL("INSERT INTO catalog_items (id, name, description, weightGrams, printTimeHours, filamentType, defaultPrice) VALUES (3, 'Suporte de Controle PS5', 'Suporte de mesa elegante para controle de PS5 DualSense', 85.0, 3.0, 'PLA', 45.0)")
                        db.execSQL("INSERT INTO catalog_items (id, name, description, weightGrams, printTimeHours, filamentType, defaultPrice) VALUES (4, 'Action Figure Batman 30cm', 'Estátua super detalhada do Batman para colecionadores', 450.0, 18.5, 'PLA', 290.0)")

                        // Seed Purchases / Expenses
                        db.execSQL("INSERT INTO expenses (id, description, category, amount, qty, date) VALUES (1, 'Filamento PLA Ouro Silk 1kg - Voolt3D', 'FILAMENTO', 120.0, 1, " + (now - 5*24*3600*1000) + ")")
                        db.execSQL("INSERT INTO expenses (id, description, category, amount, qty, date) VALUES (2, 'Filamento PETG Preto Carbono 1kg - ESUN', 'FILAMENTO', 135.0, 1, " + (now - 4*24*3600*1000) + ")")
                        db.execSQL("INSERT INTO expenses (id, description, category, amount, qty, date) VALUES (3, 'Bico de Extrusão Latão 0.4mm E3D', 'EQUIPAMENTO', 25.0, 2, " + (now - 3*24*3600*1000) + ")")
                        db.execSQL("INSERT INTO expenses (id, description, category, amount, qty, date) VALUES (4, 'Energia Elétrica Mensal Estimada', 'ENERGIA', 65.0, 1, " + (now - 2*24*3600*1000) + ")")
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
