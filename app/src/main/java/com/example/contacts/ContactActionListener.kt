package com.example.contacts

import com.example.contacts.model.Contact

interface ContactActionListener {
    fun onContactEdit(contact: Contact)
    fun addContact(contact: Contact)
}