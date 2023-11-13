package com.example.contacts

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.databinding.ItemContactBinding
import com.example.contacts.model.Contact

class ContactsDiffCallback(
    private val oldList: List<Contact>,
    private val newList: List<Contact>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}

class ContactsAdapter(
    private val actionListener: ContactActionListener
) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>(), ContactActionListener,
    View.OnClickListener {

    var contacts: List<Contact> = emptyList()
        set(value) {
            val diffCallback = ContactsDiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return ContactsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.binding) {
            holder.itemView.tag = contact

            surnameNameTextView.text = contact.firstLastName
            phoneNumberTextView.text = contact.phoneNumber
            check.visibility = if (contact.isCheckBoxVisible) View.VISIBLE else View.GONE
            check.isChecked = contact.isSelected

            check.setOnClickListener {
                toggleSelection(position)
            }
        }
    }

    fun updateData(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        val contact = contacts[position]
        contact.isSelected = !contact.isSelected
        notifyItemChanged(position)
    }

    fun clearSelection() {
        contacts.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    class ContactsViewHolder(
        val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onContactEdit(contact: Contact) {
        notifyDataSetChanged()
    }

    override fun onClick(view: View) {
        val contact = view.tag as Contact

        actionListener.onContactEdit(contact)
    }
}