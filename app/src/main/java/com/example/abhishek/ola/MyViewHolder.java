package com.example.abhishek.ola;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by abhishek on 11/12/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder{
    TextView songName;
    TextView artist;
    TextView url;
    ImageView cover;
    Button download;
    Button stream;
    Button play;
    ProgressBar progressBar;
    public MyViewHolder(View itemView) {
        super(itemView);
        //we need to initialize these variables so adapter databinder can set the text on thse fields
songName =(TextView) itemView.findViewById(R.id.songName);
artist =(TextView) itemView.findViewById(R.id.artist);
url=(TextView) itemView.findViewById(R.id.url);
cover=(ImageView)itemView.findViewById(R.id.cover);
download=(Button)itemView.findViewById(R.id.download);
stream=(Button)itemView.findViewById(R.id.stream);
play=(Button)itemView.findViewById(R.id.play);
progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar);
    }
}
