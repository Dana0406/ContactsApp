package com.example.contacts.model

import com.github.javafaker.Faker
import java.util.*

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

    fun getContacts(): List<Contact>{
        return contacts
    }

    fun addContact(contact: Contact){
        contacts.add(contact)
        notifyChanges()
    }

    fun deleteContact(contact: Contact){
        contacts.remove(contact)
        notifyChanges()
    }

    fun moveContact(contact: Contact, moveBy: Int){
        val oldIndex = contacts.indexOfFirst { it.id == contact.id }
        if(oldIndex == -1) return
        val newIndex = oldIndex + moveBy
        if(newIndex < 0 || newIndex >= contacts.size) return
        Collections.swap(contacts, oldIndex, newIndex)
        notifyChanges()
    }

    fun addListener(listener: ContactListener){
        listeners.add(listener)
        listener.invoke(contacts)
    }

    fun removeListener(listener: ContactListener){
        listeners.remove(listener)
    }

    private fun notifyChanges(){
        listeners.forEach{it.invoke(contacts)}
    }
}