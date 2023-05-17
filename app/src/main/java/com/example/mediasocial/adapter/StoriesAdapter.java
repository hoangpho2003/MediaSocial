package com.example.mediasocial.adapter;

import static com.example.mediasocial.ViewStoryActivity.FILE_TYPE;
import static com.example.mediasocial.ViewStoryActivity.VIDEO_URL_KEY;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mediasocial.Model.StoriesModel;
import com.example.mediasocial.R;
import com.example.mediasocial.StoryAddActivity;
import com.example.mediasocial.ViewStoryActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoriesHolder> {

    List<StoriesModel>  list;
    Activity activity;

    public StoriesAdapter(List<StoriesModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public StoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_layout, parent, false);
        return new StoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesHolder holder, int position) {

        if (position == 0) {
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_add))
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(v ->
                    activity.startActivity(new Intent(activity, StoryAddActivity.class)));
        }else {
            Glide.with(activity)
                    .load(list.get(position).getUrl())
                    .timeout(6500)
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(v -> {
                if (holder.getAbsoluteAdapterPosition() == 0) {
                    //new story
                    activity.startActivity(new Intent(activity, StoryAddActivity.class));
                } else {
                    //open story
                    Intent intent = new Intent(activity, ViewStoryActivity.class);
                    intent.putExtra(VIDEO_URL_KEY, list.get(position).getUrl());
                    intent.putExtra(FILE_TYPE, list.get(position).getType());
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class StoriesHolder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;

        public StoriesHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
