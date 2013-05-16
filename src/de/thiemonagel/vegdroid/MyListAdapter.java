package de.thiemonagel.vegdroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<Venue> {

    private ArrayList<Venue> venues;

    public MyListAdapter(Context context, int textViewResourceId,
            ArrayList<Venue> objects) {
        super(context, textViewResourceId, objects);
        if(objects != null)
        this.venues = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.list_item, null);
        }
        Venue venue = venues.get(position);

        if (venue == null || venue.filtered(getContext())) {
            v.setVisibility(View.GONE);
        }
        else if (v != null) {
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView description = (TextView) v
                    .findViewById(R.id.short_description);
            TextView uri = (TextView) v.findViewById(R.id.uri);

            name.setText(venue.name);
            description.setText(venue.shortDescription);
            uri.setText(venue.website);
        }
        return v;
    }

}
