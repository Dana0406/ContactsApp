package com.example.contacts

import android.app.Application
import com.example.contacts.model.ContactsService

class App : Application() {
    val contactsService = ContactsService()
}