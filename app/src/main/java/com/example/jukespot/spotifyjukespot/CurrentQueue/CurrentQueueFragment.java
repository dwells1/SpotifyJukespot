package com.example.jukespot.spotifyjukespot.CurrentQueue;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Enums.QueueType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
import com.example.jukespot.spotifyjukespot.R;

import java.util.List;


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
    private User user;

    private TextView queueHeader;
    private Button btnWhichQueue;
    private ListView queueList;
    private MusicPlayer musicPlayer;
    private PopupMenu songPopUp;
    private SimpleTrack trackChosen;
    private QueueType currentQueueType;
    private View view;
    private List <SimpleTrack> currentQueue;


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
        user = User.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_current_queue, container, false);
        queueHeader = view.findViewById(R.id.queueHeader);
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
        updateList(QueueType.CURRENT_QUEUE);
    }

    public void updateList(QueueType qType){
        currentQueueType = qType;
        if(qType == QueueType.CURRENT_QUEUE){
            currentQueue = musicPlayer.getCurrentQueue();
        }else if (qType == QueueType.PREV_QUEUE){
            currentQueue = musicPlayer.getPrevQueue();
        }
        if(currentQueue.isEmpty()){
            log.logMessage(TAG, "***Queue empty***");
        }
        QueueListAdapter qAdapter = new QueueListAdapter(getActivity(), currentQueue);
        queueList = view.findViewById(R.id.queueList);
        queueList.setAdapter(qAdapter);
        queueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                trackChosen = (SimpleTrack) queueList.getItemAtPosition(position);
                log.logMessage(TAG, "Item Clicked: " + trackChosen.song_name + " by " + trackChosen.artist);
                showPopUp(view);
            }
        });
    }

    public void showPopUp(View anchor){
        songPopUp = new PopupMenu(this.getActivity(), anchor);
        songPopUp.getMenuInflater().inflate(R.menu.song_pressed_menu, songPopUp.getMenu());
        songPopUp.getMenu().add("Remove From Queue");
        /* check user's permissions */
        validateUserPermissions();

        songPopUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuOption) {
                String itemChosen = menuOption.getTitle().toString();
                /*check user menu choices*/
                switch(itemChosen){
                    case "Remove From Queue":
                        /*TODO: add functionality to remove from queue*/
                        musicPlayer.removeFromQueue(trackChosen);
                        updateList(currentQueueType);
                        log.logMessage(TAG, "Pressed in Popup:" + menuOption.getTitle() + " for " + trackChosen.song_name);
                        break;
                    case "Play Now":
                        musicPlayer.queueAtPosition(0, trackChosen);
                        updateList(currentQueueType);
                        log.logMessage(TAG, "Pressed in Popup:" + menuOption.getTitle() + " for " + trackChosen.song_name);
                        break;
                    case "Add to Queue":
                        musicPlayer.queue(trackChosen);
                        updateList(currentQueueType);
                        log.logMessage(TAG, "Pressed in Popup:" + menuOption.getTitle() + " for " + trackChosen.song_name);
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
        songPopUp.show();
    }
    public void validateUserPermissions(){
        switch(user.getUserPermissions()){
            case CAN_EDIT_NO_PLAY:
                songPopUp.getMenu().findItem(0).setVisible(true);
                songPopUp.getMenu().findItem(0).setVisible(false);
                songPopUp.getMenu().findItem(0).setVisible(true);
                break;
            case CAN_PLAY_NO_EDIT:
                songPopUp.getMenu().findItem(0).setVisible(false);
                songPopUp.getMenu().findItem(0).setVisible(true);
                songPopUp.getMenu().findItem(0).setVisible(false);
                break;
            case CAN_PLAY_AND_EDIT:
                songPopUp.getMenu().findItem(0).setVisible(true);
                songPopUp.getMenu().findItem(0).setVisible(true);
                songPopUp.getMenu().findItem(0).setVisible(true);
                break;
            case NO_EDIT_NO_PLAY:
                songPopUp.getMenu().findItem(0).setVisible(false);
                songPopUp.getMenu().findItem(0).setVisible(false);
                songPopUp.getMenu().findItem(0).setVisible(false);
                break;
        }

        if(currentQueueType.equals(QueueType.PREV_QUEUE))
            songPopUp.getMenu().getItem(0).setVisible(false);
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
                    queueHeader.setText("Previously Played");
                    btnWhichQueue.setText("Display Current Queue");
                    updateList(QueueType.PREV_QUEUE);

                }else if(btnWhichQueue.getText().equals("Display Current Queue")){
                    log.logMessage(TAG,"Display Current Queue pressed");
                    queueHeader.setText("Current Queue");
                    btnWhichQueue.setText("Display Previously Played");
                    updateList(QueueType.CURRENT_QUEUE);
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
        // TODO: Update argument type and song_name
        void onFragmentInteraction(Uri uri);
    }
}
