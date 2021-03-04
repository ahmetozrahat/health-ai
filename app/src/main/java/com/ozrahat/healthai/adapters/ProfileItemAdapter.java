package com.ozrahat.healthai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ozrahat.healthai.R;
import com.ozrahat.healthai.models.Gender;
import com.ozrahat.healthai.models.ProfileItem;

import java.util.List;

public class ProfileItemAdapter extends RecyclerView.Adapter<ProfileItemAdapter.ViewHolder> {

    private final Context context;
    private final List<ProfileItem> items;

    public ProfileItemAdapter(Context context, List<ProfileItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    public ProfileItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.row_profile_item, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileItemAdapter.ViewHolder holder, int position){
        final ProfileItem profileItem = items.get(position);

        if(position == 5){
            holder.iconImageView.setVisibility(View.INVISIBLE);
        }

        holder.iconImageView.setImageDrawable(ContextCompat.getDrawable(context, profileItem.icon));
        holder.titleTextView.setText(profileItem.title);
        holder.contentTextView.setText(profileItem.content);
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iconImageView;
        private TextView titleTextView;
        private TextView contentTextView;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.profile_row_icon);
            titleTextView = itemView.findViewById(R.id.profile_row_title);
            contentTextView = itemView.findViewById(R.id.profile_row_content);
        }
    }

}