package com.example.jukespot.spotifyjukespot.Search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
import com.example.jukespot.spotifyjukespot.R;
import com.example.jukespot.spotifyjukespot.ResultListScrollListener;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements Search.View{
    // TODO: Rename parameter arguments, choose names that match
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    private Logging log;
    private static final String TAG = SearchFragment.class.getSimpleName();
    private User user;

    private OnFragmentInteractionListener mListener;
    private Search.ActionListener mActionListener;
    private SearchResultsAdapter mAdapter;
    public View view;

    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
    private ScrollListener mScrollListener = new ScrollListener(mLayoutManager);


    private PopupMenu songPopUp;
    /*song pressed info*/
    private Track trackChosenInSearch;
    private SimpleTrack trackConverted;
    private String trackName;
    private String trackArtist;
    private MusicPlayer musicPlayer;
    private ServicesGateway gateway;

    public SearchFragment() {
        // Required empty public constructor
    }

    private class ScrollListener extends ResultListScrollListener {

        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore() {
            mActionListener.loadMoreResults();
        }
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();
        gateway = ServicesGateway.getInstance();

        log = new Logging();
        musicPlayer = ((MainActivity)getActivity()).getMusicPlayer();
        log.logMessage(TAG,user.getPassword());
        log.logMessage(TAG,user.getUserName());
        log.logMessage(TAG,user.getSessionToken());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search2, container, false);
        mActionListener = new SearchPresenter(this.getActivity(), this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
           // log.logMessage(TAG,"Pressed SONG");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mActionListener = new SearchPresenter(this.getActivity(), this);
        Intent intent = getActivity().getIntent();
        String token = intent.getStringExtra(EXTRA_TOKEN);
        mActionListener.init(token);
        final android.widget.SearchView searchView = (android.widget.SearchView) view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                log.logMessage(TAG,"Search Submit " + query);
                mActionListener.search(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Setup search results list
        mAdapter = new SearchResultsAdapter(this.getActivity(), new SearchResultsAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {
                mActionListener.selectTrack(item);
                /*set song variables song_name, artist, and song itself*/
                TextView songTitleView = (TextView) itemView.findViewById(R.id.entity_title);
                TextView artistView = (TextView) itemView.findViewById(R.id.entity_subtitle);
                trackChosenInSearch = item;
                trackConverted = convertToSimpleTrack(trackChosenInSearch);
                trackArtist = artistView.getText().toString();
                trackName = songTitleView.getText().toString();
                log.logMessage(TAG,"Pressed Song From Search: " + trackName +" by "+ trackArtist);
                showPopUp(itemView);
            }
        });

        RecyclerView resultsList = (RecyclerView) view.findViewById(R.id.search_results);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(mLayoutManager);
        resultsList.setAdapter(mAdapter);
        resultsList.addOnScrollListener(mScrollListener);

        // If Activity was recreated wit active search restore it
        if (savedInstanceState != null) {
            String currentQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mActionListener.search(currentQuery);
        }

    }

    public SimpleTrack convertToSimpleTrack(Track toConvert){
        if(toConvert == null){
            log.logMessage(TAG, "Error Converting Track to SimpleTrack");
            return null;
        }

        SimpleTrack simpleTrack = new SimpleTrack(toConvert.name,
                toConvert.artists.get(0).name, toConvert.uri, toConvert.album.images.get(0).url);
        return simpleTrack;
    }
    public void showPopUp(View anchor){
        songPopUp = new PopupMenu(this.getActivity(), anchor);
        songPopUp.getMenuInflater().inflate(R.menu.song_pressed_menu, songPopUp.getMenu());
        songPopUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String itemChosen = item.getTitle().toString();
                switch(itemChosen){
                    case "Add to Queue":
                        if(musicPlayer.getQueue().isEmpty()){
                            musicPlayer.queueAtPosition(0,trackConverted);
                            //gateway.modifySongPlaylist(getActivity(),trackConverted);
                            //musicPlayer.play(trackChosenInSearch);
                        }else{
                            musicPlayer.queue(trackConverted);
                            //gateway.modifySongPlaylist(getActivity(),trackConverted);
                        }

                        log.logMessage(TAG, "Pressed in Popup:" + item.getTitle() + " for " + trackName);
                        break;
                    case "Play Now":
                        musicPlayer.queueAtPosition(0, trackConverted);
                        //gateway.modifySongPlaylist(getActivity(),trackConverted);
                        log.logMessage(TAG, "Pressed in Popup:" + item.getTitle() + " for " + trackName);
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
        songPopUp.show();
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
        log.logMessage(TAG,"saving state");
    }

    @Override
    public void reset() {
        mScrollListener.reset();
        mAdapter.clearData();
    }

    @Override
    public void onSaveInstanceState(Bundle saveState){
        super.onSaveInstanceState(saveState);
    }

    @Override
    public void addData(List<Track> items) {
        mAdapter.addData(items);
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
