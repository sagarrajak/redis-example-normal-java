package com.debaterr.app

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

// Complex data class example
data class User(
    @JsonProperty("id")
    val id: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("score")
    val score: Int,

    @JsonProperty("department")
    val department: String,

    @JsonProperty("active")
    val active: Boolean = true,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val phoneNumber: String? = null
)

// Another complex object
data class Employee(
    @JsonProperty("employee_id")
    val employeeId: String,

    @JsonProperty("full_name")
    val fullName: String,

    @JsonProperty("salary")
    val salary: Double,

    @JsonProperty("skills")
    val skills: List<String>,

    @JsonProperty("address")
    val address: Address?
)

data class Address(
    @JsonProperty("street")
    val street: String,

    @JsonProperty("city")
    val city: String,

    @JsonProperty("zip_code")
    val zipCode: String
)