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
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.databinding.EditDeleteDialogBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingDialog: EditDeleteDialogBinding
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
        bindingDialog = EditDeleteDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        adapter = ContactsAdapter(object : ContactActionListener {
            override fun onContactEdit(contact: Contact) {
                dialog.show()
                showCustomDialog(contact)
                contactsService.editContact(contact)
            }
        })

        val layoutManager = LinearLayoutManager(this)

        with(binding) {
            contactsRecyclerView.layoutManager = layoutManager
            contactsRecyclerView.adapter = adapter
            addContactButton = addButton
        }

        val animator = binding.contactsRecyclerView.itemAnimator
        if(animator is DefaultItemAnimator){
            animator.supportsChangeAnimations = true
        }

        contactsService.addListener(contactsListener)

        addTouch()

        addContactButton.setOnClickListener {
            dialog.show()

            showCustomDialog(Contact((contactsService.getContacts().size + 1).toLong(), "", "",""))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        contactsService.addCheckBox()
        adapter.updateData(contactsService.getContacts())
        with(binding) {
            addButton.visibility = View.GONE
            deleteButton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
        }

        binding.deleteButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val selectedContacts = contactsService.getSelectedContacts()

                if (selectedContacts.isNotEmpty()) {
                    contactsService.deleteContacts(selectedContacts)
                    adapter.updateData(contactsService.getContacts())
                    Toast.makeText(this@MainActivity, "Контакты удалены", Toast.LENGTH_SHORT).show()

                    with(binding) {
                        addButton.visibility = View.VISIBLE
                        deleteButton.visibility = View.GONE
                        cancelButton.visibility = View.GONE
                    }
                    contactsService.deleteCheckBox()

                    adapter.clearSelection()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Выберите контакты для удаления",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        binding.cancelButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                with(binding) {
                    addButton.visibility = View.VISIBLE
                    deleteButton.visibility = View.GONE
                    cancelButton.visibility = View.GONE
                }
                contactsService.deleteCheckBox()
                adapter.clearSelection()
            }

        })

        return super.onOptionsItemSelected(item)
    }

    private fun addTouch(){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        })

        itemTouchHelper.attachToRecyclerView(binding.contactsRecyclerView)
    }

    private fun showCustomDialog(contact: Contact) {
        with(bindingDialog) {
            if (contact.id != (contactsService.getContacts().size + 1).toLong()) {
                nameEditText.setText(contact.firstName)
                surnameEditText.setText(contact.lastName)
                phoneNumberEditText.setText(contact.phoneNumber)
            } else {
                nameEditText.text.clear()
                surnameEditText.text.clear()
                phoneNumberEditText.text.clear()
            }
        }

        bindingDialog.saveEditions.setOnClickListener {
            contactsService.addListener(contactsListener)

            if (dataChecking(contact)) {
                dialog.hide()
            }
        }

        bindingDialog.deleteEditions.setOnClickListener {
            dialog.hide()
            if (contact.id != (contactsService.getContacts().size + 1).toLong()) {
                bindingDialog.nameEditText.text.clear()
                bindingDialog.surnameEditText.text.clear()
                bindingDialog.phoneNumberEditText.text.clear()
            }
        }
    }

    private fun dataChecking(contact: Contact): Boolean {
        with(bindingDialog) {
            if (surnameEditText.text.toString().isEmpty()) {
                surnameEditText.error = "Введите данные"
                return false
            } else if (phoneNumberEditText.text.toString().isEmpty()) {
                phoneNumberEditText.error = "Введите данные"
                return false
            } else {
                contact.lastName = surnameEditText.text.toString()
                contact.firstName = nameEditText.text.toString()
                contact.phoneNumber = phoneNumberEditText.text.toString()

                if (contact.id == (contactsService.getContacts().size + 1).toLong()) {
                    contactsService.addContact(contact)
                } else {
                    contactsService.editContact(contact)
                }
                return true
            }
        }
    }

    private val contactsListener: ContactListener = {
        adapter.contacts = it
    }

    override fun onDestroy() {
        super.onDestroy()
        contactsService.removeListener(contactsListener)
    }
}