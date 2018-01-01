package com.example.abhishek.ola;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.example.abhishek.ola.model.Song;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements Filterable {
    private static final String FILEERROR = "file_exist";
    private LayoutInflater inflater;
    private List<Song> songs;
    private List<Song> filteredSongs;
    private Context context;
    private SearchFilter searchFilter;
    private Communicator communicator;

    @Override
    public Filter getFilter() {
        if(searchFilter == null)
            searchFilter = new SearchFilter(this, songs);
        return searchFilter;
    }

    interface Communicator{
         void handleStream(String url,String filename);
         void handlePlay(String path,String filename);
    }
    MyAdapter(Context context, List<Song> songs){
        inflater=LayoutInflater.from(context);
        this.songs = songs;
        this.context=context;
        communicator= (Communicator) context;
        this.filteredSongs= songs;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.custum_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Song song = filteredSongs.get(position);
        holder.songName.setText(song.getSongName());
        holder.artist.setText(context.getString(R.string.ARTIST_NAME) + song.getArtist());
        holder.url.setText(context.getString(R.string.URL) + song.getUrl() + "");
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                holder.cover.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dhoni));
            }
        });
        builder.downloader(new OkHttpDownloader(context));
        builder.build().load(song.getCover()).into(holder.cover);

        final String path=getDirPath();
        if(path==null){
            Toast.makeText(context,"please grant storage permission",Toast.LENGTH_LONG).show();
            return ;
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.handlePlay(path + "/" + song.getSongName() + ".mp3", song.getSongName());
            }
        });
        if ((new File(path + "/" + song.getSongName() + ".mp3")).exists()){
            changeUI(holder);
            Log.e(FILEERROR,path + "/" + song.getSongName() + ".mp3");
            return;
        }

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(song.getUrl(), song.getSongName()+".mp3",path,holder);
            }
        });
        holder.stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.handleStream(song.getUrl(), song.getSongName());
            }
        });



    }
    private void changeUI(MyViewHolder holder){
            holder.download.setVisibility(View.GONE);
            holder.stream.setVisibility(View.GONE);
            holder.play.setVisibility(View.VISIBLE);

    }
    private   boolean isStorageGranted(){
        return ((MainActivity)context).isStoragePermissionGranted();
    }
    private String getDirPath(){
        boolean storage=isStorageGranted();
        boolean b=true;
        if(storage) {
            File file=new File(Environment.getExternalStorageDirectory().toString()+"/ola");
            if(!file.exists())
                 b=file.mkdirs();
            return b?Environment.getExternalStorageDirectory().toString() + "/ola":null;
        }
        else
            return null;
    }


    @Override
    public int getItemCount() {
        return filteredSongs.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void download(String url, String fileName, String dirPath,final MyViewHolder holder){

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();

        PRDownloader.initialize(context.getApplicationContext(), config);
        PRDownloader.download(url, dirPath, fileName)
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


}
