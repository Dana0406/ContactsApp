package com.example.contacts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding
import com.example.contacts.model.ContactListener
import com.example.contacts.model.ContactsService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ContactsAdapter

    private val contactsService: ContactsService
        get() = (applicationContext as App).contactsService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ContactsAdapter()

        val layoutManager = LinearLayoutManager(this)

        with(binding) {
            contactsRecyclerView.layoutManager = layoutManager
            contactsRecyclerView.adapter = adapter
        }

        contactsService.addListener (contactsListener)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private val contactsListener: ContactListener = {
        adapter.contacts = it
    }

    override fun onDestroy() {
        super.onDestroy()
        contactsService.removeListener (contactsListener)
    }
}