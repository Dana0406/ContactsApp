package com.example.contacts.model

import com.github.javafaker.PhoneNumber

data class Contact(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)
