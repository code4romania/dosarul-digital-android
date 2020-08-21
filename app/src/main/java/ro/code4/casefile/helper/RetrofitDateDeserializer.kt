
package ro.code4.casefile.helper

import com.google.gson.*
import ro.code4.casefile.helper.Constants.DATE_API_FORMAT
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DateDeserializer : JsonSerializer<Date?>, JsonDeserializer<Date?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        element: JsonElement,
        arg1: Type?,
        arg2: JsonDeserializationContext?
    ): Date? {
        val date = element.asString
        val format = SimpleDateFormat(DATE_API_FORMAT, Locale.US)
        format.timeZone = TimeZone.getTimeZone("GMT")
        return try {
            format.parse(date)
        } catch (exp: ParseException) {
            val format2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            format2.timeZone = TimeZone.getTimeZone("GMT")
            try {
                format2.parse(date)
            } catch (exp: ParseException) {
                null
            }
        }
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        if(src == null) return null
        val format = SimpleDateFormat(DATE_API_FORMAT, Locale.US)
        return JsonPrimitive(format.format(src))
    }
}
