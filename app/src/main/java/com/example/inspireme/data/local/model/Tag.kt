package com.example.inspireme.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey
    val id: String,
    val name: String
)