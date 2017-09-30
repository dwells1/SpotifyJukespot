package com.example.jukespot.spotifyjukespot.Search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.R;
import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    private Logging log;
    private static final String TAG = SearchResultsAdapter.class.getSimpleName();

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView title;
    public final TextView subtitle;
    public final ImageView image;


    public ViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.entity_title);
        subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
        image = (ImageView) itemView.findViewById(R.id.entity_image);
        itemView.setOnClickListener(this);
        log = new Logging();
    }

    @Override
    public void onClick(View v) {
        notifyItemChanged(getLayoutPosition());
        mListener.onItemSelected(v, mItems.get(getAdapterPosition()));
        String title = mItems.get(getAdapterPosition()).name;
        log.logMessage(TAG,title);
    }
}

public interface ItemSelectedListener {
    void onItemSelected(View itemView, Track item);
}

    public SearchResultsAdapter(Context context, ItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track item = mItems.get(position);

        holder.title.setText(item.name);

        List<String> names = new ArrayList<>();
        for (ArtistSimple i : item.artists) {
            names.add(i.name);
        }
        Joiner joiner = Joiner.on(", ");
        holder.subtitle.setText(joiner.join(names));

        Image image = item.album.images.get(0);
        if (image != null) {
            Picasso.with(mContext).load(image.url).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
