package com.example.jukespot.spotifyjukespot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
import com.example.jukespot.spotifyjukespot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominique on 11/5/2017.
 */

public class JukeboxListAdapter extends ArrayAdapter<JukeBoxResponse> {

    private static class ViewHolder {
        TextView name;
        TextView home;
    }

    public JukeboxListAdapter(Context context, List<JukeBoxResponse> jukeboxes){
        super(context, R.layout.item_jukebox, jukeboxes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        JukeBoxResponse user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_jukebox, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.jukeboxName);
            //viewHolder.home = (TextView) convertView.findViewById(R.id.tvHome);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(user.getLocation_fields().getPlaylist_name());
        //viewHolder.home.setText(user.hometown);
        // Return the completed view to render on screen
        return convertView;
    }
}
