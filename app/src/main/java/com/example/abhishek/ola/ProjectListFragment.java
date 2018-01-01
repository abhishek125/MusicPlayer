package com.example.abhishek.ola;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.abhishek.ola.model.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

interface OnTaskCompleted{
    void onTaskCompleted(Song[] songs);
}

public class ProjectListFragment extends Fragment implements OnTaskCompleted {
    private static final String ERRORINHTTP ="json error" ;
    private List<Song> list= new ArrayList<>();
    private MyAdapter adapter;

    public List<Song> getList() {
        return list;
    }

    public MyAdapter getAdapter() {
        return adapter;
    }

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setRetainInstance(true);
        new ProjectListFragment.JsonTask(this).execute("http://starlord.hackerearth.com/studio");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View v=inflater.inflate(R.layout.fragment,viewGroup,false);
        RecyclerView recyclerView=(RecyclerView) v.findViewById(R.id.recyclerView);
        adapter=new MyAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onTaskCompleted(Song[] songs) {
        //some error occured
        if(songs ==null)
            return;
        Collections.addAll(list, songs);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //adapter=null;
    }
    private  class JsonTask extends AsyncTask<String, Integer, Song[]> {
         OnTaskCompleted onTaskCompleted;
         JsonTask(OnTaskCompleted onTaskCompleted){
            this.onTaskCompleted=onTaskCompleted;
        }

        protected Song[] doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String result=buffer.toString();
                return getJsonToObject(result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }



        @Override
        protected void onPostExecute(Song[] songs) {
            super.onPostExecute(songs);
            onTaskCompleted.onTaskCompleted(songs);

        }
        Song[] getJsonToObject(String json){
            Song[] songs =null;
            if (json != null) {
                try {


                    // Getting JSON Array node
                    JSONArray projects =new JSONArray(json);
                    songs =new Song[projects.length()];
                    // looping through All Contacts
                    for (int i = 0; i < projects.length(); i++) {
                        JSONObject c = projects.getJSONObject(i);
                        songs[i]=new Song();
                        songs[i].setSongName(c.getString("song"));
                        songs[i].setArtist(c.getString("artists"));
                        songs[i].setUrl(c.getString("url"));
                        songs[i].setCover(c.getString("cover_image"));



                    }
                } catch (final JSONException e) {
                    Log.e(ERRORINHTTP, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(ERRORINHTTP, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();

                    }
                });
            }

            return songs;
        }

    }

}
