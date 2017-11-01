package com.example.jukespot.spotifyjukespot.CurrentlyPlaying;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
import com.example.jukespot.spotifyjukespot.R;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentlyPlayingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentlyPlayingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentlyPlayingFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CurrentlyPlayingFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private Logging log;
    private TextView txtSongTitle;
    private TextView txtSongArtist;
    private Button btnPlayPause;
    private Button btnNext;
    private Button btnPrev;
    private ImageView albumCoverView;

    private boolean isSongPaused;
    private boolean isSongPlaying = false;
    private String name;
    private String artist;
    private String urlString;
    private Bitmap coverImg;
    private MusicPlayer musicPlayer;

    private View view;


    public CurrentlyPlayingFragment() {
        // Required empty public constructor
    }

    public static CurrentlyPlayingFragment newInstance(String param1, String param2) {
        CurrentlyPlayingFragment fragment = new CurrentlyPlayingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new Logging();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_currently_playing, container, false);
        initButtons();
        initTextViews();
        initAlbumCover();
        try {
            musicPlayer = ((MainActivity) getActivity()).getMusicPlayer();
            isSongPlaying = musicPlayer.isPlaying();
            isSongPaused = musicPlayer.getIsPaused();

            //checkWhenSongEnds();
            if(isSongPlaying || isSongPaused){
                name = musicPlayer.getCurrentTrack().name;
                artist = musicPlayer.getCurrentTrack().artistName;
                urlString = musicPlayer.getCurrentTrack().albumCoverWebUrl;
                updateSongInfo();
            }else{
                updateSongInfo();
            }
        }catch(NullPointerException e){
            log.logErrorNoToast(TAG,"null Music Player returned");
            updateSongInfo();
        }
        return view;
    }

    public void setAlbumImage(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void...params) {

                try{
                    URL coverImgUrl = new URL(urlString);
                    InputStream in = coverImgUrl.openStream();
                    coverImg = BitmapFactory.decodeStream(in);
                }catch(MalformedURLException e){
                    log.logErrorNoToast(TAG,"Not A valid URL For Album Cover Image");
                }catch(IOException e){
                    log.logErrorNoToast(TAG,"Not A valid Connection For Album Cover Image");
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void result){
                albumCoverView.setImageBitmap(coverImg);
            }
        }.execute();
    }

    public void initButtons(){
        btnPlayPause = view.findViewById(R.id.btnPlayPauseSong);
        btnPlayPause.setOnClickListener(this);


        btnNext = view.findViewById(R.id.btnNextSong);
        btnNext.setOnClickListener(this);

        btnPrev = view.findViewById(R.id.btnPrevSong);
        btnPrev.setOnClickListener(this);
    }

    public void initTextViews(){
        txtSongArtist = view.findViewById(R.id.txtSongArtist);
        txtSongTitle = view.findViewById(R.id.txtSongTitle);
    }

    public void initAlbumCover(){
        albumCoverView = view.findViewById(R.id.albumCoverImg);
    }

    public void showButtons(){
        btnPlayPause.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnPrev.setVisibility(View.VISIBLE);
    }
    public void hideButtons(){
        btnPlayPause.setVisibility(View.INVISIBLE);
        //btnPause.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        btnPrev.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onClick(View view){
        log.logMessage(TAG, "PRESSED: " + view.getResources().getResourceName(view.getId()));
        isSongPlaying = musicPlayer.isPlaying();
       // isSongPaused = musicPlayer.getIsPaused();
        switch(view.getId()){
            case R.id.btnPlayPauseSong:
                if(!isSongPlaying && isSongPaused){
                    musicPlayer.resume();
                    btnPlayPause.setText("Pause");
                    isSongPaused = false;
                    musicPlayer.setIsPaused(isSongPaused);
                }
                else if(isSongPlaying && !isSongPaused){
                    musicPlayer.pause();
                    btnPlayPause.setText("Play");
                    isSongPaused = true;
                    musicPlayer.setIsPaused(isSongPaused);
                }
                break;
            case R.id.btnNextSong:
               // musicPlayer.next();
                try{
                    name = musicPlayer.getNextTrack().name;
                    artist = musicPlayer.getNextTrack().artist;
                    urlString = musicPlayer.getNextTrack().albumImgLink;
                    log.logMessage(TAG,"NEXT SONG NAME: " + name + " by " + artist);
                    updateSongInfo();
                    isSongPaused = false;
                    musicPlayer.setIsPaused(isSongPaused);
                    btnPlayPause.setText("Pause");
                    musicPlayer.next();
                }catch(NullPointerException | IndexOutOfBoundsException noNextTrack){
                    noNextTrack.printStackTrace();
                    log.logMessageWithToast(getActivity(),TAG,"No Tracks left in Current Queue!");
                    updateSongInfo();
                    break;
                }
                break;

            case R.id.btnPrevSong:
                try{
                    name = musicPlayer.getPrevTrack().name;
                    artist = musicPlayer.getPrevTrack().artist;
                    urlString = musicPlayer.getPrevTrack().albumImgLink;
                    log.logMessage(TAG,"Previous SONG NAME: " + name + " by " + artist);
                    updateSongInfo();
                    isSongPaused = false;
                    musicPlayer.setIsPaused(isSongPaused);
                    btnPlayPause.setText("Pause");
                    musicPlayer.prev();
                }catch(NullPointerException  | IndexOutOfBoundsException noPrevTrack){
                    log.logMessageWithToast(getActivity(),TAG,"No Previous Tracks in Current Queue!");
                    updateSongInfo();
                    break;
                }
                break;
        }
        ((MainActivity) getActivity()).getMusicPlayer();

    }

    public void updateSongInfo(){
        isSongPlaying = musicPlayer.isPlaying();
        isSongPaused =musicPlayer.getIsPaused();

        if(isSongPlaying || isSongPaused){
            txtSongTitle.setText(name);
            txtSongArtist.setText(artist);
            setAlbumImage();
            if(isSongPaused)
                btnPlayPause.setText("Play");
            showButtons();

        }else{
            txtSongTitle.setText("NO SONGS CURRENTLY PLAYING");
            txtSongArtist.setVisibility(View.INVISIBLE);
            albumCoverView.setVisibility(View.INVISIBLE);
            hideButtons();

        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
