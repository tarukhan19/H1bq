package com.dbvertex.company.h1bq.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dbvertex.company.h1bq.R;
import com.facebook.CallbackManager;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.ShareMessengerGenericTemplateContent;
import com.facebook.share.model.ShareMessengerGenericTemplateElement;
import com.facebook.share.model.ShareMessengerURLActionButton;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


public class DynamiclinkCreate {
    static String mInvitationUrl;
    String packagename;
    static LinearLayout shareListLL, progressbarLL;
    static ProgressBar progressbar;
    static CallbackManager callbackManager;
    static ShareDialog shareDialog;
    public static void shareImage(final String uri, final Context context, final String title, final String postid)
    {

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog((Activity) context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_share, null);

        createUrl(postid);


        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        LinearLayout facebookLL = dialog.findViewById(R.id.facebookLL);
        LinearLayout gmailLL = dialog.findViewById(R.id.gmailLL);
        LinearLayout googleplusLL = dialog.findViewById(R.id.googleplusLL);
        LinearLayout messengerLL = dialog.findViewById(R.id.messengerLL);
        LinearLayout whatsappLL = dialog.findViewById(R.id.whatsappLL);
        LinearLayout intagramLL = dialog.findViewById(R.id.intagramLL);
        shareListLL = dialog.findViewById(R.id.shareListLL);
        progressbarLL = dialog.findViewById(R.id.progressbarLL);
        progressbar = dialog.findViewById(R.id.progressbar);

        shareListLL.setVisibility(View.GONE);
        progressbarLL.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);

        facebookLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Bitmap image=null;
                try {
                    URL url = new URL(uri);
                     image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(IOException e) {
                    System.out.println(e);
                }


                ShareFeedContent feed = new ShareFeedContent.Builder()
                        .setLink(mInvitationUrl)
                        .setPicture(uri)
                        .setLinkCaption(title)
                        .build();
                shareDialog.show(feed);

            }
        });

        gmailLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(uri, context, title,  "com.google.android.gm");
            }
        });

        googleplusLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(uri, context, title,  "com.google.android.apps.plus");
            }
        });

        messengerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareMessengerURLActionButton actionButton =
                        new ShareMessengerURLActionButton.Builder()
                                .setTitle(mInvitationUrl)
                                .setUrl(Uri.parse(mInvitationUrl))
                                .build();
                ShareMessengerGenericTemplateElement genericTemplateElement =
                        new ShareMessengerGenericTemplateElement.Builder()
                                .setTitle(title)
                                .setImageUrl(Uri.parse(uri))
                                .setButton(actionButton)
                                .build();
                ShareMessengerGenericTemplateContent genericTemplateContent =
                        new ShareMessengerGenericTemplateContent.Builder()
                                .setPageId("Your Page Id") // Your page ID, required
                                .setGenericTemplateElement(genericTemplateElement)
                                .build();

                MessageDialog.show((Activity) context, genericTemplateContent);
            }
        });

        whatsappLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                share(uri, context, title,  "com.whatsapp");
            }
        });

        intagramLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(uri, context, title,  "com.instagram.android");
            }
        });

        dialog.show();

    }

    private static void share(final String uri, final Context context, final String title, final String packagename) {
        try

        {
            final Intent i = new Intent(Intent.ACTION_SEND);

            i.setPackage(packagename);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String sAux = "\n" + title + "\n\n";
            sAux = sAux + mInvitationUrl + "\n\n";

            if (!uri.isEmpty()) {
                Picasso.with(context).load(uri).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        i.setType("image/*,text/plain");

                        i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            } else {
                i.setType("text/plain");

            }

            i.putExtra(Intent.EXTRA_TEXT, sAux);
            context.startActivity(Intent.createChooser(i, "Share post"));
        }
        catch (Exception ex) // in case Instagram not installed in your device
        {
            ex.printStackTrace();
            Toast.makeText(context, "No app can perform this action", Toast.LENGTH_SHORT).show();
        }

    }

    public static void createUrl( String postid) {

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(Endpoints.POST_DETAIL + "?post_id=" + postid))
                .setDomainUriPrefix("https://h1bq.page.link/")
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.ios.H1BQ")
                                .setAppStoreId("1409517363")
                                .setFallbackUrl(Uri.parse("https://itunes.apple.com/us/app/storyboard-podcasts/id1409517363?mt=8"))
                                .setMinimumVersion("1.0")
                                .build())
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.dbvertex.company.h1bq")
                                .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.mobiletemple.photopeople"))
                                .build())

                .setNavigationInfoParameters(new DynamicLink.NavigationInfoParameters.Builder()
                        .setForcedRedirectEnabled(true).build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("h1bq")
                                .setDescription("")
                                .setImageUrl(Uri.parse(""))
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink().toString();
                        shareListLL.setVisibility(View.VISIBLE);
                        progressbarLL.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                        Log.e("mInvitationUrl", mInvitationUrl);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


}
