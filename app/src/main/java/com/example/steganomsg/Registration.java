package com.example.steganomsg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class Registration extends AppCompatActivity {
    View actionBarView;
    EditText fname,lname,email,mobile,password,otp;
    TextView tv_title,login;
    MaterialButton save;
    ImageView pic;
    RadioButton male,female;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String img="";
    String gender, otpno;
    String user;
    String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();

                Log.d("device token:", token);
            }
        });
        setActionBarView();
        findViewById();
    }
    private void clear(){
        fname.setText("");
        lname.setText("");
        email.setText("");
        mobile.setText("");
        password.setText("");

    }

    private void findViewById() {
        fname = (EditText) findViewById(R.id.et_firstName);
        lname = (EditText) findViewById(R.id.et_lastName);
        email = (EditText) findViewById(R.id.et_email);
        mobile = (EditText) findViewById(R.id.et_mobile);
        password = (EditText) findViewById(R.id.et_pass);
        save = findViewById(R.id.tv_save);
        male = (RadioButton)findViewById(R.id.rb_male) ;
        female = (RadioButton)findViewById(R.id.rb_female);
        pic = (ImageView)findViewById(R.id.iv_user);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePic();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkValidation()) {
                    user = mobile.getText().toString().trim();
                    GetUserFromServer(user);
                }

            }


        });
        tv_title = (TextView)actionBarView.findViewById(R.id.actionbar_view_title);
        tv_title.setText("Registration");
        login = (TextView)actionBarView.findViewById(R.id.actionbar_view_tv_right);
        login.setText("Login");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

    }
    private void GetUserFromServer(final String user ) {
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
                                    Toast.makeText(getApplicationContext(),"Mobile No. Already exist ",Toast.LENGTH_SHORT).show();



                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (male.isChecked()){
                                    gender = "male";
                                }else {
                                    gender = "femaile";
                                }
                                SaveDataToServer(fname.getText().toString().trim(),lname.getText().toString().trim(),email.getText().toString().trim()
                                        ,mobile.getText().toString().trim(),gender,password.getText().toString().trim(),img,token);



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


    boolean checkValidation(){
        boolean ret = true;
        if(!Validation.hasText(fname))ret=false;
        if(!Validation.hasText(lname))ret=false;
        if(!Validation.hasText(password))ret=false;
        if(!Validation.isPhoneNumber(mobile,true))ret=false;
        if(!Validation.isEmailAddress(email,true))ret=false;

        return ret;
    }

    private void SaveDataToServer(final String fname , final String lname, final String email, final String mobile, final String gender , final String password , final String base64img, final String token) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url+"Login/post2/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response",response);
                            progressDialog.dismiss();
                            try {

                                clear();
                                Intent i = new Intent(getApplicationContext(),VerifyOtp.class);
                                i.putExtra("user",mobile);
                                startActivity(i);
                                finish();
                                JSONObject obj = new JSONObject(response);
                                JSONArray jArray = obj.getJSONArray("result");
                                for (int j = 0; j < jArray.length(); j++) {
                                    JSONObject json_data = jArray.getJSONObject(j);
                                    Log.i("log_tag",
                                            " user_id : " + json_data.getString("0")
                                    );

                                    otpno = json_data.getString("0");
                                    Toast.makeText(getApplicationContext(),""+otpno,Toast.LENGTH_SHORT).show();


                                }

                                //Toast.makeText(getApplicationContext(),"Data Insert success",Toast.LENGTH_SHORT).show();





                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext()," fail",Toast.LENGTH_SHORT).show();

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
                    params.put("first_name", fname);
                    params.put("last_name", lname);
                    params.put("password", password);
                    params.put("mobile", mobile);
                    params.put("email", email);
                    params.put("gender", gender);
                    params.put("profile_pic", base64img);
                    params.put("active", "0");
                    params.put("token", token);
                    System.out.println("MAP DATA :: "+params.toString());
                    return params;
                }


            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void setActionBarView() {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_view_custom);
        actionBarView = getSupportActionBar().getCustomView();
    }


    public void chooseProfilePic() // replcae method name with chooseProfilePic

    {
        final CharSequence[] items = { "Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result= UserSessionManager.checkPermissions(getApplicationContext());

                if (items[item].equals("Camera")) {
                    userChoosenTask="Camera";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Gallery")) {
                    userChoosenTask="Gallery";
                    if(result)
                        galleryIntent();

                }
            }
        });
        builder.show();
    }


    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }


    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pic.setImageBitmap(bm);
        img = getStringImage(bm);

    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pic.setImageBitmap(thumbnail);
        img = getStringImage(thumbnail);
        Log.v("heloo","base64 >>>"+img+" <<<<<");
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);


       /*// if (requestCode == 1 && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
			final InputStream imageStream = getContentResolver().openInputStream(imageUri);
			final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                String encodedImage = encodeImage(selectedImage);
        */ }

    }





   /* private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }*/


}
