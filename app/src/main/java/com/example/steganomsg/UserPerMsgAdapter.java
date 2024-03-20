package com.example.steganomsg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;



public class UserPerMsgAdapter extends ArrayAdapter<ResponseMessage> {
    Context context;
    String privateKey;




    private static class ViewHolder {
        TextView rec,send;
        LinearLayout ll_send,ll_rec;
        LinearLayout llTotal;
    }

    public UserPerMsgAdapter(Context context, ArrayList<ResponseMessage> responseMessages) {
        super(context, R.layout.item_msg_view, responseMessages);
        this.context = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)  {
        ViewHolder viewHolder; // view lookup cache stored in tag

        final ResponseMessage responseMessage = getItem(position);

        viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_msg_view, parent, false);
            viewHolder.rec = (TextView) convertView.findViewById(R.id.rec_msg);
            viewHolder.send = (TextView) convertView.findViewById(R.id.send_msg);
            viewHolder.ll_rec = (LinearLayout) convertView.findViewById(R.id.ll_rec);
            viewHolder.ll_send = (LinearLayout) convertView.findViewById(R.id.ll_send);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ll_send.setVisibility(View.VISIBLE);
        viewHolder.send.setText(""+responseMessage.getMsg() + " " );
        UserSessionManager userSessionManager = new UserSessionManager(context);
        String user=userSessionManager.getUserDetails().get("mob");
       if (responseMessage.getSender().equalsIgnoreCase(""+user)){
           viewHolder.ll_send.setVisibility(View.VISIBLE);
           viewHolder.ll_rec.setVisibility(View.GONE);
           viewHolder.send.setText(""+responseMessage.getMsg() + " " );
       }else if (responseMessage.getReceiver().equalsIgnoreCase(""+user)){
           viewHolder.ll_rec.setVisibility(View.VISIBLE);
           viewHolder.ll_send.setVisibility(View.GONE);
           viewHolder.rec.setText(""+responseMessage.getMsg() + " " );
       }

       convertView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {


               AlertDialog.Builder builder = new AlertDialog.Builder(context);
               builder.setTitle("Title");

// Set up the input
               final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
               input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
               builder.setView(input);

// Set up the buttons
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                      // privateKey = input.getText().toString();
                   }
               });
               builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });

//               builder.show();

               return false;
           }
       });

        return convertView;
    }
}
