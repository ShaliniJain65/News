package com.shalinijain.news_gateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final String ACTION_NEWS_STORY = "The News_Story";
   // private final String ARTICLE_LIST = "Article List";
    private final String ACTION_MSG_TO_SVC = "Message Service 2";
    private static  final String DATA_OBJ="DATA OBJ";

    private final String ARTICLE_LIST="Article List";
    //For View Pager and Adapter
    private PagerAdapter mypageadapter;
    private ViewPager mviewPager;
    private List<Fragment> fragments;
   // DataofSource current_Source=new DataofSource();

    private PlaceHolderArticle placeHolderArticle;

    //For Drawer Layout
    private Menu drawer_categor_list_menu;
    private DrawerLayout mydrawerlayout;
    private HashMap<String, DataofSource> sourcedata = new HashMap<>();
    private ArrayList<DataofSource> source_id_list = new ArrayList<>();
    private ArrayList<DataofArticle> article_list=new ArrayList<>();
    private ArrayList<String> source_name = new ArrayList<>();
    private ArrayList<String> category_name;
    private ListView mydrawerlist;
    private ActionBarDrawerToggle mydrawertoggle;
    NewsSourceDownloader nsd;
    NewsReceiver receiver;
    String current_News_source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        receiver = new NewsReceiver();
        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(receiver, filter1);



        //Starting the service for use


        mydrawerlayout = findViewById(R.id.drawer_layout);
        mydrawerlist = findViewById(R.id.drawer_list);

        mydrawerlist.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_item_view,source_name));


        mydrawerlist.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                         //mviewPager.setBackgroundColor(null);
                        if(!source_name.isEmpty()) {
                            current_News_source = source_name.get(position);
                            Intent intent = new Intent();
                            intent.setAction(ACTION_MSG_TO_SVC);
                            if (sourcedata.containsValue(current_News_source)) {
                                DataofSource dataobj = sourcedata.get(current_News_source);
                                intent.putExtra(DATA_OBJ, dataobj);
                                sendBroadcast(intent);
                                selectItem(position);
                            }
                        }
                         mydrawerlayout.closeDrawer(mydrawerlayout);
                        reDoFragments(position,article_list);
                    }
                }
        );
        mydrawertoggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mydrawerlayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = getFragments();
        mypageadapter = new PagerAdapter(getSupportFragmentManager());

        mviewPager = (ViewPager)findViewById(R.id.view_pager);
        mviewPager.setAdapter(mypageadapter);
        NewsSourceDownloader source_downloader = new NewsSourceDownloader(this, "");
        source_downloader.execute();
    }

    private void selectItem(int position) {

        //set the Txtviews here
        //Toast.makeText(this, items.get(position), Toast.LENGTH_SHORT).show();
        setTitle(source_name.get(position));
        //reDoFragments(position);
        mydrawerlayout.closeDrawer(mydrawerlist);
    }

    //This function discards the current fragment and replace it with the new category selected
    private void reDoFragments(int position,ArrayList<DataofArticle> data_list) {

        for (int i = 0; i < mypageadapter.getCount(); i++)
            mypageadapter.notifyChangeInPosition(i);
        fragments.clear();

        for(int i=0;i<data_list.size();i++) {
            DataofArticle obj = data_list.get(i);

            fragments.add(PlaceHolderArticle.newInstance(data_list.size(), obj));

        }
       //Notify the chnage in the data
        mypageadapter.notifyDataSetChanged();

        //Set again the start point ot the zero.
        mviewPager.setCurrentItem(0);

    }
    // You need the 2 below to make the drawer-toggle work properly:

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mydrawertoggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mydrawertoggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_source_list, menu);
        drawer_categor_list_menu = menu;
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mydrawertoggle.onOptionsItemSelected(item)) {
            return true;
        }
        String name=item.getTitle().toString();
        new NewsSourceDownloader(this,name);
        nsd.execute();

        mydrawerlayout.openDrawer(mydrawerlist);
        return super.onOptionsItemSelected(item);

    }
    protected void onDestroy() {
        unregisterReceiver(receiver);
        Intent intent = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent);
        super.onDestroy();
    }
    public void setSources(ArrayList<DataofSource> sourcesData_list, ArrayList<String> categories_list) {

        if(categories_list==null || sourcesData_list==null)
        {return;
        }
        sourcedata.clear();
        source_name.clear();

        //Fill in the class object
        source_id_list.addAll(sourcesData_list);

        for(int i=0;i<sourcesData_list.size();i++)
        {
            //Fill in the List array
            source_name.add(sourcesData_list.get(i).getSource_name());

            //Fill in tha HashMap with the value and its data
            sourcedata.put(sourcesData_list.get(i).getSource_name(),sourcesData_list.get(i));
        }

        //Adding the category list into the menu item view.
        if(category_name==null)
        {
            category_name=new ArrayList<>();
            category_name.add("ALL");
            category_name.addAll(categories_list);
            Collections.sort(category_name);
            for(String m:category_name)
            {
                drawer_categor_list_menu.add(m);
            }

        }
        ((ArrayAdapter) mydrawerlist.getAdapter()).notifyDataSetChanged();

    }

    public List<Fragment> getFragments() {
        List<Fragment> frag_List = new ArrayList<Fragment>();
        return frag_List;
    }

    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch(intent.getAction())
            {
                case ACTION_NEWS_STORY:

                    //Get the Article list from the intent's extras
                    if (intent.hasExtra("storylist")) {
                        article_list.clear();
                        ArrayList<DataofArticle> data=new ArrayList<>();
                        data =(ArrayList<DataofArticle>) intent.getSerializableExtra("storylist");
                        article_list.addAll(data);
                        Log.d(TAG, "onReceive: "+article_list.size());
                        break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //This class is used to handle the fragments and its movement back  and forth.
    public class PagerAdapter extends FragmentPagerAdapter {

        private long baseId = 0;
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show total pages. This is hard-coded but often is the size of a collection.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "SECTION " + position;
        }
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }


     // A placeholder fragment containing a simple view.

}
