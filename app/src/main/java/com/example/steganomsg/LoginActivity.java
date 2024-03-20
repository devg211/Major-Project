package com.example.steganomsg;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity  {
    TextInputEditText username , password;
    TextView newAccount,forget;
    MaterialButton submit;
    String user,token,pass;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            Toast.makeText(this, "no network", Toast.LENGTH_LONG).show();

            connected = false;
        }

        userSessionManager = new UserSessionManager(LoginActivity.this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();

                Log.d("device token:", token);
            }
        });
        username = findViewById(R.id.login_etEmail);
        password = findViewById(R.id.login_etPassword);
        submit = findViewById(R.id.login_tvLogin);
        forget = (TextView)findViewById(R.id.login_tvForgotPassword);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgetPassword.class));
            }
        });
        newAccount = (TextView)findViewById(R.id.tv_new_account);
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Registration.class));

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user = username.getText().toString().trim();
                pass = password.getText().toString().trim();
                if (user.length() == 0){
                    Toast.makeText(getApplicationContext(),"Enter User Name",Toast.LENGTH_SHORT).show();

                }else if(pass.length() == 0){
                    Toast.makeText(getApplicationContext(),"Enter PassWord",Toast.LENGTH_SHORT).show();
                }else{
                    GetUserFromServer();
                    // startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }


            }
        });
        int myAPI = Build.VERSION.SDK_INT;
        if(myAPI>22)
        {

            List<String> permissionsNeeded = new ArrayList<String>();
            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
                permissionsNeeded.add("Send SMS");

            if (permissionsList.size() > 0) {

                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
        }

    }
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void GetUserFromServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"Login/get/mobile/"+user+"/password/"+pass+"/active/1",
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
                                   /* Log.i("log_tag",
                                            " user_id : " + json_data.getString("user_id")
                                    );*/
                                    Toast.makeText(getApplicationContext(),"Login Successfully",Toast.LENGTH_SHORT).show();


                                    updateToken();
                                    userSessionManager.createUserLoginSession(""+json_data.getString("first_name"),
                                    ""+json_data.getString("last_name"),json_data.getString("mobile"),
                                            ""+json_data.getString("email"),json_data.getString("password"),
                                            ""+json_data.getString("profile_pic"),
                                            ""+json_data.getString("gender")
                                            );
                                    finish();


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Data fail",Toast.LENGTH_SHORT).show();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                            progressDialog.dismiss();

                            //username.setError(getString(R.string.error_incorrect_username));

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
//                    params.put("id", user);
//                    params.put("token", pass);
                    //params.put("app1", "2");
                    return params;
                }


            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateToken() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"Login/postSavedToken",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            progressDialog.dismiss();
                            try {

                                JSONObject obj = new JSONObject(response);
                                JSONArray jArray = obj.getJSONArray("result");
                                //int len = jArray.length();
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                   /* Log.i("log_tag",
                                            " user_id : " + json_data.getString("user_id")
                                    );*/



                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Data fail",Toast.LENGTH_SHORT).show();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error",error.toString());
                            progressDialog.dismiss();

                            //username.setError(getString(R.string.error_incorrect_username));

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", user);
                    params.put("token", token);
                    //params.put("app1", "2");
                    return params;
                }


            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
