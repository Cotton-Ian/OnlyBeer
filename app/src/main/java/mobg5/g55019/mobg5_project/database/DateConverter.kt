package mobg5.g55019.mobg5_project.database

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

        @TypeConverter
        fun toDate(dateLong: Long?): Date? {
            return dateLong?.let { Date(it) }
        }

        @TypeConverter
        fun fromDate(date: Date?): Long? {
            return if (date == null) null else date.getTime()
        }

}