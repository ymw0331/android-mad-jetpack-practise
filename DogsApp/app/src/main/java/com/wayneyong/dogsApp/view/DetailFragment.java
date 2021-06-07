package com.wayneyong.dogsApp.view;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wayneyong.dogsApp.R;
import com.wayneyong.dogsApp.databinding.FragmentDetailBinding;
import com.wayneyong.dogsApp.databinding.SendSmsDialogBinding;
import com.wayneyong.dogsApp.model.DogBreed;
import com.wayneyong.dogsApp.model.DogPalette;
import com.wayneyong.dogsApp.model.SmsInfo;
import com.wayneyong.dogsApp.util.Util;
import com.wayneyong.dogsApp.viewmodel.DetailViewModel;

public class DetailFragment extends Fragment {

    //    FragmentDetailBinding mBinding;
    private FragmentDetailBinding binding;
    private DetailViewModel viewModel;
    private int dogUuid;

    private DogBreed currentDog;

    public Boolean sendSmsStarted = false;


    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        mBinding = FragmentDetailBinding.inflate(inflater, container, false);
//        View view = mBinding.getRoot();

        FragmentDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        this.binding = binding; //match to declared binding in global scope

        setHasOptionsMenu(true); //show menu in this fragment

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {

            dogUuid = DetailFragmentArgs.fromBundle(getArguments()).getDogUuid();
//            mBinding.textView2.setText(String.valueOf(dogUuid));

        }
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        viewModel.fetch(dogUuid);
        //check DetailViewModel fetch function, dogUuid is pass as parameter for detailView
        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.dogLiveData.observe(getViewLifecycleOwner(), new Observer<DogBreed>() {
            @Override
            public void onChanged(DogBreed dogBreed) {
                if (dogBreed != null && dogBreed instanceof DogBreed && getContext() != null) {
//                    mBinding.dogName.setText(dogBreed.dogBreed);
//                    mBinding.dogPurpose.setText(dogBreed.bredFor);
//                    mBinding.dogLifespan.setText(dogBreed.lifeSpan);
//                    mBinding.dogTemperament.setText(dogBreed.temperament);

//                    if (mBinding.dogImage != null) {
//                        Util.loadImage(mBinding.dogImage, dogBreed.imageUrl, new CircularProgressDrawable(getContext()));
//                    }
                    currentDog = dogBreed;

                    //replace by data binding
                    binding.setDog(dogBreed);
                    if (dogBreed.imageUrl != null) {
                        setupBackgroundColor(dogBreed.imageUrl);
                    }
                }
            }
        });
    }

    //use palette library to generate color for image
    private void setupBackgroundColor(String url) {

        //require bitmap to retrieve color
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        Palette.from(resource)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@Nullable Palette palette) {
                                        int intColor = palette.getLightMutedSwatch().getRgb();
                                        DogPalette myPalette = new DogPalette(intColor);
                                        binding.setPalette(myPalette);
                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                        //leaf empty no use
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_send_sms: {
//                Toast.makeText(getContext(), "Action sms", Toast.LENGTH_LONG).show();
                //set flag to prevent too many times
                if (!sendSmsStarted) {
                    sendSmsStarted = true;
                    ((MainActivity) getActivity()).checkSmsPermission();
                }
                break;
            }
            case R.id.action_share: {
                //make sharing as generic as possible to share to multiple app
//                Toast.makeText(getContext(), "Action share", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed");
                intent.putExtra(Intent.EXTRA_TEXT, currentDog.dogBreed + " bred for " + currentDog.bredFor);
                intent.putExtra(Intent.EXTRA_STREAM, currentDog.imageUrl);
                startActivity(Intent.createChooser(intent, "Share with")); //allow user to choose app to share
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPermissionResult(Boolean permissionGranted) {

        if (isAdded() && sendSmsStarted && permissionGranted) {
            SmsInfo smsInfo = new SmsInfo("", currentDog.dogBreed + " bred for " + currentDog.bredFor, currentDog.imageUrl);

            //create dialog
            SendSmsDialogBinding dialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()),
                    R.layout.send_sms_dialog,
                    null,
                    false
            );

            new AlertDialog.Builder(getContext())
                    .setView(dialogBinding.getRoot())
                    .setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!dialogBinding.smsDestination.getText().toString().isEmpty()) {
                                smsInfo.to = dialogBinding.smsDestination.getText().toString();
                                sendSms(smsInfo);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            sendSmsStarted = false;
            dialogBinding.setSmsInfo(smsInfo);
        }
    }

    //sms functionality
    private void sendSms(SmsInfo smsInfo) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null);
        Toast.makeText(getContext(), "SMS sent", Toast.LENGTH_LONG).show();
    }
}


