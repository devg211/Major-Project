package com.example.steganomsg;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class UserFragment extends Fragment {
    View rootView;
    ListView listView;
    ArrayList<ResponseUserContact> responseUserContacts =new ArrayList<>();
    private UserListAdapter userListAdapter;
    String user;
    final int PERMISSION_CONTACT = 1;
    String phoneNumber;
    String name;
    TextView tvNoData;
    private ArrayList<String> listOfUnregisteredContacts = new ArrayList<String>();
    private HashMap<String, String> unregisteredContactHashMap = new HashMap<String, String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        UserSessionManager userSessionManager = new UserSessionManager(getActivity());
        user = userSessionManager.getUserDetails().get("mob");
        listView = (ListView)rootView.findViewById(R.id.lv_main);
        tvNoData = (TextView) rootView.findViewById(R.id.tvNoData);
        responseUserContacts.clear();




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(), TypeMsgActivity.class));
            }
        });

        openContact();

        getUsersListFromServer();
        return rootView;
    }


    private void getUsersListFromServer() {
        //System.out.println(">>>>>>>>>>>>> >  :: "+listOfUnregisteredContacts.toString() );

       /* for (int i=0;i<listOfUnregisteredContacts.size();i++){
            System.out.println(">>>>>>>>>>>>> >  :: "+listOfUnregisteredContacts.get(i) );
        }*/

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"Login/get/active/1",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            progressDialog.dismiss();
                            try {

                                JSONObject obj = new JSONObject(response);
                                JSONArray jArray = obj.getJSONArray("login");
                                //int len = jArray.length();
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    Log.i("log_tag",
                                            " user_id : " + json_data.getString("email")
                                    );

                                    if (listOfUnregisteredContacts.contains("+91"+json_data.getString("mobile"))) {

                                        responseUserContacts.add(new ResponseUserContact(json_data.getString("first_name"),
                                                json_data.getString("last_name"), json_data.getString("email")
                                                , json_data.getString("mobile"),
                                                json_data.getString("gender"),
                                                ""+json_data.getString("profile_pic"),
                                                ""+json_data.getString("token")));
                                    }

                                }

                                for (int j=0 ;j<responseUserContacts.size();j++){
                                    if (responseUserContacts.get(j).getMobile().equalsIgnoreCase(user)){
                                        responseUserContacts.remove(j);
                                    }
                                }

                                if (responseUserContacts.size()==0){
                                    tvNoData.setVisibility(View.VISIBLE);
                                }else {
                                    tvNoData.setVisibility(View.GONE);
                                }
                                userListAdapter = new UserListAdapter(getActivity(),responseUserContacts);

                                listView.setAdapter(userListAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        Intent i = new Intent(getActivity(),TypeMsgActivity.class);
                                        i.putExtra("sender",responseUserContacts.get(position).getMobile());
                                        i.putExtra("token",responseUserContacts.get(position).getToken());
                                        startActivity(i);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvNoData.setVisibility(View.VISIBLE);
                                Log.e("nitin",e.getMessage());
                                Toast.makeText(getActivity(),"Data Fail",Toast.LENGTH_SHORT).show();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                            //progressDialog.dismiss();

                            //username.setError(getString(R.string.error_incorrect_username));

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    /*params.put("username", user);
                    params.put("password", pass);*/
                    //params.put("app1", "2");
                    return params;
                }


            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(20),
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    void askPermission_contacts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.READ_CONTACTS))) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSION_CONTACT);

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSION_CONTACT);


                }
            }
        }


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {


            case PERMISSION_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("CONTACT PERMISSION GRANTED");

//                    Intent in = new Intent();
//                    in.setClass(getApplicationContext(), ContactRequestService.class);
//                    startService(in);

                    try {

                        contactChooser();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }


                } else {


                    for (int i = 0, len = permissions.length; i < len; i++) {
                        String permission = permissions[i];
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(permission);

                            if (!showRationale) {

                                showpopupPermission();

                            } else if (Manifest.permission.READ_CONTACTS.equals(permission)) {
                                askPermission_contacts();

                            }
                        }


                    }

                }
                break;



        }
    }


    public void showpopupPermission() {

        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
        alertbox.setMessage("Here contact permission is required.");
        alertbox.setCancelable(false);
        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getActivity().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        alertbox.show();

    }
    public void openContact() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                askPermission_contacts();


            } else {

                contactChooser();

            }

        } else {

            contactChooser();

        }

    }

    public void contactChooser() {

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {

            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phoneNumber = phoneNumber.replace("(", "");
            phoneNumber = phoneNumber.replace(")", "");
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            int len = phoneNumber.length();
            if (len==10){
                phoneNumber ="+91"+phoneNumber;
                Log.e("PhoneNumber",phoneNumber);
            }else if (phoneNumber.substring(0, 1).equalsIgnoreCase("0")){
                phoneNumber="+91"+phoneNumber.substring(1,len);
            }

            listOfUnregisteredContacts.add(phoneNumber.toString());


            /*if (name != null && !name.isEmpty()) {

                unregisteredContactHashMap.put(phoneNumber, name);

            } else {
                unregisteredContactHashMap.put(phoneNumber, phoneNumber);
            }*/


           /* getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                }
            });*/

        }

    }


}
