package net.mamoe.n

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File

val Json = Json {
    this.ignoreUnknownKeys = true
    this.isLenient = true
    this.encodeDefaults = true
}

inline fun <reified T : Any> String.deserialize(): T = Json.decodeFromString(this)


inline fun <reified T : Any> T.serialize(format: StringFormat, serializer: KSerializer<T> = format.serializersModule.serializer()): String {
    return format.encodeToString(serializer, this)
}


inline fun <reified T:Any> File.writeData(data: T){
    this.writeText(data.serialize(Json))
}


inline fun <reified T : Any> File.deserialize(): T?{
    val text = this.readText()
    if(text.isEmpty()){
        return null
    }
    return Json.decodeFromString(text)
}

fun createUuid4(): String = java.util.UUID.randomUUID().toString()
