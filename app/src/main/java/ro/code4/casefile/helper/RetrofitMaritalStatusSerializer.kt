
package ro.code4.casefile.helper

import com.google.gson.*
import ro.code4.casefile.ui.patient.addpatient.MaritalStatus
import java.lang.reflect.Type

class RetrofitMaritalStatusSerializer : JsonSerializer<MaritalStatus>, JsonDeserializer<MaritalStatus> {
    override fun serialize(src: MaritalStatus?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        val result = src?.id
        return JsonPrimitive(result)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MaritalStatus? {
        return when(json?.asInt) {
            0 -> MaritalStatus.NOT_MARRIED
            1 -> MaritalStatus.MARRIED
            2 -> MaritalStatus.DIVORCED
            3 -> MaritalStatus.WIDOWED
            else -> null
        }
    }

}
