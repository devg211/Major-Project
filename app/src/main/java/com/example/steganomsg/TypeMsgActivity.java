package com.example.steganomsg;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.ayush.imagesteganographylibrary.Text.TextEncoding;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


public class TypeMsgActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener, TextEncodingCallback, TextDecodingCallback {
    public static TypeMsgActivity typeMsgActivity;
    public static boolean flag;
    public static int msgLen = 0;
    private static int index;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    //final private String FCM_API = "https://fcm.googleapis.com/v1/{parent=demofcm-311ab/*}";
    final private String serverKey = "key=" + "AAAAC5pLdw0:APA91bGJJW_hcx400dADQ-xHmAmYZHre4I60yYP_LTObSywis4U_ca5k7BwKSrv6yCiB67vbLkI7SpMLW2nlF25R_Ig0VLESsri4Vz5SrwfCAkcVwjR0BVotzeuhl_LVno1no33tGCSm";
    final private String contentType = "application/json";
    public String isEncypted = "0", tokon;
    RelativeLayout relativeLayout;
    View actionBarView;
    RecyclerView recyclerView;
    TextView tv_title, home;
    ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
    String privateKey, receiver, sender, date_time;
    UserSessionManager userSessionManager;
    LinearLayoutManager mLinearLayoutManager;
    EditText text;
    ImageView send;
    Bundle ab;
    LinearLayout lvSwitch;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date, TOKEN;
    String decryptmsg;
    boolean doubleBackToExitPressedOnce = false;
    Switch actionbar_view_switch;
    private MsgAdapter msgAdapter;
    //    private String image = "null";
    private Uri ImageUri;
    ImageView showImage;
    Bitmap ImageBitmap;
    ImageSteganography imageSteganography;
    private ImageView mPhotoAdd;
    TextEncoding textEncoding;
    TextDecoding textDecoding;
    private int SELECT_PICTURE = 100;

    String filePath;
    private String ImageUrl;
    private ProgressDialog save;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        typeMsgActivity = this;
        msgLen = 0;
        ab = getIntent().getExtras();
        sender = ab.getString("sender");
        //tokon = ab.getString("token");
        //isEncypted = ab.getString("isEncypted");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                TOKEN = instanceIdResult.getToken();

                Log.d("DEVICE TOKEN:", TOKEN);
            }
        });

        setActionBarView();

        save = new ProgressDialog(this);
        checkAndRequestPermissions();


        //date_time="2018-02-06 00:00:00.000000";

        //Toast.makeText(typeMsgActivity, ""+isEncypted, Toast.LENGTH_SHORT).show();

        try {
            Cipher c = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        findViewById();
        userSessionManager = new UserSessionManager(this);
        receiver = userSessionManager.getUserDetails().get("mob");
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        getMesgFromServer();
        msgAdapter = new MsgAdapter(TypeMsgActivity.this, responseMessages);
        recyclerView.setAdapter(msgAdapter);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMesgFromServer();
                    }
                });

            }
        }, 5000, 4000);


        System.out.println("tokon  ::" + tokon);


    }


    private void getMesgFromServer() {

       /* final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();*/
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UserSessionManager.url + "Msg/getAll/receiver/" + receiver + "/sender/" + sender,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.e("responsenitin", response);
                            //progressDialog.dismiss();
                            try {

                                JSONObject obj = new JSONObject(response);
                                JSONArray jArray = obj.getJSONArray("msg");
                                //int len = jArray.length();
                                responseMessages.clear();
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);


                                    responseMessages.add(new ResponseMessage(
                                            "" + json_data.getString("sender"),
                                            "" + json_data.getString("receiver"),
                                            "" + json_data.getString("messages"),
                                            "" + json_data.getString("private_key"),
                                            "" + json_data.getString("id"),
                                            "" + json_data.getString("isEncypted"),
                                            "" + json_data.getString("image")
                                    ));

                                }

                                if (responseMessages.size() > msgLen) {
                                    msgLen = responseMessages.size();
                                    msgAdapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(responseMessages.size());
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("nitin3", e.getMessage());
                                //Toast.makeText(getApplicationContext(),"Data fail",Toast.LENGTH_SHORT).show();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error", error.toString());
                            // progressDialog.dismiss();

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

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViewById() {
        lvSwitch = (LinearLayout) findViewById(R.id.lvSwitch);
        mPhotoAdd = findViewById(R.id.add_image);
        mPhotoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePic2();
//                ImageChooser();
            }
        });
        showImage = findViewById(R.id.image_view);
        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogImg(TypeMsgActivity.this, 0, ImageUri.getPath());
            }
        });
        showImage.setVisibility(View.GONE);
        lvSwitch.setVisibility(View.VISIBLE);


        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        send = (ImageView) findViewById(R.id.iv_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date = simpledateformat.format(calander.getTime());

                System.out.println("asfsadf::::  " + Date);
                if (isEncypted.equalsIgnoreCase("1")) {
                    if (ImageBitmap != null) {
                        dialog(text.getText().toString().trim(), 3, 0);
                    } else {
                        dialog(text.getText().toString().trim(), 2, 0);
                    }
                } else {
                    try {
                        System.out.println("server hit 1");
                        SendMsgToServer(sender, receiver, "1", "" + Des._encrypt(text.getText().toString().trim(), "1"), Date, isEncypted, ImageBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        text = (EditText) findViewById(R.id.textMsg);
        tv_title = (TextView) actionBarView.findViewById(R.id.actionbar_view_title);
        actionbar_view_switch = (Switch) actionBarView.findViewById(R.id.actionbar_view_switch);
        home = (TextView) actionBarView.findViewById(R.id.actionbar_view_tv_left);
        home.setVisibility(View.VISIBLE);
        home.setText("Back");
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        if (isEncypted != null) {
            if (isEncypted.equalsIgnoreCase("1")) {
                actionbar_view_switch.setChecked(true);

            } else {
                actionbar_view_switch.setChecked(false);
            }
        }
        actionbar_view_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    isEncypted = "1";
                    //changeEncryptionMode(sender, receiver, "1");
                } else {
                    isEncypted = "0";
                    //changeEncryptionMode(sender, receiver, "0");
                }

            }
        });

        tv_title.setText("User Name");
    }

    /* public void setSwitch(Boolean b){
         actionbar_view_switch.setChecked(b);
         flag=true;
     }*/

    private void SendMsgToServer(final String sender, final String receiver, final String privateKey, final String msg, final String date_time, final String isEncypted1, final Bitmap image) {
        Log.e("nnnitin",sender+receiver+privateKey+msg+date_time+isEncypted1);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        try {
            VolleyMultipartRequest smr = new VolleyMultipartRequest(Request.Method.POST, UserSessionManager.url + "msg/post/", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    Log.d("Responsedee", String.valueOf(response));
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender", receiver);
                    params.put("receiver", sender);
                    params.put("private_key", privateKey);
                    params.put("messages", msg);
                    params.put("date_time", date_time);
                    params.put("active", "1");
                    params.put("isEncypted", "" + isEncypted1);
                    return params;
                }
            };
//            SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, UserSessionManager.url + "msg/post/",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.d("Responsedee", response);
//                            progressDialog.dismiss();
//                            if (response.toString().contains("200")) {
//                                try {
//                                    System.out.println("SAFASDF::::  " + response);
////                                JSONObject jObj = new JSONObject(response);
//                                    sendSMS(sender, privateKey, "1");
//                                    text.setText("");
//                                    showImage.setVisibility(View.GONE);
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            sendToken(receiver, msg);
//
//                                        }
//                                    });
//                                } catch (Exception e) {
//                                    // JSON error
//                                    e.printStackTrace();
//                                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//            System.out.println("Afasdf:::  " + filePath);
//           // smr.addFile("imagePhoto", filePath);
//            smr.addMultipartParam("image", "file",filePath);
//            smr.addStringParam("sender", receiver);
//            smr.addStringParam("receiver", sender);
//            smr.addStringParam("private_key", privateKey);
//            smr.addStringParam("messages", msg);
//            smr.addStringParam("date_time", date_time);
//            smr.addStringParam("active", "1");
//            smr.addStringParam("isEncypted", "" + isEncypted1);
//            RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//            mRequestQueue.add(smr);
//            mRequestQueue.start();
//        } catch (Exception e) {
//            progressDialog.dismiss();
//            e.printStackTrace();
//        }
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(smr);
        }
        catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void sendSMS(String phoneNumber, String message, String mid) {
        // Toast.makeText(getApplicationContext(), phoneNumber + message, Toast.LENGTH_LONG).show();
       /* PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, TypeMsgActivity.class), 0);*/
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        //Toast.makeText(this, "sms Send ", Toast.LENGTH_SHORT).show();
    }


    private void setActionBarView() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_view_custom);
        actionBarView = getSupportActionBar().getCustomView();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void dialog(final String msg, final int type, final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Private Key");
        TypeMsgActivity.index = index;

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                privateKey = input.getText().toString().trim();
                String decrypt, encrypt;
                switch (type) {
                    case 1:
                        dialog.dismiss();
                        try {

                            decrypt = Des._decrypt(msg, privateKey);
                            responseMessages.get(index).setMsg(decrypt);
                            msgAdapter.notifyDataSetChanged();
                            new CountDownTimer(8000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                                    //here you can have your logic to set text to edittext
                                }

                                public void onFinish() {
                                    responseMessages.get(index).setMsg(msg);
                                    msgAdapter.notifyDataSetChanged();
                                }

                            }.start();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Wrong Key", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 2:
                        dialog.dismiss();
                        try {
                            encrypt = Des._encrypt(msg, privateKey);
                            System.out.println("server hit ::::2" + privateKey);
                            SendMsgToServer(sender, receiver, privateKey, encrypt, Date, "1", ImageBitmap);
                            //Toast.makeText(getApplicationContext(),"hit",Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;


                    case 3:
                        dialog.dismiss();
//                        try {
//                            System.out.println("sfdsadf::::  private keyyysendddd    :" + privateKey+msg);
//                            imageSteganography = new ImageSteganography(msg, privateKey, ImageBitmap);
//                            textEncoding = new TextEncoding(TypeMsgActivity.this,
//                                    TypeMsgActivity.this);
//                            textEncoding.execute(imageSteganography);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        imageSteganography = new ImageSteganography(msg,
                                privateKey,
                                ImageBitmap);
                        //TextEncoding object Instantiation
                        textEncoding = new TextEncoding(TypeMsgActivity.this, TypeMsgActivity.this);
                        //Executing the encoding
                        textEncoding.execute(imageSteganography);
                        break;

                    case 4:
                        dialog.dismiss();
                        try {
                            ImageUrl = "https://techfizone.com/projects/projects21-22/secureMessenger/application/photos/" + msg;

                            new MyTask().execute();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Wrong Key", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.show();
    }


    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ImageBitmap = null;
            try {

                URL url = new URL(ImageUrl);

                ImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("fdsasfasd::::  " + e.toString());
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            System.out.println("sfdsadf::::  private keyyy    :" + privateKey + "   " + ImageBitmap);

            imageSteganography = new ImageSteganography(privateKey, ImageBitmap);
            textDecoding = new TextDecoding(TypeMsgActivity.this,
                    TypeMsgActivity.this);
            textDecoding.execute(imageSteganography);

            super.onPostExecute(aVoid);
        }
    }

    public void sendToken(String title, String body) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message", body);

//                    if (userToken.toString().isEmpty()) {
//                        MainToken = TOKEN;
//                    } else {
//                        MainToken = UserToken;
//                    }

            System.out.println("Map Token :: " + sender);

            notification.put("to", "/topics/" + sender);
//            notification.put("to", "egLdu-_bP88:APA91bHao-8nXht2HVwNbKekXdVJes8OWpaHZLlSbCJAA3BCUb8xGSBV8TKJ73yXTyn1NP0YKbUQqAQKVJWN7YXS");
            notification.put("data", notifcationBody);
            sendNotification(notification);
        } catch (JSONException e) {
            e.getStackTrace();
            Log.e("notify", "onCreate: " + e.getMessage());
        }

    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("nofity", "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error notify", "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void chooseProfilePic2() {

        try {
            ImagePicker.Companion.with(this)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(100)            //Final image size will be less than 1 MB(Optional)
                    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                ImageUri = data.getData();
                filePath = ImageUri.getPath();
                showImage.setVisibility(View.VISIBLE);
                showImage.setImageURI(ImageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ImageBitmap = null;
            try {
//                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), ImageUri);
//                ImageBitmap = ImageDecoder.decodeBitmap(source);
                ImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndRequestPermissions() {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }


    public void showDialogImg(final Context context, final int pos, String Image) throws NullPointerException {
        String front;
        try {
            if (Image.equalsIgnoreCase("null")) {
                front = "https://techfizone.com/projects/projects21-22/secureMessenger/application/photos/" + responseMessages.get(pos).getImage();
            } else {
                front = Image;
            }

            final Dialog deleteDialogView = new Dialog(context, R.style.Theme_AppCompat_Translucent);
            deleteDialogView.setContentView(R.layout.dialog_imgview);


            final PhotoView photoView = deleteDialogView.findViewById(R.id.imageView);
            Picasso.get().load(front).into(photoView);

            ImageView next = deleteDialogView.findViewById(R.id.next);
            final TextView textView = deleteDialogView.findViewById(R.id.text_fb);
            final ProgressBar progressBar = deleteDialogView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            ImageView close = deleteDialogView.findViewById(R.id.close);


            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDialogView.dismiss();

                }
            });

            deleteDialogView.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStartTextEncoding() {

    }


    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        System.out.println("afasfjkahf Result ::::   " + result.isDecoded() + "   " + result.isEncoded() + "   " + result.getSecret_key()
                + result.getMessage());
        if (result != null && result.isEncoded()) {
            try {
                ImageBitmap = result.getEncoded_image();
                Date = simpledateformat.format(calander.getTime());
                save.setCancelable(false);
                save.show();
                saveToInternalStorage(ImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result != null) {
            /* If result.isDecoded() is false, it means no Message was found in 					the image. */
            if (!result.isDecoded()) {
                responseMessages.get(index).setMsg("No Message Found");
                msgAdapter.notifyDataSetChanged();
                new CountDownTimer(8000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        responseMessages.get(index).setMsg("null");
                        msgAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(index);

                    }

                }.start();

            } else {
                /* If result.isSecretKeyWrong() is true, it means that secret key provided 				is wrong. */
                if (!result.isSecretKeyWrong()) {
                    responseMessages.get(index).setMsg("" + result.getMessage());
                    msgAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(index);

                } else {
                    responseMessages.get(index).setMsg("Wrong secret key");
                    msgAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(index);

                }
            }
        } else {
            //If result is null it means that bitmap is null
            System.out.println("asfasfjkasd  ::: Bad Image format ");
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        OutputStream fOut;
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "IMG_" + timeStamp + ".PNG"); // the File to save ,
        try {
            System.out.println("server hit :3");
            fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file
            fOut.flush(); // Not really required
            fOut.close();
            filePath = null;
            filePath = file.getPath();
            System.out.println("hjghjgjg :::: " + filePath);
            save.dismiss();
            SendMsgToServer(sender, receiver, privateKey, "null", Date, "1", ImageBitmap);
        } catch (FileNotFoundException e) {
            System.out.println("AFasfdfsdaf:::: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("sfdsffd:::: " + e.getMessage());

            e.printStackTrace();
        }
    }
}


