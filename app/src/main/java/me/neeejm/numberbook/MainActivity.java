package me.neeejm.numberbook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.neeejm.numberbook.adapters.ContactAdapter;
import me.neeejm.numberbook.beans.Contact;
import me.neeejm.numberbook.services.ContactService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CONTACT = 1;
    private Button call, sms, contacts, search;
    private EditText phoneNumber;
    private CountryCodePicker ccp;
    private Gson gson = new Gson();
    private ConstraintLayout layout;
    private ContactService contactService;
    private String URL = "http://192.168.1.4:8080/contacts/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.layout = findViewById(R.id.main_activity_layout);
        call = findViewById(R.id.call);
        sms = findViewById(R.id.sms);
        search = findViewById(R.id.search);
        phoneNumber = findViewById(R.id.label_phone_number);
        phoneNumber.setText("");
        ccp = findViewById(R.id.spinner);
        ccp.registerCarrierNumberEditText(phoneNumber);
        call.setOnClickListener(this);
        sms.setOnClickListener(this);
        search.setOnClickListener(this);
        contacts = findViewById(R.id.contacts);
        contacts.setOnClickListener(this);

        contactService = ContactService.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        if(v == call){
            if (ccp.isValidFullNumber()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber.getText()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "phone number not valid", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == sms){

            if (ccp.isValidFullNumber()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber.getText()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "phone number not valid", Toast.LENGTH_SHORT).show();
            }
        }

        if(v == contacts){
            if (requestPermission()) {

                new AddTask().execute(this);
            }
            //Toast.makeText(this, loadContact(this.getContentResolver())+"", Toast.LENGTH_SHORT).show();

        }

        if(v == search){
            if (phoneNumber.getText().toString().equals("") || phoneNumber == null) {
                Toast.makeText(this, "enter a value", Toast.LENGTH_SHORT).show();

            } else {

                StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, URL + "find?value=" + phoneNumber.getText(),
                        response -> {
                            Log.i("api", "find by value: " + response);
                            //Toast.makeText(this, gson.fromJson(String.valueOf(response), Contact.class)+"", Toast.LENGTH_SHORT).show();
                            LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View customView = layoutInflater.inflate(R.layout.contact_popup, null);
                            Contact contact = gson.fromJson(String.valueOf(response), Contact.class);
                            if (contact != null) {
                                TextView value = customView.findViewById(R.id.value);
                                value.setText(contact.getName() + " : " + contact.getPhoneNumber());
                                PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                popupWindow.setFocusable(true);
                                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
                                dimBehind(popupWindow);
                            } else {
                                Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        , error -> {
                    Log.e("api", String.valueOf(error));
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(jsonObjectRequest);
            }


        }

    }

    public List<Contact> loadContact (ContentResolver cr){
        List<Contact> contacts = new ArrayList<>();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {
            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new Contact(name, phoneNumber));
        }
        phones.close();
        return contacts;
    }

    private boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.READ_CONTACTS
            }, PERMISSION_REQUEST_CONTACT);

            return false;
        }
        return true;
    }

    class AddTask extends AsyncTask<Activity, Integer, Activity> {

        @Override
        protected void onPostExecute(Activity activity) {
            super.onPostExecute(activity);
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    response -> {
                        Log.i("api", "findAll: " + response);
                        List<Contact> contacts = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                contacts.add(gson.fromJson(response.get(i).toString(), Contact.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        intent.putExtra("contacts", (Serializable) contacts);
                        startActivity(intent);
                    }
                    , error -> {
                Log.e("api", String.valueOf(error));
            });
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(jsonObjectRequest);


        }

        @Override
        protected Activity doInBackground(Activity... activities) {
            contactService.createAll(loadContact(activities[0].getContentResolver()));
            return activities[0];
        }
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.8f;
        wm.updateViewLayout(container, p);
    }
}