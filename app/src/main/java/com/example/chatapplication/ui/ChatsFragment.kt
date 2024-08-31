package com.example.chatapplication.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.MessageActivity
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatsFragment : Fragment() {

    private val REQUEST_CONTACTS_PERMISSION = 1
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        contactsAdapter = ContactsAdapter(emptyList()) { contactNumber ->
            // Start a chat with the selected contact number
            val intent = Intent(activity, MessageActivity::class.java).apply {
                putExtra("CONTACT_NUMBER", contactNumber)
            }
            startActivity(intent)
        }
        recyclerView.adapter = contactsAdapter

        requestContactsPermission()
        return view
    }

    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACTS_PERMISSION
            )
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CONTACTS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts()
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(context, "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadContacts() {
        val contacts = mutableListOf<String>()
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        cursor?.let {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id),
                        null
                    )

                    pCursor?.let {
                        while (pCursor.moveToNext()) {
                            val phoneNumber = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contacts.add(phoneNumber)
                        }
                        pCursor.close()
                    }
                }
            }
            cursor.close()
        }

        checkRegisteredContacts(contacts)
    }

    private fun checkRegisteredContacts(contacts: List<String>) {
        db.collection("users")
            .whereIn("phoneNumber", contacts)
            .get()
            .addOnSuccessListener { documents ->
                val registeredContacts = documents.map { "${it.getString("name")}: ${it.getString("phoneNumber")}" }
                contactsAdapter = ContactsAdapter(registeredContacts) { contactNumber ->
                    val intent = Intent(activity, MessageActivity::class.java).apply {
                        putExtra("CONTACT_NUMBER", contactNumber)
                    }
                    startActivity(intent)
                }
                recyclerView.adapter = contactsAdapter
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch contacts", Toast.LENGTH_SHORT).show()
            }
    }
}
