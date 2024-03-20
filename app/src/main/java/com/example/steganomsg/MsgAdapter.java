package com.example.steganomsg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MsgAdapter extends
        RecyclerView.Adapter<MsgAdapter.MyViewHolder> {

    public Context mContext;
    private ArrayList<ResponseMessage> dataItem;
    String UrlImage ="http://192.168.0.114/secureMessenger/application/photos/";

    public MsgAdapter(Context mContext, ArrayList<ResponseMessage> dataItem) {
        this.dataItem = dataItem;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final ResponseMessage c = dataItem.get(position);

        UserSessionManager userSessionManager = new UserSessionManager(mContext);
        String user = userSessionManager.getUserDetails().get("mob");
        TypeMsgActivity.flag = false;
        if (c.getSender().equalsIgnoreCase("" + user))
        {

            if (c.getMsg().equalsIgnoreCase("null")) {
                holder.send.setVisibility(View.GONE);
            }
            holder.ll_send.setVisibility(View.VISIBLE);

            holder.ll_rec.setVisibility(View.GONE);

            if (c.getIsEncypted().equalsIgnoreCase("0")) {
//                if (c.getImage().length() > 10) {
//                    holder.send.setVisibility(View.GONE);
//                }
                //TypeMsgActivity.typeMsgActivity.setSwitch(false);
                try {
                    holder.send.setText("" + Des._decrypt(c.getMsg(), c.getKey()) + " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //TypeMsgActivity.typeMsgActivity.setSwitch(true);
                holder.send.setText("" + c.getMsg() + " ");
            }
            if (c.getImage().length() > 10) {
//                Bitmap bitmap = TypeMsgActivity.typeMsgActivity.getBitmapFromString(c.getImage());
                holder.sent_image.setVisibility(View.VISIBLE);
                try{
                    Picasso.get().load(UrlImage+c.getImage()).into(holder.sent_image);
                }catch (Exception e){
                    e.printStackTrace();
                }
//                holder.sent_image.setImageBitmap(bitmap);
            }
            holder.send_id.setText(c.getMsg_id());

        }
        else if (c.getReceiver().equalsIgnoreCase("" + user)) {

            holder.ll_rec.setVisibility(View.VISIBLE);
            if (c.getMsg().equalsIgnoreCase("null")) {
                holder.recive.setVisibility(View.GONE);
            }
            holder.ll_send.setVisibility(View.GONE);
            if (c.getIsEncypted().equalsIgnoreCase("0")) {
//                if (c.getImage().length() > 10) {
//                    holder.recive.setVisibility(View.GONE);
//                }
                //TypeMsgActivity.typeMsgActivity.setSwitch(false);
                try {
                    holder.recive.setText("" + Des._decrypt(c.getMsg(), c.getKey()) + " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //TypeMsgActivity.typeMsgActivity.setSwitch(true);
                holder.recive.setText("" + c.getMsg() + " ");
            }
            if (c.getImage().length() > 10) {
                holder.rec_image.setVisibility(View.VISIBLE);
                try{
                    Picasso.get().load(UrlImage+c.getImage()).into(holder.rec_image);
                }catch (Exception e){
                    e.printStackTrace();
                }
//                Bitmap bitmap = TypeMsgActivity.typeMsgActivity.getBitmapFromString(c.getImage());
//                holder.rec_image.setImageBitmap(bitmap);
            }
            holder.rec_id.setText("" + c.getMsg_id() + " ");

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (c.getIsEncypted().equalsIgnoreCase("1")) {
                    if (c.getImage().length() > 10) {
                        TypeMsgActivity.typeMsgActivity.dialog(c.getImage(), 4, position);

                    } else {
                        TypeMsgActivity.typeMsgActivity.dialog(c.getMsg(), 1, position);
                    }
                }

            }
        });


        holder.rec_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (c.getIsEncypted().equalsIgnoreCase("1")) {
//                    TypeMsgActivity.typeMsgActivity.dialog(c.getImage(), 4, position);
//
//                } else {
                if (!c.getMsg().equalsIgnoreCase("null")) {
                    TypeMsgActivity.typeMsgActivity.showDialogImg(mContext, position,"null");
//                }
                }else {
                    TypeMsgActivity.typeMsgActivity.dialog(dataItem.get(position).getImage(), 4, position);

                }
            }
        });
        holder.sent_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!c.getMsg().equalsIgnoreCase("null")) {
                    TypeMsgActivity.typeMsgActivity.showDialogImg(mContext, position,"null");
                }else {
                    TypeMsgActivity.typeMsgActivity.dialog(dataItem.get(position).getImage(), 4, position);

                }

//                TypeMsgActivity.typeMsgActivity.dialog(c.getImage(), 3, position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return dataItem.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_msg_view, parent, false);


        return new MyViewHolder(v);
    }

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView send, recive, send_id, rec_id;
        public LinearLayout ll_send, ll_rec;
        public Context mContext;
        ImageView sent_image, rec_image;


        public MyViewHolder(View view) {
            super(view);

            send = view.findViewById(R.id.send_msg);
            rec_image = view.findViewById(R.id.image_rec);
            sent_image = view.findViewById(R.id.image_send);
            rec_id = view.findViewById(R.id.msg_id);
            send_id = view.findViewById(R.id.send_msg_id);

            recive = view.findViewById(R.id.rec_msg);
            ll_rec = view.findViewById(R.id.ll_rec);
            ll_send = view.findViewById(R.id.ll_send);




            /*code = (TextView) view.findViewById(R.id.code);
            name = (TextView) view.findViewById(R.id.vendor_name);
            contact = (TextView) view.findViewById(R.id.contact);
            tin = (TextView) view.findViewById(R.id.tinNo);*/
        }
    }


}