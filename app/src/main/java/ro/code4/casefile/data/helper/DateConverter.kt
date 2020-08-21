package ro.code4.casefile.data.helper

import androidx.room.TypeConverter
import java.util.Date

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? = if(timestamp != null && timestamp > -62135769600000) Date(timestamp) else null

    @TypeConverter
    @JvmStatic
    fun toTimestamp(date: Date?): Long? = date?.time
}
