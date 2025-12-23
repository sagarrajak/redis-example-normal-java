package com.debaterr.app

import com.fasterxml.jackson.databind.ObjectMapper
import redis.clients.jedis.Jedis
import java.io.Serializable

class RedisSetOperation<T: Serializable>(
    private val jedis: Jedis,
    private val key: String,
    private val objectMapper: ObjectMapper,
    private val clazz: Class<T>
): Set<T> {
    private fun toJson(value: T): String {
        return objectMapper.writeValueAsString(value) ?: throw IllegalArgumentException("invalid argument");
    }

    private fun deserialize(value: String): T {
        return try {
            objectMapper.readValue(value, clazz)
        } catch (e: Exception) {
            throw Exception("Failed to deserialize string to ${clazz.simpleName}")
        }
    }

    fun setAdd(vararg newvalue: T) {
        val serializedValues = newvalue.map { t ->
            toJson(t)
        }.toTypedArray()
        jedis.sadd(key, *serializedValues);
    }

    fun setRemove(vararg newValue: T) {
        val serializedValues = newValue.map { t ->
            toJson(t)
        }.toTypedArray()
        jedis.srem(key, *serializedValues)
    }

    fun setIsMember(value: T): Boolean {
        val serializedValue = toJson(value)
        return jedis.sismember(key, serializedValue)
    }


    fun setIsMember(vararg value: T): List<Boolean> {
        val serializedValue = value.map {  toJson(it)}.toTypedArray()
        return jedis.smismember(key, *serializedValue)
    }

    fun setSize(): Long {
        return jedis.scard(key)
    }

    fun setPop(): T {
        val popedValue = jedis.spop(key)
        val serializedValue = deserialize(popedValue)
        return serializedValue;
    }

    fun moveItem(toKey: String, value: T): Boolean {
        // Redis SMOVE returns 1 on success, 0 if element not found
        return jedis.smove(key, toKey, toJson(value)) == 1L
    }

    override val size: Int
        get() = setSize().toInt()

    override fun isEmpty(): Boolean {
        return setSize() == 0L;
    }

    override fun contains(element: T): Boolean {
        return setIsMember(element)
    }

    override fun iterator(): Iterator<T> {
        return jedis.smembers(key)
            .map { deserialize(it) }
            .iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty()) return true
        val serializedValues = elements.map { toJson(it) }.toTypedArray()
        val results = jedis.smismember(key, *serializedValues)
        return results.all { it }
    }
}