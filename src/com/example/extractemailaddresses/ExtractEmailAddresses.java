
package com.example.extractemailaddresses;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ExtractEmailAddresses extends Activity {
    private static final String TAG = ExtractEmailAddresses.class
            .getSimpleName();
    private static final String EMAIL_ADDRESS_LIST = "emailAddressList";
    private static final String EMAIL_ADDRESS_LIST_SIZE = "emailAddressListSize";
    private static final String PREF_LIST = "sharedPrefEmailAddressList";
    private static final String TEST_EMAIL = "test.emailscoopy@gmail.com";
    private static final String TEST_PW = "Samsung135";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_email_addresses);

        ArrayList<String> addressList = new ArrayList<String>();
        SharedPreferences pref = getSharedPreferences(PREF_LIST, 0);

        if (pref.getString(EMAIL_ADDRESS_LIST, "DOES_NOT_EXIST")
                .equals("EXIST")) {
            addressList = loadAddressList();
            putOnDisplay(addressList);
        } else {
            new LoadEmailAddress().execute(this);

        }

    }

    public boolean saveAddressList(ArrayList<String> addressList) {
        SharedPreferences pref = getSharedPreferences(PREF_LIST, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(EMAIL_ADDRESS_LIST, "EXIST");
        int addressListSize = addressList.size();
        editor.putInt(EMAIL_ADDRESS_LIST_SIZE, addressListSize);
        for (int i = 0; i < addressListSize; i++) {
            editor.remove("Address_" + i);
            editor.putString("Address_" + i, addressList.get(i));
            Log.i(TAG, addressList.get(i));
        }
        return editor.commit();
    }

    public ArrayList<String> loadAddressList() {
        ArrayList<String> addressList = new ArrayList<String>();
        SharedPreferences pref = getSharedPreferences(PREF_LIST, 0);
        int addressListSize = pref.getInt(EMAIL_ADDRESS_LIST_SIZE, 0);
        for (int i = 0; i < addressListSize; i++) {
            addressList.add(pref.getString("Address_" + i, null));
            Log.i(TAG, addressList.get(i));
        }
        return addressList;
    }

    public void putOnDisplay(ArrayList<String> addressList) {
        Collections.sort(addressList);
        ListView listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.list_single_row, addressList);
        listView.setAdapter(adapter);
    }

    private class LoadEmailAddress extends
            AsyncTask<Context, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Context... params) {
            ArrayList<String> addressList = new ArrayList<String>();
            try {
                Extractor.run(addressList, TEST_EMAIL, TEST_PW);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            saveAddressList(addressList);
            return addressList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> addressList) {

            putOnDisplay(addressList);
            return;

        }
    }

}
