package me.neeejm.numberbook.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.neeejm.numberbook.R;
import me.neeejm.numberbook.beans.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final List<Contact> contacts;
    private Context context;
    private final LayoutInflater inflater;

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.contacts = contacts;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }



    @NonNull
    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = inflater.inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(contactView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.name.setText(contact.getName());
        holder.phoneNumber.setText(contact.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name, phoneNumber;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.label_name);
            phoneNumber = itemView.findViewById(R.id.label_phone_number);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
