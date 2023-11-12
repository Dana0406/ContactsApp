package com.example.contacts.model

import com.github.javafaker.Faker
import java.util.*
import kotlin.collections.ArrayList

typealias ContactListener = (contactsList: List<Contact>) -> Unit

class ContactsService {
    private var contacts = mutableListOf<Contact>()

    private val listeners = mutableSetOf<ContactListener>()

    init {
        val locale = Locale("ru")
        val faker = Faker(locale)
        contacts = (1..100).map {
            Contact(
                id = it.toLong(),
                firstName = faker.name().firstName(),
                lastName = faker.name().lastName(),
                phoneNumber = faker.phoneNumber().phoneNumber()
            )
        }.toMutableList()
    }

    fun getContacts(): List<Contact> {
        return contacts
    }

    fun addContact(contact: Contact) {
        contacts = ArrayList(contacts)
        contacts.add(contact)
        notifyChanges()
    }

    fun editContact(contact: Contact) {
        val index = contacts.indexOfFirst { it.id == contact.id }
        val updateContact = contact.copy(firstName = contact.firstName, lastName = contact.lastName,
            phoneNumber = contact.phoneNumber)
        contacts = ArrayList(contacts)
        contacts[index] = updateContact
        notifyChanges()
    }

    fun deleteContacts(contactsToDelete: List<Contact>) {
        contacts = ArrayList(contacts)
        contacts.removeAll(contactsToDelete)
        notifyChanges()
    }

    fun addCheckBox() {
        contacts = ArrayList(contacts)
        contacts.forEach { it.isCheckBoxVisible = true }
        notifyChanges()
    }

    fun deleteCheckBox() {
        contacts = ArrayList(contacts)
        contacts.forEach {it.isCheckBoxVisible = false }
        notifyChanges()
    }

    fun getSelectedContacts(): List<Contact> {
        return contacts.filter { it.isSelected }
    }

    fun addListener(listener: ContactListener) {
        listeners.add(listener)
        listener.invoke(contacts)
    }

    fun removeListener(listener: ContactListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(contacts) }
    }
}