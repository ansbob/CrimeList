package com.ansbob.practice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class PagerActivity extends AppCompatActivity {
    private ViewPager pager;
    private static final String EXTRA_KEY = "asdasd";
    private List<Crime> crimes;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent i = new Intent(packageContext, PagerActivity.class);
        i.putExtra(EXTRA_KEY, crimeId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_KEY);
        crimes = CrimeSet.get(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();

        pager = (ViewPager) findViewById(R.id.activity_pager);
        pager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = crimes.get(position);
                return PagerFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });

        for(int i=0;i<crimes.size();i++) {
            if(crimes.get(i).getId().equals(crimeId)) {
                pager.setCurrentItem(i);
                break;
            }
        }
    }
}