package com.example.agattiprg4;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class myListAdapter extends ArrayAdapter<myList> {

    private int mostRecentlyClickedPosition;

    /**
     * Initializes the adapter
     * @param context
     * @param theList
     */
    public myListAdapter(Activity context, ArrayList<myList> theList) {
        super(context, 0, theList);
    }

    /**
     * Gets the data from the listview so the position and view can be passed or used
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

/*        Intent callerIntent = getIntent();
        String name = callerIntent.getStringExtra("name");*/

        // modify index textView
        TextView indexTV = listItemView.findViewById(R.id.indexTextView);
        indexTV.setText(String.valueOf(position + 1));

        myList currentList = getItem(position);

        TextView timePeriod = listItemView.findViewById(R.id.editTimePeriod);
        timePeriod.setText(currentList.getTimePeriodFinal());

        TextView temp = listItemView.findViewById(R.id.tempEdit);
        temp.setText(String.valueOf(currentList.getTemperatureFinal()) + "° F");

        TextView desc = listItemView.findViewById(R.id.editDesc);
        desc.setText(currentList.getDescFinal());

        return listItemView;
    }


}

