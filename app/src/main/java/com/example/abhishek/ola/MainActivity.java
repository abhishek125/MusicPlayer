package com.example.abhishek.ola;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

public class MainActivity extends Activity implements MyAdapter.Communicator{

    private SearchView searchView;
    private final String TAG="STORAGEGRANTED";
    private ProjectListFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getFragmentManager().findFragmentByTag("projectListFragment") == null) {
            fragment = new ProjectListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.relativeLayout, fragment, "projectListFragment");
            transaction.commit();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem=menu.findItem(R.id.searchview);
        searchView= (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                MyAdapter myAdapter= fragment.getAdapter();
                myAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                MyAdapter myAdapter= fragment.getAdapter();
                myAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
    @Override
    public void handleStream(String url,String filename) {
        Fragment playerFragment=new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("URL", url);
        args.putString("FILENAME", filename);
        fragmentHelper(playerFragment,args);
    }
    public void fragmentHelper(Fragment fragment, Bundle args){
        fragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void switchFragment(){
        onCreate(null);
    }
    public void handlePlay(String filepath,String songName){
        Fragment playerFragment=new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("PATH", filepath);
        args.putString("FILENAME", songName);
        fragmentHelper(playerFragment,args);
    }
}
