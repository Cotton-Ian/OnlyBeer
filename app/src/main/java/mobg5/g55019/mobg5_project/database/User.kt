package mobg5.g55019.mobg5_project.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val mail:String,

    @ColumnInfo(name="Date")
    val date: Long
)
