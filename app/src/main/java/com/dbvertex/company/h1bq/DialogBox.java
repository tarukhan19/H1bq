package com.dbvertex.company.h1bq;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.dbvertex.company.h1bq.model.HomeDTO;
import com.dbvertex.company.h1bq.session.SessionManager;

import java.util.ArrayList;

public class DialogBox {
    Context context;
    SessionManager sessionManager;
    ArrayList<HomeDTO> homeDTOArrayList;
    Apis apis = new Apis();
    RequestQueue requestQueue;




    private void showDeleteDialog(final String postId, final int adapterPosition, final Context context, final ArrayList<HomeDTO>
            homeDTOArrayList, final RequestQueue requestQueue) {

        this.context = context;
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_logout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //Log.e("DetailDialPos", adapterPosition + "");

        LinearLayout ok = (LinearLayout) dialog.findViewById(R.id.ok);
        LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
        TextView msgTV = (TextView) dialog.findViewById(R.id.msgTV);
        msgTV.setText("You want to delete this post!");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                apis.deletePost(postId, adapterPosition, context, homeDTOArrayList, requestQueue);
                //Log.e("DetailDialPos", adapterPosition + "");

                dialog.dismiss();


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });


        dialog.show();

    }


    private void openReportDialog(final String postId, final int adapterPosition, final Context context) {

        this.context = context;
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_reportabuse);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();


        LinearLayout ok = dialog.findViewById(R.id.ok);
        LinearLayout cancel = dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final EditText descripationET = dialog.findViewById(R.id.descripationET);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descriptionS = descripationET.getText().toString();
                if (descriptionS.isEmpty()) {
                    openDialog("Please enter the reason for report abuse", context);
                } else {
                    // reportAbuse(descriptionS, postId, adapterPosition);
                    dialog.dismiss();
                    HideKeyboard.hideKeyboard(context);
                }


            }
        });
    }


    private void openDialog(String s, Context context) {
        this.context = context;

        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_signupsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        LinearLayout ok = dialog.findViewById(R.id.ok);
        TextView msg = dialog.findViewById(R.id.msg);
        ImageView image = dialog.findViewById(R.id.image);

        msg.setText(s);
        image.setVisibility(View.GONE);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }


}
