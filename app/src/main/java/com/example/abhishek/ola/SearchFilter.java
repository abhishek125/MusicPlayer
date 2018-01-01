package com.example.abhishek.ola;

import android.widget.Filter;

import com.example.abhishek.ola.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishek on 12/27/2017.
 */

public class SearchFilter extends Filter {
    private final MyAdapter adapter;

    private final List<Song> originalList;

    private final List<Song> filteredList;

    public SearchFilter(MyAdapter adapter, List<Song> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = new ArrayList<>(originalList);
        this.filteredList = new ArrayList<>(originalList);
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();

            for (final Song song : originalList) {
                if ((song.getSongName().toLowerCase().contains(filterPattern))) {
                    filteredList.add(song);
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.filteredSongs.clear();
        adapter.filteredSongs.addAll((ArrayList<Song>) results.values);
        adapter.notifyDataSetChanged();
    }
}
