package com.example.abhishek.ola;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.example.abhishek.ola.model.DataObject;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by abhishek on 11/12/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    LayoutInflater inflater;
    List<DataObject> data;
    Context context;
    Communicator communicator;

    interface Communicator{
        public void handleStream(String url,String filename);
        public void handlePlay(String path,String filename);
    }
    public MyAdapter(Context context, List<DataObject> data){
        inflater=LayoutInflater.from(context);
        this.data=data;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.custum_row,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DataObject dataObject = data.get(position);
        holder.songName.setText(dataObject.getSongName());
        holder.artist.setText("artist: " + dataObject.getArtist());
        holder.url.setText("url:" + dataObject.getUrl() + "");
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                holder.cover.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dhoni));
            }
        });
        builder.downloader(new OkHttpDownloader(context));
        builder.build().load(dataObject.getCover()).into(holder.cover);

        final String path=getDirPath();
        if(path==null){
            Toast.makeText(context,"please grant storage permission",Toast.LENGTH_LONG).show();
            return ;
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Communicator)context).handlePlay(path + "/" + dataObject.getSongName() + ".mp3",dataObject.getSongName());
            }
        });
        if ((new File(path + "/" + dataObject.getSongName() + ".mp3")).exists()){
            changeUI(holder);
            Log.e("file_exist",path + "/" + dataObject.getSongName() + ".mp3");
            return;
        }

    //        getRealURL(dataObject.getUrl());

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(dataObject.getUrl(),dataObject.getSongName()+".mp3",path,holder);
            }
        });
        holder.stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Communicator)context).handleStream(dataObject.getUrl(),dataObject.getSongName());
            }
        });



    }
    public static String getRealURL(String url) {
        try {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();
        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location");
            Log.i("redirectedurl",redirectUrl);
            return getRealURL(redirectUrl);
        }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("fatalError","error occured");
        }
        return url;
    }
    public void changeUI(MyViewHolder holder){
            holder.download.setVisibility(View.GONE);
            holder.stream.setVisibility(View.GONE);
            holder.play.setVisibility(View.VISIBLE);
            Log.e("calledas",holder.songName.toString());
        return;
    }
    public  boolean isStorageGranted(){
        return ((MainActivity)context).isStoragePermissionGranted();
    }
    public String getDirPath(){
        boolean storage=isStorageGranted();
        boolean b=true;
        if(storage) {
            File file=new File(Environment.getExternalStorageDirectory().toString()+"/ola");
            if(!file.exists())
                 b=file.mkdirs();
            return b==true?Environment.getExternalStorageDirectory().toString() + "/ola":null;
        }
        else
            return null;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void download(String url, String fileName, String dirPath,final MyViewHolder holder){

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();

        PRDownloader.initialize(context.getApplicationContext(), config);
        int downloadId = PRDownloader.download(url, dirPath, fileName)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                holder.progressBar.setVisibility(View.VISIBLE);
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                    holder.progressBar.setProgress((int)(((float)progress.currentBytes/progress.totalBytes)*100));
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                    holder.progressBar.setVisibility(View.GONE);
                                    changeUI(holder);
                            }

                            @Override
                            public void onError(Error error) {
                                holder.progressBar.setVisibility(View.GONE);
                                Toast.makeText(context, "Download error", Toast.LENGTH_LONG).show();
                            }


                        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error in image", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
