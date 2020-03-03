package com.ansbob.practice;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class IncreasedPhoto extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_KEY = "31954u09';";
    private ImageView photo;

    public static IncreasedPhoto newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY, file);
        IncreasedPhoto in = new IncreasedPhoto();
        in.setArguments(args);
        return in;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File photoFile = (File) getArguments().getSerializable(ARG_KEY);
        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.increased_photo, null);
        photo = (ImageView) v.findViewById(R.id.increased_photo);
        photo.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity()).setView(v).setPositiveButton(android.R.string.ok, this).create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
