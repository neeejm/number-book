package me.neeejm.numberbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import me.neeejm.numberbook.adapters.ContactAdapter;
import me.neeejm.numberbook.beans.Contact;
import me.neeejm.numberbook.services.ContactService;

public class ContactsActivity extends AppCompatActivity {
    private ContactAdapter contactAdapter;
    private ContactService contactService;
    private ConstraintLayout layout;
    private RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactService = ContactService.getInstance(this);

        this.rvContacts = findViewById(R.id.contact_rv);
        this.layout = findViewById(R.id.contact_activity_layout);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        List<Contact> contacts = (List<Contact>) this.getIntent().getSerializableExtra("contacts");
        this.contactAdapter = new ContactAdapter(this, contacts);
        rvContacts.setAdapter(contactAdapter);
    }
}