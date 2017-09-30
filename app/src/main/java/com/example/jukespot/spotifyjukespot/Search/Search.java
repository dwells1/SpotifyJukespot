package com.example.jukespot.spotifyjukespot.Search;

/**
 * Created by nique on 9/19/2017.
 */
import kaaes.spotify.webapi.android.models.Track;
import java.util.List;

public class Search {
    public interface View{
        void reset();
        void addData(List<Track> items);
    }

    public interface ActionListener {

        void init(String token);

        String getCurrentQuery();

        void search(String searchQuery);

        void loadMoreResults();

        void selectTrack(Track item);

        void resume();

        void pause();

        void destroy();

    }
}
