package com.debaterr.app

import redis.clients.jedis.Jedis
import redis.clients.jedis.params.LPosParams

class RedisListOperation(
    private val jedis: Jedis,
    private val key: String
): List<String> {

    override val size: Int
        get() = jedis.llen(key).toInt()

    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: String): Boolean {
        return jedis.lpos(key, element) != null
    }

    override fun get(index: Int): String {
        return jedis.lindex(key, index.toLong())
            ?: throw IndexOutOfBoundsException("Index: $index, Size: $size")
    }

    /**
     * Maps to LPOS: Returns the index of the first match, or -1.
     */
    override fun indexOf(element: String): Int {
        return jedis.lpos(key, element)?.toInt() ?: -1
    }

    /**
     * Maps to LPOS with reverse parameter: Searches from the tail.
     */
    override fun lastIndexOf(element: String): Int {
        // LPosParams().rank(-1) tells Redis to start searching from the end
        return jedis.lpos(key, element, LPosParams().rank(-1))?.toInt() ?: -1
    }

    /**
     * Maps to LRANGE: Fetches the range.
     * Note: Kotlin subList end is exclusive, so we subtract 1 for Redis.
     */
    override fun subList(fromIndex: Int, toIndex: Int): List<String> {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw IndexOutOfBoundsException()
        }
        if (fromIndex == toIndex) return emptyList()
        return jedis.lrange(key, fromIndex.toLong(), (toIndex - 1).toLong())
    }

    override fun iterator(): Iterator<String> {
        return listIterator()
    }

    override fun listIterator(): ListIterator<String> {
        return listRange(0, -1).listIterator()
    }

    override fun listIterator(index: Int): ListIterator<String> {
        return listRange(0, -1).listIterator(index)
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        // Performance Warning: This is O(N*M)
        return elements.all { contains(it) }
    }

    // Helper used by iterators to fetch fresh data
    private fun listRange(start: Long, end: Long): List<String> {
        return jedis.lrange(key, start, end)
    }


    /// redis operation
    fun listRightPush( vararg items: String): Long {
        return jedis.rpush(key, *items)
    }

    fun listLeftPush(vararg items: String): Long {
        return jedis.lpush(key, *items)
    }

    fun listRightPop(key: String): String? {
        return jedis.rpop(key)
    }

    fun listLeftPop(key: String): String? {
        return jedis.lpop(key)
    }

    fun listIndex(index: Long): String? {
        return jedis.lindex(key, index)
    }

    fun listTrim(start: Long, end: Long): String {
        return jedis.ltrim(key, start, end)
    }
}