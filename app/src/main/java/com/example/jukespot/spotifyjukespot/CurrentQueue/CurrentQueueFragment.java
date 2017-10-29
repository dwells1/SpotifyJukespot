package com.example.jukespot.spotifyjukespot.CurrentQueue;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.CurrentlyPlaying.CurrentlyPlayingFragment;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
import com.example.jukespot.spotifyjukespot.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentQueueFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentQueueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentQueueFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = CurrentQueueFragment.class.getSimpleName();
    private Logging log;
    private TextView queueHeader;
    private Button btnWhichQueue;
    private ListView currentQueueList;
    private MusicPlayer musicPlayer;

    private View view;

    public CurrentQueueFragment() {
        // Required empty public constructor
    }


    public static CurrentQueueFragment newInstance(String param1, String param2) {
        CurrentQueueFragment fragment = new CurrentQueueFragment();
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
        view = inflater.inflate(R.layout.fragment_current_queue, container, false);
        initButton();
        musicPlayer = ((MainActivity)getActivity()).getMusicPlayer();
        initListView();
        return view;

    }
    public void initButton(){
        btnWhichQueue = view.findViewById(R.id.btnWhichQueue);
        btnWhichQueue.setOnClickListener(this);
    }

    public void initListView(){
       // List<Track>
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onClick(View view) {
        log.logMessage(TAG, "pressed button");
        switch(view.getId()){
            case R.id.btnWhichQueue:
                if(btnWhichQueue.getText().equals("Display Previously Played")){
                    log.logMessage(TAG,"Display Previously Played pressed");
                    btnWhichQueue.setText("Display Current Queue");
                }else if(btnWhichQueue.getText().equals("Display Current Queue")){
                    log.logMessage(TAG,"Display Current Queue pressed");
                    btnWhichQueue.setText("Display Previously Played");
                }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
