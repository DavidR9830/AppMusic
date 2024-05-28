package com.david.appmusic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrackRVAdapter extends RecyclerView.Adapter<TrackRVAdapter.ViewHolder> {

    private ArrayList<TrackRVModal> trackRVModals;
    private Context context;


    public TrackRVAdapter(ArrayList<TrackRVModal> trackRVModals, Context context) {
        this.trackRVModals = trackRVModals;
        this.context = context;
    }

    @NonNull
    @Override
    public TrackRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackRVAdapter.ViewHolder holder, int position) {

        TrackRVModal trackRVModal = trackRVModals.get(position);
        holder.trackNameTV.setText(trackRVModal.getTrackName());
        holder.trackArtistTV.setText(trackRVModal.getTrackArtist());
        // adding click listener for track item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackUrl = "https://open.spotify.com/track/" + trackRVModal.getId();
                Uri uri = Uri.parse(trackUrl); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackRVModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView trackNameTV, trackArtistTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackNameTV = itemView.findViewById(R.id.idTVTrackName);
            trackArtistTV = itemView.findViewById(R.id.idTVTrackArtist);
        }
    }
}
