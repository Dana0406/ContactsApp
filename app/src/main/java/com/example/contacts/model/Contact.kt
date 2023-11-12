package com.example.contacts.model

data class Contact(
    var id: Long,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var isCheckBoxVisible: Boolean = false,
    var isSelected: Boolean = false
)
