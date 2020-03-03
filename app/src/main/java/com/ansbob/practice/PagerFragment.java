package com.ansbob.practice;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class PagerFragment extends Fragment {
    private static final String ARG_KEY = "ASDASDASD";
    private static final String DIALOG_DATE_TAG = "DDDD";
    private static final String INCREASED_TAG = "421";
    private static final int REQUEST_CODE_DATE = 0;
    private static final int REQUEST_CODE_SUSPECT = 1;
    private static final int REQUEST_CODE_PHOTO = 2;
    private static final int REQUEST_CODE_INCREASED_PHOTO = 3;
    private EditText editText;
    private Button dateButton;
    private CheckBox solvedCheckbox;
    private Button reportButton;
    private Button suspectButton;
    private ImageButton photoButton;
    private ImageView photoView;
    private Crime crime;
    private File photoFile;

    public static PagerFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY, crimeId);
        PagerFragment fragment = new PagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getCrimeReport() {
        String solvedString = null;
        if(crime.isSolved()) solvedString = getString(R.string.report_solved);
        else solvedString = getString(R.string.report_unsolved);

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, crime.getDate()).toString();

        String suspect = crime.getSuspect();
        if(suspect == null) suspect = getString(R.string.report_no_suspect);
        else suspect = getString(R.string.report_suspect, suspect);
        String report = getString(R.string.report, crime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView() {
        if(photoView == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(ARG_KEY);
        crime = CrimeSet.get(getActivity()).getCrime(id);
        photoFile = CrimeSet.get(getActivity()).getPhotoFile(crime);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pager_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                CrimeSet set = CrimeSet.get(getActivity());
                set.deleteCrime(crime);
                getActivity().finish();
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeSet.get(getActivity()).updateCrime(crime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pager_fragment, container, false);

        editText = (EditText) v.findViewById(R.id.fragment_edittext);
        dateButton = (Button) v.findViewById(R.id.fragment_button_date);
        solvedCheckbox = (CheckBox) v.findViewById(R.id.fragment_checkbox);
        reportButton = (Button) v.findViewById(R.id.report_button);
        suspectButton = (Button) v.findViewById(R.id.suspect_button);

        editText.setText(crime.getTitle());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dateButton.setText(DateFormat.format("MMM d, yyy ", crime.getDate()));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment d = DatePickerFragment.newInstance(crime.getDate());
                d.setTargetFragment(PagerFragment.this, REQUEST_CODE_DATE);
                d.show(fm, DIALOG_DATE_TAG);
            }
        });
        solvedCheckbox.setChecked(crime.isSolved());
        solvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContant = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        suspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContant, REQUEST_CODE_SUSPECT);
            }
        });

        if(crime.getSuspect() != null) suspectButton.setText(crime.getSuspect());

        //прокерка реагирующих активностей
        PackageManager pm = getActivity().getPackageManager();
        if(pm.resolveActivity(pickContant, pm.MATCH_DEFAULT_ONLY) == null) suspectButton.setEnabled(false);

        photoButton = (ImageButton) v.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = photoFile != null && captureImage.resolveActivity(pm) != null;
        photoButton.setEnabled(canTakePhoto);

        if(canTakePhoto) {
            Uri uri = Uri.fromFile(photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_CODE_PHOTO);
            }
        });

        photoView = (ImageView) v.findViewById(R.id.crime_photo);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                IncreasedPhoto ip = IncreasedPhoto.newInstance(photoFile);
                ip.setTargetFragment(PagerFragment.this, REQUEST_CODE_PHOTO);
                ip.show(fm, INCREASED_TAG);
            }
        });
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == REQUEST_CODE_DATE) {
            Date d = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_KEY);
            crime.setDate(d);
            dateButton.setText(DateFormat.format("MMM d, yyy ", crime.getDate()));
        }
        else if(requestCode == REQUEST_CODE_SUSPECT && data != null) {
            Uri contactUri = data.getData();
            String[] queryString = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = getActivity().getContentResolver().query(contactUri, queryString, null, null, null);
            try {
                if(c.getCount() == 0) return;
                c.moveToFirst();
                String suspect = c.getString(0);
                crime.setSuspect(suspect);
                suspectButton.setText(crime.getSuspect());
            } finally {
                c.close();
            }
        }
        else if(requestCode == REQUEST_CODE_PHOTO) {
            updatePhotoView();
        }
    }
}
