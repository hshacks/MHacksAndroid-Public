package com.hshacks.ii.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hshacks.ii.android.chat.ChatFragment;
import com.hshacks.ii.android.concierge.ConciergeFragment;
import com.hshacks.ii.android.login.LoginActivity;
import com.hshacks.ii.android.navigation.NavigationDrawerFragment;
import com.hshacks.ii.android.navigation.NavigationDrawerItem;
import com.hshacks.ii.android.news.NewsFragment;
import com.hshacks.ii.android.schedule.ScheduleFragment;
import com.hshacks.ii.android.workshops.WorkshopFragment;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;



public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnRefreshListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mNavigationDrawerLayout;
    private FrameLayout mFrameLayout;
    private String mTitle;

    private ArrayList<NavigationDrawerItem> mFragments;

    private ParseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpened(getIntent());
        // Check if the user is logged in
        mUser = ParseUser.getCurrentUser();
        if (mUser == null) {
            // The user is not logged in
            finish();
        }

        mFragments = new ArrayList();
        mFragments.add(new NavigationDrawerItem(new NewsFragment(), getString(com.hshacks.ii.android.R.string.title_news), com.hshacks.ii.android.R.drawable.microphone));
        mFragments.add(new NavigationDrawerItem(new ConciergeFragment(), getString(com.hshacks.ii.android.R.string.title_concierge), com.hshacks.ii.android.R.drawable.running));
        mFragments.add(new NavigationDrawerItem(new ChatFragment(), getString(com.hshacks.ii.android.R.string.title_chat), com.hshacks.ii.android.R.drawable.sofa));
        mFragments.add(new NavigationDrawerItem(new ScheduleFragment(), getString(com.hshacks.ii.android.R.string.title_schedule), com.hshacks.ii.android.R.drawable.calendar));
        mFragments.add(new NavigationDrawerItem(new WorkshopFragment(), getString(com.hshacks.ii.android.R.string.title_workshops), com.hshacks.ii.android.R.drawable.lightbulb));

        setContentView(com.hshacks.ii.android.R.layout.activity_main);

        mFrameLayout = (FrameLayout) findViewById(com.hshacks.ii.android.R.id.container);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(com.hshacks.ii.android.R.id.navigation_drawer);
        mNavigationDrawerFragment.setItems(mFragments);
        // Set up the drawer.
        mNavigationDrawerLayout = (DrawerLayout) findViewById(com.hshacks.ii.android.R.id.drawer_layout);
        mNavigationDrawerLayout.setDrawerShadow(com.hshacks.ii.android.R.drawable.drawer_shadow, GravityCompat.END);
        mNavigationDrawerFragment.setUp(com.hshacks.ii.android.R.id.navigation_drawer, mNavigationDrawerLayout);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(com.hshacks.ii.android.R.id.container, mFragments.get(position).fragment, String.valueOf(position))
                .commit();
        onSectionAttached(position);
    }

    public void onSectionAttached(int number) {
        mTitle = mFragments.get(number).title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpTitleFont();
    }

    public void setUpTitleFont() {
        Typeface tf = Typeface.DEFAULT_BOLD;

        try {
            Integer titleId = (Integer) Class.forName("com.android.internal.R$id").getField("action_bar_title").get(null);
            TextView title = (TextView) getWindow().findViewById(titleId);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) title.getLayoutParams();
            title.setTypeface(tf);

            int pixels = 5 * getResources().getDisplayMetrics().densityDpi / 160;
            params.setMargins(pixels, pixels, pixels, pixels);
            title.setLayoutParams(params);
            title.invalidate();
        } catch (Exception e) {
            Log.e("DERP", "Failed to obtain action bar title reference");
        }
    }

    @Override
    public void onRefreshStarted(View view) {
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.hshacks.ii.android.R.menu.top_menu, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case com.hshacks.ii.android.R.id.action_logout: {
                ParseUser.logOut();
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            }

            case com.hshacks.ii.android.R.id.menu_item_share: {
                Intent intent = new Intent();
                intent.setType("text/plain");
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(com.hshacks.ii.android.R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, getString(com.hshacks.ii.android.R.string.share_string));
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void hellYeah() {
        if (mFragments.get(2).fragment instanceof ChatFragment) {
            ChatFragment fragment = (ChatFragment) mFragments.get(2).fragment;
            fragment.putDave();
        }
    }

}
