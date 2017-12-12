package com.example.jukespot.spotifyjukespot.CurrentlyPlaying;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Enums.ChangeType;
import com.example.jukespot.spotifyjukespot.Enums.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
import com.example.jukespot.spotifyjukespot.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentlyPlayingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentlyPlayingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentlyPlayingFragment extends Fragment implements View.OnClickListener, Observer {

    private static final String TAG = CurrentlyPlayingFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private Logging log;
    private User user;
    private TextView txtSongTitle;
    private TextView txtSongArtist;
    private TextView permissionsHeader;
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
        user = User.getInstance();
        musicPlayer = musicPlayer.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_currently_playing, container, false);
        initButtons();
        initTextViews();
        initAlbumCover();
        validateUserPermissions();
        try {
            isSongPlaying = musicPlayer.isPlaying();
            isSongPaused = musicPlayer.getIsPaused();

            if(isSongPlaying || isSongPaused){
                log.logMessage(TAG,"CurrentQueue is");
                for(SimpleTrack s: musicPlayer.getCurrentQueue()){
                    log.logMessage(TAG,s.song_name);
                }
                name = musicPlayer.getCurrentTrackNotFromPlayer().song_name;
                artist = musicPlayer.getCurrentTrackNotFromPlayer().artist;
                urlString = musicPlayer.getCurrentTrackNotFromPlayer().album_image_link;
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

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("StaticFieldLeak")
    public void setAlbumImage(){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void...params) {

                try{
                    URL coverImgUrl = new URL(urlString);
                    InputStream in = coverImgUrl.openStream();
                    coverImg = BitmapFactory.decodeStream(in);
                    in.close();
                }catch(MalformedURLException e){
                    log.logErrorNoToast(TAG,"Not A valid URL For Album Cover Image");
                }catch(IOException e) {
                    log.logErrorNoToast(TAG, "Not A valid Connection For Album Cover Image");
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
        permissionsHeader = view.findViewById(R.id.permissionHeader);
    }

    public void initAlbumCover(){
        albumCoverView = view.findViewById(R.id.albumCoverImg);
    }
    public void validateUserPermissions(){
        switch(user.getUserPermissions()){
            case CAN_PLAY_NO_EDIT:
                disableAllButtons();
                permissionsHeader.setText("You DO NOT have permission to use player");
                break;
            case CAN_PLAY_AND_EDIT:
                permissionsHeader.setText("You have permission to use player");
                break;
            case CAN_EDIT_NO_PLAY:
                permissionsHeader.setText("You have permission to use player");
                break;
            case NO_EDIT_NO_PLAY:
                disableAllButtons();
                permissionsHeader.setText("You DO NOT have permission to use player");
                break;
        }

        /*Subscribers cannot pause / play song only creator has access to that*/
        if(user.getTypeOfUser() == UserType.SUBSCRIBER)
            disablePausePlayButton();

    }
    public void disableAllButtons(){
        btnPlayPause.setAlpha(.5f);
        btnPlayPause.setClickable(false);

        btnNext.setAlpha(.5f);
        btnNext.setClickable(false);

        btnPrev.setAlpha(.5f);
        btnPrev.setClickable(false);
    }
    public void showButtons(){
        btnPlayPause.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnPrev.setVisibility(View.INVISIBLE);
    }
    public void hideButtons(){
        btnPlayPause.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        btnPrev.setVisibility(View.INVISIBLE);
    }
    public void disablePausePlayButton(){
        btnPlayPause.setAlpha(.5f);
        btnPlayPause.setClickable(false);
    }
    @Override
    public void onClick(View view){
        log.logMessage(TAG, "PRESSED: " + view.getResources().getResourceName(view.getId()));
        checkViewButtons(view);
    }
    public void checkViewButtons(View view){

        isSongPlaying = musicPlayer.isPlaying();
        isSongPaused = musicPlayer.getIsPaused();
        switch(view.getId()){
            case R.id.btnPlayPauseSong:
                if(!isSongPlaying && isSongPaused){
                    musicPlayer.resume();
                    btnPlayPause.setText("Pause");
                    // isSongPaused = false;
                    //musicPlayer.setIsPaused(isSongPaused);
                }
                else if(isSongPlaying && !isSongPaused){
                    musicPlayer.pause();
                    btnPlayPause.setText("Play");
                    //isSongPaused = true;
                    //musicPlayer.setIsPaused(isSongPaused);
                }
                break;
            case R.id.btnNextSong:
                /*TODO: CHECK QUEUE SIZE BEFORE CALLING NEXT BECAUSE IT WILL REMOVE A SONG FROM THE WEBSERVIC BEFORE CHECKING IF ITS THE LAST ONE IN THE QUEUE*/
                try {
                    if(musicPlayer.getCurrentQueue().size() > 1){
                        musicPlayer.next();
                    }else if(musicPlayer.getCurrentQueue().size() == 1){
                        log.logMessageWithToast(getActivity(),TAG,"This is the last song on the queue");
                    }else if(musicPlayer.getCurrentQueue().size() <= 0){
                        updateSongInfo();
                    }

                }catch(NullPointerException | IndexOutOfBoundsException noNextTrack){
                    // noNextTrack.printStackTrace();
                    // log.logMessageWithToast(getActivity(),TAG,"No Tracks left in Current Queue!");
                    log.logError(getActivity(), TAG, "Array For Current Queue is not valid");
                    updateSongInfo();
                    break;
                }

                break;

            case R.id.btnPrevSong:
                try{
                    name = musicPlayer.getPrevTrack().song_name;
                    artist = musicPlayer.getPrevTrack().artist;
                    urlString = musicPlayer.getPrevTrack().album_image_link;
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
        //((MainActivity) getActivity()).getMusicPlayer();
    }

    public void updateSongInfo(){
        SimpleTrack track = musicPlayer.getCurrentTrackNotFromPlayer();
        isSongPlaying = musicPlayer.isPlaying();
        isSongPaused = musicPlayer.getIsPaused();

        if(isSongPlaying || isSongPaused){
            txtSongTitle.setText(track.song_name);
            txtSongArtist.setText(track.artist);
            urlString = track.album_image_link;
            setAlbumImage();
            if(isSongPaused)
                btnPlayPause.setText("Play");
            showButtons();

        }else{
            //txtSongTitle.setText(" SONGS CURRENTLY PLAYING");
            txtSongTitle.setVisibility(View.INVISIBLE);
            txtSongArtist.setVisibility(View.INVISIBLE);
            albumCoverView.setVisibility(View.INVISIBLE);
            hideButtons();

        }


        if(musicPlayer.getCurrentQueue().size() <= 0 ){
            txtSongTitle.setVisibility(View.VISIBLE);
            txtSongTitle.setText("No Songs in Queue");
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

    @Override
    public void update(Observable observable, Object change) {
        log.logMessage(TAG, "CURRENTLY PLAYING RECIEVES UPDATE!");
        ChangeType changeType = (ChangeType) change;

        if(changeType == ChangeType.UPDATE_GUI) {
            SimpleTrack current = musicPlayer.getCurrentTrackNotFromPlayer();
            log.logMessage(TAG,"IN CURRENTLY PLAYING UPDATE SONG : " + current.song_name );
            name = current.song_name;
            artist = current.artist;
            urlString = current.album_image_link;
        }

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
        // TODO: Update argument type and song_name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy(){
        musicPlayer.removeObserverFragment(this);
        super.onDestroy();
    }
}

