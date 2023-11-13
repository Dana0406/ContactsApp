package com.example.contacts.model

data class Contact(
    var id: Long,
    var firstLastName: String,
    var phoneNumber: String,
    var isCheckBoxVisible: Boolean = false,
    var isSelected: Boolean = false
)
