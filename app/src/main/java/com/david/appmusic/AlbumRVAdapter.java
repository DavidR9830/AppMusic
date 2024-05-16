package com.david.appmusic;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumRVAdapter extends RecyclerView.Adapter<AlbumRVAdapter.ViewHolder> {
    // creating variables for array list and context
    private ArrayList<AlbumRVModal> albumRVModalArrayList;
    private Context context;


    public AlbumRVAdapter(ArrayList<AlbumRVModal> albumRVModalArrayList, Context context) {
        this.albumRVModalArrayList = albumRVModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumRVAdapter.ViewHolder holder, int position) {

        AlbumRVModal albumRVModal = albumRVModalArrayList.get(position);
        Picasso.get().load(albumRVModal.imageUrl).into(holder.albumIV);
        holder.albumNameTV.setText(albumRVModal.name);
        holder.albumDetailTV.setText(albumRVModal.artistName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, AlbumDetailActivity.class);
                // on below line passing album related data.
                i.putExtra("id", albumRVModal.id);
                i.putExtra("name", albumRVModal.name);
                i.putExtra("img", albumRVModal.imageUrl);
                i.putExtra("artist", albumRVModal.artistName);
                i.putExtra("albumUrl", albumRVModal.external_urls);
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return albumRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        // for image view and text view.
        private ImageView albumIV;
        private TextView albumNameTV, albumDetailTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            albumIV = itemView.findViewById(R.id.idIVAlbum);
            albumNameTV = itemView.findViewById(R.id.idTVAlbumName);
            albumDetailTV = itemView.findViewById(R.id.idTVALbumDetails);
        }
    }
}
