package com.ansbob.practice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recycler;
    private boolean subtitleVisible;
    private Adapter adapter;
    private static final String SAVED_INSTANCE_KEY = "asdi99-as";

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Crime crime;
        private TextView TitleTextView;
        private TextView DateTextView;
        private CheckBox SolvedCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            TitleTextView = (TextView) itemView.findViewById(R.id.list_item_title);
            DateTextView = (TextView) itemView.findViewById(R.id.item_list_date);
            SolvedCheckBox = (CheckBox) itemView.findViewById(R.id.item_list_checkbox);
        }

        public void bindCrime(final Crime crime) {
            this.crime = crime;
            TitleTextView.setText(crime.getTitle());
            DateTextView.setText(crime.getDate().toString());
            SolvedCheckBox.setChecked(crime.isSolved());
            SolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    crime.setSolved(isChecked);
                    CrimeSet.get(getActivity()).updateCrime(crime);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent i = PagerActivity.newIntent(getActivity(), crime.getId());
            startActivity(i);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Crime> crimes;

        public Adapter(List<Crime> crimes) {
            this.crimes = crimes;
        }

        public void setCrimes(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindCrime(crimes.get(position));
        }

        @Override
        public int getItemCount() {
            return crimes.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_INSTANCE_KEY);
            getActivity().invalidateOptionsMenu();
            updateSubtitle();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(SAVED_INSTANCE_KEY, subtitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_item, menu);
        MenuItem subtitle = menu.findItem(R.id.item_menu_subtitle);
        if(subtitleVisible) subtitle.setTitle(R.string.hide);
        else subtitle.setTitle(R.string.show);
    }

    private void updateSubtitle() {
        int crimeCount = CrimeSet.get(getActivity()).getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);
        if(!subtitleVisible) subtitle = null;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_add:
                Crime c = new Crime();
                CrimeSet set = CrimeSet.get(getActivity());
                set.addCrime(c);
                Intent i = PagerActivity.newIntent(getActivity(), c.getId());
                startActivity(i);
                return true;
            case R.id.item_menu_subtitle:
                subtitleVisible = !subtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view, container, false);
        recycler = (RecyclerView) v.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return v;
    }

    private void updateUI() {
        List<Crime> crimes = CrimeSet.get(getActivity()).getCrimes();
        if(adapter == null) {
            adapter = new Adapter(crimes);
            recycler.setAdapter(adapter);
        }else {
            adapter.setCrimes(crimes);
            adapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }
}
