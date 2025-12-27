package com.debaterr.app.redisoperation

import com.google.protobuf.Message
import com.google.protobuf.Parser
import redis.clients.jedis.Jedis

class RedisSetOperationProtobuf<T: Message>(
    private val jedis: Jedis,
    private val key: String,
    private val parser: Parser<T>
): RedisSetStore<T> {
    private fun toByteArray(value: T): ByteArray {
        return value.toByteArray() ?: throw IllegalArgumentException("invalid argument");
    }

    private val binaryKey = key.toByteArray()

    private fun fromBytes(value: ByteArray): T {
        return try {
            parser.parseFrom(value)
        } catch (e: Exception) {
            throw Exception("Failed to deserialize binary value")
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
            .map { fromBytes(it.toByteArray()) }
            .iterator()
    }

    override fun add(vararg values: T): Long {
        val serializedValues = values.map { t ->
            toByteArray(t)
        }.toTypedArray()
        return jedis.sadd(binaryKey, *serializedValues);
    }

    override fun remove(vararg values: T): Long {
        val serializedValues = values.map { t ->
            toByteArray(t)
        }.toTypedArray()
        return jedis.srem(binaryKey, *serializedValues);
    }

    override fun isMember(value: T): Boolean {
        val serializedValue = toByteArray(value)
        return jedis.sismember(binaryKey, serializedValue)
    }

    override fun areMembers(vararg values: T): List<Boolean> {
        val serializedValue = values.map {  toByteArray(it)}.toTypedArray()
        return jedis.smismember(binaryKey, *serializedValue)
    }


    override fun pop(): T? {
        if (this.isEmpty()) return null;
        val popedValue = jedis.spop(binaryKey)
        val serializedValue = fromBytes(popedValue)
        return serializedValue;
    }

    override fun move(toKey: String, value: T): Boolean {
        // Redis SMOVE returns 1 on success, 0 if element not found
        val toKeyByteArray = toKey.toByteArray()
        return jedis.smove(binaryKey, toKeyByteArray, toByteArray(value)) == 1L
    }

    override fun getAll(): Set<T> {
        val smembers = jedis.smembers(binaryKey) ?: return setOf<T>()
        return smembers.map { fromBytes(it) }.toSet();
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty()) return true
        val serializedValues = elements.map { toByteArray(it) }.toTypedArray()
        val results = jedis.smismember(binaryKey, *serializedValues)
        return results.all { it }
    }
}