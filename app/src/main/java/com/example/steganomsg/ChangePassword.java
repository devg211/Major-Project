package com.example.steganomsg;

import android.app.ActionBar;
import android.app.ProgressDialog;
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
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChangePassword extends AppCompatActivity {
    TextView old,newPass,tv_title,back;
    String oldPass,newPassword , user;
    MaterialButton submit;
    View actionBarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        setActionBarView();
        UserSessionManager userSessionManager =new UserSessionManager(this);
        user = userSessionManager.getUserDetails().get("mob");
        old = (TextView) findViewById(R.id.tv_oldPass);
        newPass = (TextView) findViewById(R.id.tv_newPass);
        submit =  findViewById(R.id.tv_changePass);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             oldPass = old.getText().toString();
             newPassword =newPass.getText().toString();
                GetUserFromServer();

            }
        });

        tv_title = (TextView)actionBarView.findViewById(R.id.actionbar_view_title);
        tv_title.setText("Profile");
        back = (TextView)actionBarView.findViewById(R.id.actionbar_view_tv_left);
        back.setVisibility(View.VISIBLE);
        back.setText("Back");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void setActionBarView() {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_view_custom);
        actionBarView = getSupportActionBar().getCustomView();
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

                                   if (oldPass.equalsIgnoreCase(json_data.getString("password"))){
                                       UpdateDataToServer(user,newPassword);
                                   }else{
                                       Toast.makeText(getApplicationContext(),"Old Password  Is Wrong ",Toast.LENGTH_SHORT).show();
                                   }




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

    private void UpdateDataToServer(final String mobile,final String password ) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"Registration/password/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            progressDialog.dismiss();
                            try {

                                old.setText("");
                                newPass.setText("");
                                Toast.makeText(getApplicationContext(),"password change success",Toast.LENGTH_SHORT).show();
                                finish();
                                JSONObject obj = new JSONObject(response);
                                // JSONArray jArray = obj.getJSONArray("login");
                                //int len = jArray.length();



                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(),"Data fail", Toast.LENGTH_SHORT).show();

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
                    params.put("mobile", mobile);
                    params.put("password", password);
                    return params;
                }


            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}

