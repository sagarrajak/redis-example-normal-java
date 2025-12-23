package com.debaterr.app


fun main() {
    val employees = listOf(
        Employee(
            "EMP-001", "Alice Johnson", 85000.0,
            listOf("Kotlin", "Redis", "AWS"),
            Address("123 Maple St", "New York", "10001")
        ),
        Employee(
            "EMP-002", "Bob Smith", 72000.0,
            listOf("Java", "Spring Boot"),
            Address("456 Oak Ave", "Chicago", "60605")
        ),
        Employee(
            "EMP-003", "Charlie Davis", 95000.0,
            listOf("Python", "Data Science", "SQL"),
            Address("789 Pine Rd", "San Francisco", "94103")
        ),
        Employee(
            "EMP-004", "Diana Prince", 110000.0,
            listOf("Security", "Go", "Docker"),
            null // Testing null address
        ),
        Employee(
            "EMP-005", "Evan Wright", 68000.0,
            listOf("JavaScript", "React"),
            Address("321 Birch Ln", "Austin", "73301")
        ),
        Employee(
            "EMP-006", "Fiona Chen", 105000.0,
            listOf("Kotlin", "Kubernetes", "NoSQL"),
            Address("654 Cedar Blvd", "Seattle", "98101")
        ),
        Employee(
            "EMP-007", "George Miller", 88000.0,
            listOf("C++", "Embedded Systems"),
            Address("987 Elm St", "Denver", "80202")
        ),
        Employee(
            "EMP-008", "Hannah Abbott", 76000.0,
            listOf("UI/UX", "Figma", "CSS"),
            Address("159 Willow Way", "Portland", "97201")
        ),
        Employee(
            "EMP-009", "Ian Malcolm", 125000.0,
            listOf("Chaos Theory", "Python", "R"),
            Address("753 Aspen Dr", "Dallas", "75201")
        ),
        Employee(
            "EMP-010", "Julia Roberts", 92000.0,
            listOf("Management", "Agile", "Jira"),
            Address("246 Cherry Ct", "Miami", "33101")
        )
    )
    val employee = Employee(
        "EMP-010", "Julia Roberts", 92000.0,
        listOf("Management", "Agile", "Jira"),
        Address("246 Cherry Ct", "Miami", "33101")
    )
    val initJedis = initJedis()
    val objectMapper = ObjectMapperConfig.objectMapper
    val redisSetOperation = RedisSetOperation<Employee>(initJedis, "somekeystatic", objectMapper, Employee::class.java)
    redisSetOperation.setAdd(*employees.toTypedArray())
    redisSetOperation.setAdd(employees[0])
    print(redisSetOperation.size)
    println(redisSetOperation.contains(employees[1]))
    println(redisSetOperation.contains(employee))
    for (item in redisSetOperation) {
        println(item)
    }
}