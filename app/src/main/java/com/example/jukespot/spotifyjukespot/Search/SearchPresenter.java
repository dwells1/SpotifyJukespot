package com.example.jukespot.spotifyjukespot.Search;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

import com.example.jukespot.spotifyjukespot.Logging.Logging;

public class SearchPresenter implements Search.ActionListener{
    public static final int PAGE_SIZE = 20;

    private static final String TAG = SearchPresenter.class.getSimpleName();
    private final Context mContext;
    private final Search.View mView;
    private String mCurrentQuery;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchListener;
    private Logging log;


    public SearchPresenter(Context context, Search.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void init(String accessToken) {
        log = new Logging();
        log.logMessage(TAG,"Api Client created");
        SpotifyApi spotifyApi = new SpotifyApi();

        log.logMessage(TAG,"Access Token " + accessToken);

        if (accessToken != null) {
            spotifyApi.setAccessToken(accessToken);
        } else {
            log.logMessage(TAG,"No valid access token");
        }

        mSearchPager = new SearchPager(spotifyApi.getService());

        //mContext.bindService(PlayerService.getIntent(mContext), mServiceConnection, Activity.BIND_AUTO_CREATE);
    }

    public void search(@Nullable String searchQuery){
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            log.logMessage(TAG,"query text submit " + searchQuery);
            mCurrentQuery = searchQuery;
            mView.reset();
            mSearchListener = new SearchPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    mView.addData(items);
                }

                @Override
                public void onError(Throwable error) {
                    log.logMessage(TAG,error.getMessage());
                }
            };
            if(mSearchListener != null) {
                mSearchPager.getFirstPage(searchQuery, PAGE_SIZE, mSearchListener);
            }
        }
    }

    @Override
    public void destroy() {
        //mContext.unbindService(mServiceConnection);
    }

    @Override
    @Nullable
    public String getCurrentQuery() {
        return mCurrentQuery;
    }

    @Override
    public void resume() {
        //mContext.stopService(PlayerService.getIntent(mContext));
    }

    @Override
    public void pause() {
        //mContext.startService(PlayerService.getIntent(mContext));
    }

    @Override
    public void loadMoreResults() {
        log.logMessage("SearchPresenter", "Load more...");
        mSearchPager.getNextPage(mSearchListener);
    }

    @Override
    public void selectTrack(Track item) {

//        if (mPlayer == null) return;
//
//        String currentTrackUrl = mPlayer.getCurrentTrack();
//
//        if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl)) {
//            mPlayer.play(previewUrl);
//        } else if (mPlayer.isPlaying()) {
//            mPlayer.pause();
//        } else {
//            mPlayer.resume();
//        }
    }

}
