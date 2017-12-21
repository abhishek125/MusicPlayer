package com.example.abhishek.ola;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.abhishek.ola.model.DataObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishek on 11/12/2017.
 */
interface OnTaskCompleted{
    void onTaskCompleted(DataObject[] dataObjects);
}

public class ProjectListFragment extends Fragment implements OnTaskCompleted {
    List<DataObject> list=new ArrayList<DataObject>();
    MyAdapter adapter;

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
    public void onTaskCompleted(DataObject[] dataObjects) {

        if(dataObjects==null)
            return;
        for(DataObject dataObject:dataObjects)
            list.add(dataObject);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //adapter=null;
    }
    private class JsonTask extends AsyncTask<String, Integer, DataObject[]> {
        public OnTaskCompleted onTaskCompleted;
        public JsonTask(OnTaskCompleted onTaskCompleted){

            this.onTaskCompleted=onTaskCompleted;
        }

        protected DataObject[] doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String result=buffer.toString();
                DataObject[] dataObjects=getJsonToObject(result);
                return dataObjects;
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
        protected void onPostExecute(DataObject[] dataObjects) {
            super.onPostExecute(dataObjects);
            onTaskCompleted.onTaskCompleted(dataObjects);

        }
        public DataObject[] getJsonToObject(String json){
            DataObject[] dataObjects=null;
            if (json != null) {
                try {


                    // Getting JSON Array node
                    JSONArray projects =new JSONArray(json);
                    dataObjects=new DataObject[projects.length()];
                    Log.e("no of elements",projects.length()+"");
                    // looping through All Contacts
                    for (int i = 0; i < projects.length(); i++) {
                        JSONObject c = projects.getJSONObject(i);
                        dataObjects[i]=new DataObject();
                        dataObjects[i].setSongName(c.getString("song"));
                        dataObjects[i].setArtist(c.getString("artists"));
                        dataObjects[i].setUrl(c.getString("url"));
                        dataObjects[i].setCover(c.getString("cover_image"));



                    }
                } catch (final JSONException e) {
                    Log.e("json error", "Json parsing error: " + e.getMessage());
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
                Log.e("server error", "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return dataObjects;
        }

    }

}
