package com.example.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class LogFragment extends Fragment {
    private LogFragmentListener listener;
    private Spinner spHours, spMinutes, spGoal;
    private Button btnLog;
    private DatabaseHelper dbHelper;

    public interface LogFragmentListener {
        void sendHoursToActivity(int hours, int minutes, int goalHours);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmant_log, container, false);

        btnLog = (Button) v.findViewById(R.id.btnLogHours);
        spHours = (Spinner) v.findViewById(R.id.spHours);
        spMinutes = (Spinner) v.findViewById(R.id.spMinutes);
        spGoal = (Spinner) v.findViewById(R.id.spGoal);

        setupSpinners(v);
        setupListeners();

        return v;
    }

    private void setupListeners() {
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hours = Integer.parseInt(spHours.getSelectedItem().toString());
                int minutes = Integer.parseInt(spMinutes.getSelectedItem().toString());
                int goalHours = Integer.parseInt(spGoal.getSelectedItem().toString());
                listener.sendHoursToActivity(hours, minutes, goalHours);
            }
        });
    }

    private void setupSpinners(View v) {
        ArrayList<String> hoursList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hoursList.add(String.valueOf(i));
        }

        ArrayList<String> minutesList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minutesList.add(String.valueOf(i));
        }

        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(
            v.getContext(), R.layout.spinner, hoursList);
        ArrayAdapter<String> minutesAdapter = new ArrayAdapter<>(
            v.getContext(), R.layout.spinner, minutesList);

        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spHours.setAdapter(hoursAdapter);
        spGoal.setAdapter(hoursAdapter);
        spMinutes.setAdapter(minutesAdapter);

        setSelectedItems();
    }

    private void setSelectedItems() {
        Cursor c = dbHelper.getCurrentDay();
        if (c.getCount() != 0) {
            c.moveToFirst();
            String minutesString = c.getString(0);
            String goalMinutesString = c.getString(1);

            int hours = Integer.parseInt(minutesString) / 60;
            int minutes = Integer.parseInt(minutesString) % 60;
            int goalHours = Integer.parseInt(goalMinutesString) / 60;

            spHours.setSelection(hours, true);
            spMinutes.setSelection(minutes, true);
            spGoal.setSelection(goalHours, true);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LogFragmentListener) {
            listener = (LogFragmentListener) context;
            dbHelper = new DatabaseHelper(context);
        } else {
            throw new RuntimeException(context.toString() + " must implement LogFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
