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
import android.widget.TextView;


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


public class IndoxFragment extends Fragment {

    View rootView;
    ListView listView;
    ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
    private UserMsgAdapter userMsgAdapter;
    String mobile;
    String dataCheck;
    TextView tvNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ListView)rootView.findViewById(R.id.lv_main);
        tvNoData = (TextView) rootView.findViewById(R.id.tvNoData);

        UserSessionManager userSessionManager =new UserSessionManager(getActivity());
        mobile = userSessionManager.getUserDetails().get("mob");

        getMesgFromServer();
        return rootView;
    }

    private void getMesgFromServer() {
        /*final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing...");
        progressDialog.show();*/
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"Msg/test_msg/"+mobile,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            Log.e("response",response);
                            //progressDialog.dismiss();
                            try {

                                JSONObject obj = new JSONObject(response);
                                JSONArray jArray = obj.getJSONArray("msg");
                                //int len = jArray.length();
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);


                                        responseMessages.add(new ResponseMessage("" + json_data.getString("last_msg"),
                                                "" + json_data.getString("name"),
                                                "" + json_data.getString("photo"),
                                                "" + json_data.getString("number"),
                                                "" + json_data.getString("isEncypted")
                                        ));


                                }

                                if (responseMessages.size()==0){
                                    tvNoData.setVisibility(View.VISIBLE);
                                }else {
                                    tvNoData.setVisibility(View.GONE);
                                }
                                userMsgAdapter = new UserMsgAdapter(getActivity(),responseMessages);
                                listView.setAdapter(userMsgAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        Intent i = new Intent(getActivity(),TypeMsgActivity.class);
                                       // Toast.makeText(getActivity(), ""+, Toast.LENGTH_SHORT).show();
                                        System.out.println("????????????  isEncypted :: "+responseMessages.get(position).getIsEncypted());
                                        i.putExtra("sender",responseMessages.get(position).getNumber());
                                        i.putExtra("isEncypted",responseMessages.get(position).getIsEncypted());
                                        i.putExtra("token","egLdu-_bP88:APA91bHao-8nXht2HVwNbKekXdVJes8OWpaHZLlSbCJAA3BCUb8xGSBV8TKJ73yXTyn1NP0YKbUQqAQKVJWN7YXS");
                                        startActivity(i);
                                    }

                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvNoData.setVisibility(View.VISIBLE);
                                Log.e("nitin2",e.getMessage());
                               // Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                               // getMesSendgFromServer();


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
