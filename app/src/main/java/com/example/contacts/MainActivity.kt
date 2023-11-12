package com.example.contacts

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.databinding.ActivityMainBinding
import com.example.contacts.model.Contact
import com.example.contacts.model.ContactListener
import com.example.contacts.model.ContactsService
import android.graphics.Color;
import android.widget.ImageButton
import com.example.contacts.databinding.EditContactBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingDialog: EditContactBinding
    private lateinit var adapter: ContactsAdapter
    private lateinit var dialog: Dialog

    private lateinit var addContactButton: ImageButton


    private val contactsService: ContactsService
        get() = (applicationContext as App).contactsService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this@MainActivity)
        bindingDialog = EditContactBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        adapter = ContactsAdapter(object : ContactActionListener {
            override fun onContactEdit(contact: Contact) {
                dialog.show()
                showCustomDialogForEditing(contact)
            }

            override fun addContact(contact: Contact) {

            }
        })

        val layoutManager = LinearLayoutManager(this)

        with(binding) {
            contactsRecyclerView.layoutManager = layoutManager
            contactsRecyclerView.adapter = adapter
            addContactButton = addButton
        }

        contactsService.addListener(contactsListener)

        addContactButton.setOnClickListener {
            dialog.show()

            var contactNew = Contact(
                0,
                "",
                ""
            )

            contactNew.id = (contactsService.getContacts().size + 1).toLong()
            contactsService.addContact(showCustomDialogForAdding(dataChecking(contactNew)))
        }

    }

    private fun showCustomDialogForEditing(contact: Contact) {
        with(bindingDialog) {
            surnameNameEditText.setText(contact.firstLastName)
            phoneNumberEditText.setText(contact.phoneNumber)
        }

        bindingDialog.saveEditions.setOnClickListener {
            contactsService.addListener(contactsListener)

            dataChecking(contact)
        }

        bindingDialog.deleteEditions.setOnClickListener {
            dialog.hide()
        }
    }

    private fun showCustomDialogForAdding(contact: Contact): Contact {
        bindingDialog.saveEditions.setOnClickListener {
            contactsService.addListener(contactsListener)

            dataChecking(contact)

            dialog.hide()

            bindingDialog.surnameNameEditText.text.clear()
            bindingDialog.phoneNumberEditText.text.clear()
        }

        bindingDialog.deleteEditions.setOnClickListener {
            dialog.hide()
            contactsService.deleteContact(contact)
        }
        return contact
    }

    private fun dataChecking(contact: Contact): Contact {
        with(bindingDialog) {
            if (!surnameNameEditText.text.toString()
                    .isEmpty() && !phoneNumberEditText.text.toString().isEmpty()
            ) {
                contact.firstLastName = surnameNameEditText.text.toString()
                contact.phoneNumber = phoneNumberEditText.text.toString()

                dialog.hide()

                bindingDialog.surnameNameEditText.text.clear()
                bindingDialog.phoneNumberEditText.text.clear()
            } else if (surnameNameEditText.text.toString().isEmpty()) {
                surnameNameEditText.error = "Введите данные"
            } else if (phoneNumberEditText.text.toString().isEmpty()) {
                phoneNumberEditText.error = "Введите данные"
            }
        }
        return contact
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
        contactsService.removeListener(contactsListener)
    }
}