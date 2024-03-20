package com.example.steganomsg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ForgetPassword extends AppCompatActivity{
    EditText number;
    MaterialButton bt;
    String user;
    LinearLayout getpass;
    String getway;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        getpass = (LinearLayout)findViewById(R.id.getpass);
        number = (EditText)findViewById(R.id.tv_getPassword);
        bt = findViewById(R.id.bt);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = number.getText().toString().trim();
                if(user.length()==10){
                    GetUserFromServer();
                }else{
                    Toast.makeText(getApplicationContext(),"Number incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void GetUserFromServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"Login/get/mobile/"+user,
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
                                   String senderId ="TECHFI";
                                   String serverUrl = "msg.msgclub.net";
                                   String authKey = "c35b6d3e2bb6769b86536f1ac249a69";
                                   String  routeId="1";

                                   String message="Your Password is "+json_data.getString("password") +"";

                                    String getData = "mobileNos="+json_data.getString("mobile")+"&message="+ URLEncoder.encode(message, "utf-8")+"&senderId="+senderId+"&routeId="+routeId;

                                    //API URL
                                    getway="http://"+serverUrl+"/rest/services/sendSMS/sendGroupSms?AUTH_KEY="+authKey+"&"+getData;
                                    sendUserpassword();




                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Wrong fail",Toast.LENGTH_SHORT).show();

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
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
                    /*params.put("username", user);
                    params.put("password", pass);*/
                    //params.put("app1", "2");
                    return params;
                }


            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendUserpassword() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, getway,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            progressDialog.dismiss();
                            try {


                                Toast.makeText(getApplicationContext(),"Your Password sent in your mobile No.",Toast.LENGTH_LONG).show();

                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                finish();
                                JSONObject obj = new JSONObject(response);
                                //JSONArray jArray = obj.getJSONArray("login");
                                //int len = jArray.length();


                                }
                             catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext()," fail",Toast.LENGTH_SHORT).show();

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
                    /*params.put("username", user);
                    params.put("password", pass);*/
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
