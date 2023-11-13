package com.example.contacts.model

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.contacts.ContactsDiffCallback
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
                firstLastName = faker.name().firstName() + " " + faker.name().lastName(),
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
        val updateContact = contact.copy(firstLastName = contact.firstLastName, phoneNumber = contact.phoneNumber)
        contacts = ArrayList(contacts)
        contacts[index] = updateContact
        notifyChanges()
    }

    fun deleteContacts(contactsToDelete: List<Contact>) {
        contacts = ArrayList(contacts)
        contacts.removeAll(contactsToDelete)
        notifyChanges()
    }

    fun moveContact(contact: Contact, moveBy: Int) {
        val oldIndex = contacts.indexOfFirst { it.id == contact.id }
        if (oldIndex == -1) return
        val newIndex = oldIndex + moveBy
        if (newIndex < 0 || newIndex >= contacts.size) return
        Collections.swap(contacts, oldIndex, newIndex)
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

    fun toggleSelection(position: Int) {
        val contact = contacts[position]
        contact.isSelected = !contact.isSelected
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