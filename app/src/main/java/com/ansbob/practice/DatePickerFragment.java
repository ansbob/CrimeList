package com.ansbob.practice;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DialogInterface.OnClickListener{
    private static final String ARG_KEY = "asdasd";
    public static final String EXTRA_KEY = "4u9031";
    DatePicker picker;

    public static DatePickerFragment newInstance(Date d) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY, d);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date d = (Date) getArguments().getSerializable(ARG_KEY);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.date_dialog, null);
        picker = (DatePicker) v.findViewById(R.id.date_picker_dialog);
        picker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, this).create();
    }

    private void sendResult(int resultCode, Date d) {
        if(getTargetFragment() == null) return;
        Intent i = new Intent();
        i.putExtra(EXTRA_KEY, d);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int year = picker.getYear();
        int month = picker.getMonth();
        int day = picker.getDayOfMonth();
        Date d = new GregorianCalendar(year, month, day).getTime();
        sendResult(Activity.RESULT_OK, d);
    }
}
