package com.example.mk.mysmartsns.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mk.mysmartsns.BottomNavigationViewHelper;
import com.example.mk.mysmartsns.Message;
import com.example.mk.mysmartsns.R;
import com.example.mk.mysmartsns.ResumableDownloader;
import com.example.mk.mysmartsns.fragment.fragment__search.HashTagSearchFragment;
import com.example.mk.mysmartsns.fragment.fragment_main.LogFragment;
import com.example.mk.mysmartsns.fragment.fragment_main.MyTimelineFragment;
import com.example.mk.mysmartsns.fragment.fragment_main.PostFragment;
import com.example.mk.mysmartsns.fragment.fragment_main.SearchFragment;
import com.example.mk.mysmartsns.fragment.fragment_main.TimelineFragment;

import java.io.File;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017-02-02.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private android.support.v4.app.Fragment fragment;
    android.support.v4.app.FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;
    private long backKeyPressedTime = 0;

    @Bind(R.id.progress_bar)
    private ProgressBar progressBar;
    @Bind(R.id.progress_bar_tv)
    private TextView percentageTV;
    private AsyncTask asyncTask;
    private String urlStr = "http://114.70.21.116:3001/prefetch/original/dodo.jpg";
    private String filename;
    private String fileExtension;

    public final String PREFS_NAME = "MyResumableDownloadPrefsFile";
    public final String PREFS_KEY_PROGRESS = "Progress";
    public final String PREFS_KEY_LASTMODIFIED = "LastModified";

    private File fileDir;
    private ResumableDownloader mDownloader;

    private boolean asyncTaskFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Butterknife bind
        ButterKnife.bind(this);

        fileDir = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/prefetch");
        initView();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mDownloader = new ResumableDownloader(settings.getString(PREFS_KEY_LASTMODIFIED, " "), mDownloader.PAUSE);
        int progress = settings.getInt(PREFS_KEY_PROGRESS, 0);
        progressBar.setProgress(progress);
        percentageTV.setText(String.format("%1$" + 3 + "s", progress) + "%");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        fragment = fragmentManager.findFragmentById(R.id.frame_layout);
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.action_timeline:
                                if (!fragment.getTag().equals("timeline_fragment")) {
                                    transaction.remove(fragment).replace(R.id.frame_layout, TimelineFragment.newInstance(), "timeline_fragment");
                                    transaction.commit();
                                }
                                break;
                            case R.id.action_search:
                                if(bottomNavigationView.getMenu().getItem(0).isChecked())
                                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                if (!fragment.getTag().equals("search_fragment")) {
//                                    if (fragment.getTag().equals("timeline_fragment")) {
//                                        transaction.addToBackStack(null);
//                                    }
                                    transaction.remove(fragment).replace(R.id.frame_layout, SearchFragment.newInstance(), "search_fragment");
                                    transaction.commit();
                                }
                                break;
                            case R.id.action_post:
                                if(bottomNavigationView.getMenu().getItem(0).isChecked())
                                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                if (!fragment.getTag().equals("post_fragment")) {
//                                    if (fragment.getTag().equals("timeline_fragment")) {
//                                        transaction.addToBackStack(null);
//                                    }
                                    transaction.remove(fragment).replace(R.id.frame_layout, new PostFragment(), "post_fragment");
                                    transaction.commit();
                                }
                                break;
                            case R.id.action_log:
                                if(bottomNavigationView.getMenu().getItem(0).isChecked())
                                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                if (!fragment.getTag().equals("log_fragment")) {
//                                    if (fragment.getTag().equals("timeline_fragment")) {
//                                        transaction.addToBackStack(null);
//                                    }
                                    transaction.remove(fragment).replace(R.id.frame_layout, LogFragment.newInstance(), "log_fragment");
                                    transaction.commit();

                                }
                                break;
                            case R.id.action_mytimeline:
                                if(bottomNavigationView.getMenu().getItem(0).isChecked())
                                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                if (!fragment.getTag().equals("my_timeline_fragment")) {
//                                    if (fragment.getTag().equals("timeline_fragment")) {
//                                        transaction.addToBackStack(null);
//                                    }
                                    transaction.remove(fragment).replace(R.id.frame_layout, MyTimelineFragment.newInstance(), "my_timeline_fragment");
                                    transaction.commit();
                                }
                                break;
                        }

                        return true;
                    }
                }
        );

        // 프레그먼트
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.frame_layout);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frame_layout, TimelineFragment.newInstance(), "timeline_fragment");
        transaction.commit();

        SharedPreferencesCompat settings = getSharedPreferences(PREFS_NAME, 0);

    }
    @Override
    public void onBackPressed(){
//        Toast.makeText(this, fragment.getTag() +", "+ fragmentManager.getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
        if(!fragmentManager.findFragmentById(R.id.frame_layout).getTag().equals("timeline_fragment")){
            if(fragmentManager.findFragmentById(R.id.frame_layout).getTag().equals("nav_search_fragment")
                    || fragmentManager.findFragmentById(R.id.frame_layout).getTag().equals("nav_my_timeline_fragment")){
//                fragmentManager.beginTransaction().setCustomAnimations(R.anim.activity_transition_end_enter, R.anim.activity_transition_end_exit);
                fragmentManager.popBackStack();
                return;
            }
            fragmentManager.beginTransaction().replace(R.id.frame_layout, TimelineFragment.newInstance(),"timeline_fragment")
                    .setCustomAnimations(R.anim.activity_transition_end_enter, R.anim.activity_transition_end_exit).commit();
            Menu menu = bottomNavigationView.getMenu();
            for(int i=0; i<menu.size(); i++) {
                if(i==0)
                    menu.getItem(i).setChecked(true);
                else
                    menu.getItem(i).setChecked(false);
            }
        }
        else{
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "한번더 누르면 종료합니다", Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                Toast.makeText(getApplicationContext(), "한번더 누르면 종료합니다", Toast.LENGTH_SHORT).cancel();
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 888){
            if(resultCode == RESULT_OK){
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layout, MyTimelineFragment.newInstance(), "nav_my_timeline_fragment");
                transaction.addToBackStack("");
                transaction.commit();
            }
        }else if(requestCode == 0){
            if(resultCode == RESULT_OK){
                int position = data.getIntExtra("position", 0);
                int the_number_of_comment = data.getIntExtra("the_number_of_comments", 0);
                fragmentManager = getSupportFragmentManager();
                fragment = fragmentManager.findFragmentById(R.id.frame_layout);
//                ((TimelineFragment)fragment).setDataChanged(position, the_number_of_comment);

                if(fragment instanceof TimelineFragment){
                    ((TimelineFragment)fragment).setDataChanged(position, the_number_of_comment);
                }else if(fragment instanceof HashTagSearchFragment){
                    ((HashTagSearchFragment)fragment).setDataChanged(position, the_number_of_comment);
                }else if(fragment instanceof MyTimelineFragment){
                    ((MyTimelineFragment)fragment).setDataChanged(position, the_number_of_comment);
                }
            }
        }
    }

    private void initView(){

    }

    private void initUrl(){
        String file = urlStr.substring(urlStr.lastIndexOf("/") + 1);
        fileExtension = file.substring(file.lastIndexOf("."));
        filename = file.substring(0, file.lastIndexOf("."));
        Log.d(TAG, "프리페칭테스트 : " + fileExtension + " , " + filename);
    }

    private void startDownload(){
        asyncTaskFinished = false;
        asyncTask = new AsyncTask<URL, Message, ResumableDownloader>(){

            @Override
            protected ResumableDownloader doInBackground(URL... urls) {
                return null;
            }
        }.execute();
    }

}

