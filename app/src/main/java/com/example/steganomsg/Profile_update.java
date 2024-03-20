package com.example.steganomsg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
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


public class Profile_update extends AppCompatActivity {

    View actionBarView;
    EditText fname, lname, email, mobile, password;
    TextView tv_title, save, login;
    ImageView pic;
    RadioButton male, female;
    String img="null", user, id;
    String pass;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        UserSessionManager userSessionManager = new UserSessionManager(this);
        user = userSessionManager.getUserDetails().get("mob");
        setActionBarView();
        findViewById();
        GetUserFromServer();
    }

    private void clear() {
        fname.setText("");
        lname.setText("");
        email.setText("");
        mobile.setText("");
        //password.setText("");

    }

    boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(fname)) ret = false;
        if (!Validation.hasText(lname)) ret = false;
        if (!Validation.isPhoneNumber(mobile, true)) ret = false;
        if (!Validation.isEmailAddress(email, true)) ret = false;

        return ret;
    }

    private void findViewById() {
        fname = (EditText) findViewById(R.id.et_firstName);
        lname = (EditText) findViewById(R.id.et_lastName);
        email = (EditText) findViewById(R.id.et_email);
        mobile = (EditText) findViewById(R.id.et_mobile);
        mobile.setEnabled(false);
        password = (EditText) findViewById(R.id.et_pass);
        password.setVisibility(View.GONE);
        //password.setEnabled(true);
        save = (TextView) findViewById(R.id.tv_save);
        save.setText("Update");
        male = (RadioButton) findViewById(R.id.rb_male);
        female = (RadioButton) findViewById(R.id.rb_female);
        pic = (ImageView) findViewById(R.id.iv_user);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePic();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gender;
                if (male.isChecked()) {
                    gender = "male";
                } else {
                    gender = "female";
                }
                if (checkValidation()) {
                    UpdateDataToServer(fname.getText().toString().trim(), lname.getText().toString().trim(), email.getText().toString().trim()
                            , mobile.getText().toString().trim(), gender, pass, img, id);
                }

            }


        });
        tv_title = (TextView) actionBarView.findViewById(R.id.actionbar_view_title);
        tv_title.setText("Profile");
        login = (TextView) actionBarView.findViewById(R.id.actionbar_view_tv_left);
        login.setVisibility(View.VISIBLE);
        login.setText("Back");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

    private void GetUserFromServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url + "Login/get/mobile/" + user,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response", response);
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

                                   if(json_data.getString("profile_pic").isEmpty()||json_data.getString("profile_pic").equalsIgnoreCase("null")){

                                       pic.setImageDrawable(getResources().getDrawable(R.drawable.camera_icon));


                                   }else {
                                       try {
                                           pic.setImageBitmap(StringToBitMap(json_data.getString("profile_pic")));
                                       }catch (Exception e){

                                       }
                                   }
                                    fname.setText(json_data.getString("first_name"));
                                    lname.setText(json_data.getString("last_name"));
                                    email.setText(json_data.getString("email"));
                                    mobile.setText(json_data.getString("mobile"));
                                    pass = json_data.getString("password");
                                    img = json_data.getString("profile_pic");

                                    id = json_data.getString("id");
                                    if (json_data.getString("gender").equalsIgnoreCase("male")) {
                                        male.setChecked(true);
                                    } else if (json_data.getString("gender").equalsIgnoreCase("female")) {
                                        female.setChecked(true);
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Data fail", Toast.LENGTH_SHORT).show();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error", error.toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void UpdateDataToServer(final String fname, final String lname, final String email, final String mobile, final String gender, final String password, final String base64img, final String id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url + "Registration/post/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("response", response);
                            progressDialog.dismiss();
                            try {


                                UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());

                                userSessionManager.createUserLoginSession(""+fname,
                                        ""+lname,
                                        ""+mobile,
                                        ""+email,
                                        ""+password,
                                        ""+img,
                                        ""+gender
                                );
                                clear();
                                Toast.makeText(getApplicationContext(), "Data update success", Toast.LENGTH_SHORT).show();
                                onBackPressed();
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
                            Log.e("error", error.toString());
                            progressDialog.dismiss();

                            //username.setError(getString(R.string.error_incorrect_username));

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", id);
                    params.put("first_name", fname);
                    params.put("last_name", lname);
                    params.put("mobile", mobile);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("gender", gender);
                    params.put("profile_pic", base64img);
                    params.put("active", "1");

                    System.out.println("sdfsaf map data ::: "+params.toString());
                    return params;
                }


            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch (Exception e) {
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
        final CharSequence[] items = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = UserSessionManager.checkPermissions(getApplicationContext());

                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    if (result)
                        galleryIntent();

                }
            }
        });
        builder.show();
    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pic.setImageBitmap(bm);
        img =getStringImage(bm);
        System.out.println("jhkfdhshjfk::: "+img);

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
        Log.v("heloo", "base64 >>>" + img + " <<<<<");
    }


    public static Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public String getStringImage(Bitmap bmp) {
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
        */
        }

    }


}
