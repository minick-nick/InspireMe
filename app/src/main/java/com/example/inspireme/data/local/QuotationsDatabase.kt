package com.example.inspireme.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inspireme.data.Quotation

@Database(entities = [Quotation.LocalQuotation::class], version = 1, exportSchema = false)
abstract class QuotationDatabase : RoomDatabase() {
    abstract fun quotationDao(): QuotationDao

    companion object {
        @Volatile
        private var Instance: QuotationDatabase? = null

        fun getDatabase(context: Context): QuotationDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, QuotationDatabase::class.java, "quotation_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}