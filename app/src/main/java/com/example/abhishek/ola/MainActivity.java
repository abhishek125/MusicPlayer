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

public class MainActivity extends Activity implements MyAdapter.Communicator{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isStoragePermissionGranted();
        if(getFragmentManager().findFragmentByTag("projectListFragment") == null) {
            Fragment fragment = new ProjectListFragment();
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
            Log.v("storage","Permission: "+permissions[0]+ "was "+grantResults[0]);
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
    public void handleStream(String url,String filename) {
        Fragment playerFragment=new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("URL", url);
        args.putString("FILENAME", filename);
        Log.i("urlandname",url+"\t\t"+filename);
        fragmentHelper(playerFragment,args);
    }
    public void fragmentHelper(Fragment playerFragment, Bundle args){
        playerFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout,playerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void handlePlay(String filepath,String songName){
        Fragment playerFragment=new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("PATH", filepath);
        args.putString("FILENAME", songName);
        fragmentHelper(playerFragment,args);
    }
}
