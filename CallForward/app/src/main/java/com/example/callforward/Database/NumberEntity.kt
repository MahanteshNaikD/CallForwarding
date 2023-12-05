package com.example.callforward.Database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "number_table")
data class NumberEntity(
    @PrimaryKey(autoGenerate = true)
     var id: Int? = null,

    @ColumnInfo(name = "number")
     var number: String? = null,

    @ColumnInfo(name = "selectedSimId")
    var selectedSimId : String? = null,

    @ColumnInfo(name = "time")
    var time : String? = null,

    @ColumnInfo(name = "status")
    var status : String? = null





)