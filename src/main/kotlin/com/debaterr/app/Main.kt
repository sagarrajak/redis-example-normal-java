package com.debaterr.app

import com.debaterr.app.redisoperation.RedisSetOperationProtobuf
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import redis.clients.jedis.Jedis


fun initJedis(): Jedis {
    val jedis = Jedis("localhost", 6379)
    jedis.connect();
    return jedis
}


class ObjectMapperConfig {
    companion object {
        val objectMapper: ObjectMapper = ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
    }
}

fun deserializeExample() {
    val json = """
           {
          "id" : "123",
          "name" : "John Smith",
          "email" : "john@example.com",
          "score" : 95,
          "department" : "Engineering",
          "active" : true,
          "phoneNumber" : "555-1234"
        }
        """.trimIndent()
    try {
        val user = ObjectMapperConfig.objectMapper.readValue(json, User::class.java)
        println("Deserialized user: ${user.name}")
        println(user)
    } catch (e: Exception) {
        throw e
    }
}

fun main() {
    val initJedis = initJedis()
//    val jsonMapper = ObjectMapperConfig.objectMapper;
//    val user = User(
//        id = "123",
//        name = "John Smith",
//        email = "john@example.com",
//        score = 95,
//        department = "Engineering",
//        phoneNumber = "555-1234"
//    )
//    val someOtherUser = SomeOtherUser()
//        .withId(10)
//        .withAge(30)
//        .withName("Sagar")
//        .withEmail("test@gmail.com")
//        .withTags(listOf("test", "test2"))
//
//    val writeValueAsString1 = jsonMapper.writeValueAsString(someOtherUser)
//    println(writeValueAsString1)
//    val writeValueAsString = jsonMapper.writeValueAsString(user)
//    println(writeValueAsString)
//    deserializeExample();
//
//
//    /// list example
//    val redisListOperation = RedisListOperation(jedis = initJedis, "testkey")
//    redisListOperation.listLeftPush("Sagar", "test", "test2");
//
//    if(!redisListOperation.contains("rightpush"))
//        redisListOperation.listRightPush("rightpush")
//
//    redisListOperation.listRightPush("rightpush1")
//    redisListOperation.listRightPush("rightpush1")
//
//    for (item in redisListOperation) {
//        println(item);
//    }

    val redisSetOperationProtobuf = RedisSetOperationProtobuf<ProtobuffUser>(initJedis, "protokey", ProtobuffUser.parser());
    val user1 = ProtobuffUser.newBuilder()
        .setId(1)
        .setName("User One")
        .addEmails("user1@example.com")
        .build()

    val user2 = ProtobuffUser.newBuilder()
        .setId(2)
        .setName("User Two")
        .addEmails("user2@example.com")
        .build()

    val user3 = ProtobuffUser.newBuilder()
        .setId(3)
        .setName("User Three")
        .addEmails("user3@example.com")
        .build()

    val user4 = ProtobuffUser.newBuilder()
        .setId(4)
        .setName("User Four")
        .addEmails("user4@example.com")
        .build()

    val user5 = ProtobuffUser.newBuilder()
        .setId(5)
        .setName("User Five")
        .addEmails("user5@example.com")
        .build()

    redisSetOperationProtobuf.add(user1)
    redisSetOperationProtobuf.add(user2)
    redisSetOperationProtobuf.add(user3)
    redisSetOperationProtobuf.add(user4)
    redisSetOperationProtobuf.add(user5)

    for (user in redisSetOperationProtobuf) {
        println(user)
    }
}


