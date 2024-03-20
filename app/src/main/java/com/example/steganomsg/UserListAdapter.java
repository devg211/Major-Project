package com.example.steganomsg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class UserListAdapter extends ArrayAdapter<ResponseUserContact> {
    Context context;



    private static class ViewHolder {
        TextView usertName,userMobile;
        ImageView userPic;
        LinearLayout llTotal;

    }

    public UserListAdapter(Context context, ArrayList<ResponseUserContact> responseUserContacts) {
        super(context, R.layout.item_user, responseUserContacts);
        this.context = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)  {
        ViewHolder viewHolder; // view lookup cache stored in tag

        final ResponseUserContact responseUserContact = getItem(position);

        viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            viewHolder.usertName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.userMobile = (TextView) convertView.findViewById(R.id.user_mobile);
            viewHolder.userPic = (ImageView) convertView.findViewById(R.id.iv_user);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.usertName.setText(""+responseUserContact.getFname()+" "+responseUserContact.getLname() );
        viewHolder.userMobile.setText(""+responseUserContact.getMobile() + " " );
        setImage(viewHolder.userPic,responseUserContact.getUserPic());

        return convertView;
    }

    public static void  setImage(ImageView iv,String bm){
       if(!bm.equalsIgnoreCase("")) {
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           byte[] imageBytes = baos.toByteArray();

           //decode base64 string to image
           imageBytes = Base64.decode(bm, Base64.DEFAULT);
           Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
           iv.setImageBitmap(decodedImage);
       }

    }



}
