package com.example.jukespot.spotifyjukespot;

import android.net.Uri;
/* TODO:WHEN MAKING A NEW FRAGMENT MAKE SURE THIS VERSION OF FRAGMENT IS IMPORTED IN THAT FILE* */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*TODO: When Adding new Fragments you have to implement them as the ones here*/
public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener, CurrentQueueFragment.OnFragmentInteractionListener {
    /*Drawer Navigation*/
    private ActionBarDrawerToggle menuDrawerToggle;
    private String currentSelectionFromMenuTitle;
    private String[] mainUserOptionsForDrawer;
    private String currentActivityTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> menuAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Functions for Navigable Menu*/
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        currentActivityTitle = getTitle().toString();
        addItemsToDrawerMenu();
        setupDrawerMenu();
        setFirstFragment();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void addItemsToDrawerMenu() {
        mainUserOptionsForDrawer = new String[]{"Search", "Current Queue",
                "Currently Playing", "End Current Jukebox", "Logout"};

        menuAdaptor = new ArrayAdapter<String>(this,
                R.layout.drawer_options_list_layout, mainUserOptionsForDrawer);

        mDrawerList.setAdapter(menuAdaptor);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSelectionFromMenuTitle = ((TextView) view.findViewById(R.id.itemChosen)).getText().toString();
                // Toast.makeText(MainActivity.this, currentSelectionFromMenuTitle, Toast.LENGTH_SHORT).show();
                selectMenuItem(position);
            }
        });
    }

    /*TODO: add detection so user cannot press same item twice and just reload*/
    public void selectMenuItem(int position) {
        Fragment currentFrag = null;
        boolean isFragmentNeeded = true;

        /*TODO: Create Fragments for other menu options except maybe logout*/
        if (currentSelectionFromMenuTitle.equals("Search")) {
            currentFrag = new SearchFragment();

        } else if (currentSelectionFromMenuTitle.equals("Current Queue")) {
            currentFrag = new CurrentQueueFragment();
        } else if (currentSelectionFromMenuTitle.equals("End Current Jukebox")) {
            /*TODO: Add Alert so user confirms ending jukebox*/
            Toast.makeText(this, "Jukebox Ended", Toast.LENGTH_SHORT).show();
            Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
            startActivity(jukeboxOptionsIntent);
            finish();
        } else if (currentSelectionFromMenuTitle.equals("Logout")) {
            /*TODO: Should this log them out of spotify??*/
            Toast.makeText(this, "Logout Successfull", Toast.LENGTH_SHORT).show();
            Intent jukeboxLoginIntent = new Intent(this, Login.class);
            startActivity(jukeboxLoginIntent);
            finish();
        }
        if (currentFrag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTransaction = null;
            try {
                fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.replace(R.id.content_frame, currentFrag);
                fragTransaction.commit();
            } catch (Exception FragNotFound) {
                FragNotFound.printStackTrace();
            }
            currentActivityTitle = currentSelectionFromMenuTitle;
            mDrawerLayout.closeDrawer(mDrawerList);
        }


    }

    public void setupDrawerMenu() {
        menuDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(currentActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        menuDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(menuDrawerToggle);
    }

    public void setFirstFragment() {
        Fragment currentFrag = new SearchFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = null;
        try {
            fragTransaction = fragmentManager.beginTransaction();
            fragTransaction.replace(R.id.content_frame, currentFrag);
            fragTransaction.commit();
        } catch (Exception FragNotFound) {
            FragNotFound.printStackTrace();
        }

        currentActivityTitle = "Search";
        setTitle(currentActivityTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        menuDrawerToggle.syncState();
    }

    /*TODO: Check what this does is it needed for loading more complex Fragments??*/
    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if (menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(mDrawerList);
                return true;
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}