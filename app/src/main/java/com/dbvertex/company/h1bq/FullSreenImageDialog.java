package com.dbvertex.company.h1bq;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullSreenImageDialog extends DialogFragment {
    ImageView selectedImage,crossImg;
    Dialog dialog;
    String image;

    Intent intent;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        intent = getActivity().getIntent();
        image=intent.getStringExtra("image");

        Log.e("image",image);

        dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        dialog.setContentView(R.layout.activity_full_sreen_image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        selectedImage = (ImageView) dialog.findViewById(R.id.selectedImage); // init a ImageView
        crossImg=dialog.findViewById(R.id.crossImg);
        Picasso.with(getActivity()).load(image).placeholder(R.color.gray).into(selectedImage);

        crossImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });






        return dialog;
    }






}


