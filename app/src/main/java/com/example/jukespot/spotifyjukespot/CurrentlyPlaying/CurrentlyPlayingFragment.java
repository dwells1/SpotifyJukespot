package com.example.jukespot.spotifyjukespot.CurrentlyPlaying;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.example.jukespot.spotifyjukespot.R;
import com.spotify.sdk.android.player.SpotifyPlayer;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Logging log;
    private TextView txtSongTitle;
    private TextView txtSongArtist;
    private Button btnPlay;
    private Button btnPause;
    private Button btnNext;
    private Button btnPrev;

    private boolean isSongPaused;
    private boolean isSongPlaying = false;
    private String name;
    private String artist;

    View view;
    public CurrentlyPlayingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentlyPlayingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentlyPlayingFragment newInstance(String param1, String param2) {
        CurrentlyPlayingFragment fragment = new CurrentlyPlayingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        log = new Logging();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_currently_playing, container, false);
        initButtons();
        initTextViews();

        isSongPlaying = ((MainActivity)getActivity()).isSongPlaying();

        if(isSongPlaying){
            name = ((MainActivity) getActivity()).getCurrentTrackName();
            artist = ((MainActivity) getActivity()).getCurrentTrackArtist();
            updateSongInfo();
        }else{
            updateSongInfo();
        }


        return view;
    }
    public void updateSongInfo(){
        if(isSongPlaying){
            txtSongTitle.setText(name);
            txtSongArtist.setText(artist);
            showButtons();

        }else{
            txtSongTitle.setText("NO SONGS CURRENTLY PLAYING");
            txtSongArtist.setVisibility(View.INVISIBLE);
            hideButtons();

        }
    }

    public void initButtons(){
        btnPlay = view.findViewById(R.id.btnPlaySong);
        btnPlay.setOnClickListener(this);

        btnPause = view.findViewById(R.id.btnPauseSong);
        btnPause.setOnClickListener(this);

        btnNext = view.findViewById(R.id.btnNextSong);
        btnNext.setOnClickListener(this);

        btnPrev = view.findViewById(R.id.btnPrevSong);
        btnPrev.setOnClickListener(this);
    }

    public void initTextViews(){
        txtSongArtist = view.findViewById(R.id.txtSongArtist);
        txtSongTitle = view.findViewById(R.id.txtSongTitle);
    }

    public void showButtons(){
        btnPlay.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnPrev.setVisibility(View.VISIBLE);
    }
    public void hideButtons(){
        btnPlay.setVisibility(View.INVISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        btnPrev.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onClick(View view){
        log.logMessage(TAG, "PRESSED: " + view.getResources().getResourceName(view.getId()));
        boolean isSongPlaying = ((MainActivity)getActivity()).isSongPlaying();
        switch(view.getId()){
            case R.id.btnPlaySong:
                if(isSongPlaying == false && isSongPaused == true){
                    ((MainActivity)getActivity()).resumeSong();
                    btnPlay.setText("Play");
                    isSongPaused = false;
                }
                break;
            case R.id.btnPauseSong:
                if(((MainActivity)getActivity()).isSongPlaying()){
                    ((MainActivity)getActivity())
                            .pauseSong();
                    btnPlay.setText("Resume");
                    isSongPaused = true;
                }
                break;
            case R.id.btnNextSong:
                name = ((MainActivity)getActivity()).getMusicPlayer().getNextTrack().name;
                artist = ((MainActivity)getActivity()).getMusicPlayer().getNextTrack().artistName;
                ((MainActivity)getActivity()).nextSong();
                if(name != null || artist != null){
                    updateSongInfo();
                }
                break;

            case R.id.btnPrevSong:
                ((MainActivity)getActivity()).prevSong();
                name = ((MainActivity)getActivity()).getMusicPlayer().getPrevTrack().name;
                artist = ((MainActivity)getActivity()).getMusicPlayer().getPrevTrack().artistName;
                if(name != null || artist != null){
                    updateSongInfo();
                }

                break;
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
