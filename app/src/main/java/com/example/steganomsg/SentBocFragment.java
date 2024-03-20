package com.example.steganomsg;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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



public class SentBocFragment extends Fragment {

    View rootView;
    ListView listView;
    ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
    private  UserMsgAdapter userMsgAdapter;
    String mobile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ListView)rootView.findViewById(R.id.lv_main);


        UserSessionManager userSessionManager =new UserSessionManager(getActivity());
        mobile = userSessionManager.getUserDetails().get("mob");
        userMsgAdapter = new UserMsgAdapter(getActivity(),responseMessages);
        listView.setAdapter(userMsgAdapter);
        getMesgFromServer();
        return rootView;
    }
    private void getMesgFromServer() {
        /*final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing...");
        progressDialog.show();*/
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"msg/get/sender/"+mobile,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            //progressDialog.dismiss();
                            try {

                                JSONObject obj = new JSONObject(response);
                                JSONArray jArray = obj.getJSONArray("msg");
                                //int len = jArray.length();
                                responseMessages.clear();
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);

//                                    responseMessages.add(new ResponseMessage(""+json_data.getString("sender"),
//                                            ""+json_data.getString("receiver"),""+json_data.getString("messages"),
//                                            ""+json_data.getString("private_key"),""+json_data.getString("first_name"),
//                                            ""+json_data.getString("last_name"),""+json_data.getString("mobile"),
//                                            ""+json_data.getString("email"),""+json_data.getString("gender"),
//                                            ""+json_data.getString("profile_pic")));

                                }
                                userMsgAdapter = new UserMsgAdapter(getActivity(),responseMessages);
                                listView.setAdapter(userMsgAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        Intent i = new Intent(getActivity(),TypeMsgActivity.class);
                                        i.putExtra("sender",responseMessages.get(position).getReceiver());
                                        startActivity(i);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(),"Data fail",Toast.LENGTH_SHORT).show();

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

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
