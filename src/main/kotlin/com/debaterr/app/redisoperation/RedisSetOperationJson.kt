package com.debaterr.app.redisoperation

import com.fasterxml.jackson.databind.ObjectMapper
import redis.clients.jedis.Jedis
import java.io.Serializable

class RedisSetOperationJson<T: Serializable>(
    private val jedis: Jedis,
    private val key: String,
    private val objectMapper: ObjectMapper,
    private val clazz: Class<T>
):  RedisSetStore<T> {
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


    private fun getSize(): Long {
        return jedis.scard(key)
    }

    override val size: Int
        get() = getSize().toInt();

    override fun isEmpty(): Boolean {
        return getSize() == 0L;
    }

    override fun contains(element: T): Boolean {
        return isMember(element)
    }

    override fun iterator(): Iterator<T> {
        return jedis.smembers(key)
            .map { deserialize(it) }
            .iterator()
    }

    override fun add(vararg values: T): Long {
        val serializedValues = values.map { t ->
            toJson(t)
        }.toTypedArray()
        return jedis.sadd(key, *serializedValues);
    }

    override fun remove(vararg values: T): Long {
        val serializedValues = values.map { t ->
            toJson(t)
        }.toTypedArray()
        return jedis.srem(key, *serializedValues);
    }

    override fun isMember(value: T): Boolean {
        val serializedValue = toJson(value)
        return jedis.sismember(key, serializedValue)
    }

    override fun areMembers(vararg values: T): List<Boolean> {
        val serializedValue = values.map {  toJson(it)}.toTypedArray()
        return jedis.smismember(key, *serializedValue)
    }


    override fun pop(): T? {
        if (this.isEmpty()) return null;
        val popedValue = jedis.spop(key)
        val serializedValue = deserialize(popedValue)
        return serializedValue;
    }

    override fun move(toKey: String, value: T): Boolean {
        // Redis SMOVE returns 1 on success, 0 if element not found
        return jedis.smove(key, toKey, toJson(value)) == 1L
    }

    override fun getAll(): Set<T> {
        val smembers = jedis.smembers(this.key) ?: return setOf<T>()
        return smembers.map { deserialize(it) }.toSet();
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty()) return true
        val serializedValues = elements.map { toJson(it) }.toTypedArray()
        val results = jedis.smismember(key, *serializedValues)
        return results.all { it }
    }
}