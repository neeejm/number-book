package me.neeejm.numberbook.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.content.AsyncTaskLoader;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.neeejm.numberbook.beans.Contact;
import me.neeejm.numberbook.dao.IDao;

public class ContactService implements IDao<Contact> {
    private final List<Contact> contacts;
    private final Context context;
    private static ContactService instance;
    private final Gson gson;

    private final String URL = "http://192.168.1.4:8080/contacts/";

    private ContactService(Context context) {
        this.context = context;
        this.contacts = new ArrayList<>();
        this.gson = new Gson();
    }

    public synchronized static ContactService getInstance(Context context) {
        if(instance == null)
            instance = new ContactService(context);
        return instance;
    }

    @Override
    public boolean create(Contact o) {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("name", o.getName());
            object.put("phoneNumber", o.getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new MyJsonRequest(Request.Method.POST, URL, object,
                response -> {
                    Log.d("api", "create: contact");
                    //Toast.makeText(context, "Contact ajouté", Toast.LENGTH_LONG).show();
                    contacts.add(o);
                }
                , error -> {
            Log.e("api", String.valueOf(error));
            //Toast.makeText(context, "Contact n'est pas ajouté", Toast.LENGTH_LONG).show();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);

        return true;
    }

    @Override
    public boolean update(Contact o) {
        for (Contact c:
             this.findAll()) {
            if (c.getId() == o.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Contact o) {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.DELETE, URL + o.getId()+"", null,
                response -> {
                    //contacts.remove(o);
                    Log.i("api", "removed");
                }
                , error -> {
            Log.e("api", String.valueOf(error));
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
        return true;
    }

    @Override
    public Contact findById(int id) {
        for(Contact c : this.findAll() ){
            if(c.getId() == id)
                return c;
        }
        return null;
    }

    @Override
    public List<Contact> findAll() {
        return contacts;
    }

    public void createAll(List<Contact> contacts) {
        for (Contact c :
                contacts) {
            this.create(c);
        }

    }

    private static class MyJsonRequest extends JsonObjectRequest {
        public MyJsonRequest(String url, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        public MyJsonRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        // handle empty response object
        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                if (response.data.length == 0) {
                    byte[] responseData = "{}".getBytes("UTF8");
                    response = new NetworkResponse(response.statusCode, responseData,
                            response.notModified, response.networkTimeMs, response.allHeaders);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return super.parseNetworkResponse(response);
        }
    }

}
