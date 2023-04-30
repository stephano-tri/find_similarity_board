package eom.demo.ejh_board

import com.google.gson.*
import java.lang.reflect.Type
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDatetimeAdapter: JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ZonedDateTime {
        return try {
            ZonedDateTime.parse(json.asString)
        } catch (e: Exception) {
            throw JsonParseException("Failed to deserialize instance", e)
        }
    }

    override fun serialize(src: ZonedDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.format(DateTimeFormatter.ISO_INSTANT))
    }
}
