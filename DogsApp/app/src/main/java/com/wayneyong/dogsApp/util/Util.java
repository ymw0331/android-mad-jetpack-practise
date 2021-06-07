package com.wayneyong.dogsApp.util;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wayneyong.dogsApp.R;

//for glide
public class Util {

    public static void loadImage(ImageView imageView, String url, CircularProgressDrawable progressDrawable) {

        RequestOptions options = new RequestOptions()
                .placeholder(progressDrawable)
                .error(R.mipmap.ic_launcher);

        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(options)
                .load(url)
                .into(imageView);
    }


    //set the animation for progress when loading
    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable cpd = new CircularProgressDrawable(context);
        cpd.setStrokeWidth(10f); //let system know how large the line of spinner look
        cpd.setCenterRadius(50f);
        cpd.start();
        return cpd;
    }

    //data binding for fetching imageUrl
    @BindingAdapter("android:imageUrl")
    public static void loadImage(ImageView view, String url) {
        loadImage(view, url, getProgressDrawable(view.getContext()));
    }
}
