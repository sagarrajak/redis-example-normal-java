package com.debaterr.app.redisoperation

interface RedisSetStore<T>: Set<T>{
    fun add(vararg values: T): Long
    fun remove(vararg values: T): Long
    fun isMember(value: T): Boolean
    fun areMembers(vararg values: T): List<Boolean>
    fun pop(): T?
    fun move(toKey: String, value: T): Boolean
    fun getAll(): Set<T>
    override fun containsAll(elements: Collection<T>): Boolean
    override fun isEmpty(): Boolean
};