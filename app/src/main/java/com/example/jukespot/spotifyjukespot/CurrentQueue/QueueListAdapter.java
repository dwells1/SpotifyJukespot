package com.example.jukespot.spotifyjukespot.CurrentQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
import com.example.jukespot.spotifyjukespot.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lino on 10/29/2017.
 */

public class QueueListAdapter extends ArrayAdapter<SimpleTrack>  {
    private static final String TAG = CurrentQueueFragment.class.getSimpleName();

    private Logging log;
    private Context mContext;
    private List<SimpleTrack> trackList;
    private String imageUrl;
    private ImageView albumArtView;
    Bitmap albumCover;


    public QueueListAdapter(Context mContext, List<SimpleTrack> trackList){
        super(mContext, R.layout.list_item, trackList);
        this.mContext = mContext;
        this.trackList = trackList;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rowView = inflater.inflate(R.layout.list_item, null, true);
        TextView trackTitle = rowView.findViewById(R.id.entity_title);
        TextView trackSubtitle = rowView.findViewById(R.id.entity_subtitle);
        albumArtView = rowView.findViewById(R.id.entity_image);

        trackTitle.setText(trackList.get(position).song_name);
        trackSubtitle.setText(trackList.get(position).artist);
        imageUrl = trackList.get(position).album_image_link;

        //Solved image download issue
        Picasso.with(getContext()).load(imageUrl).into(albumArtView);

        return rowView;
    }
}

