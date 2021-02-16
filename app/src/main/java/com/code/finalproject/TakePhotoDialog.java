package com.code.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.net.URI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TakePhotoDialog extends DialogFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PIC_CROP = 2;

    private Uri picUri;

    public TakePhotoDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Take a photo to replace the profile image?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            takePicture();
        });

        builder.setNegativeButton("No", (dialog, which) -> {

        });

        return builder.create();
    }

    private void takePicture() {
        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            startActivityForResult(picture, REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
