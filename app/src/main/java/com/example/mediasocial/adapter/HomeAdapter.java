package com.example.mediasocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mediasocial.Model.HomeModel;
import com.example.mediasocial.R;
import com.example.mediasocial.ReplacerActivity;
import com.example.mediasocial.fragment.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    private List<HomeModel> list;
    OnPressed onPressed;
    Activity context;

    public HomeAdapter(List<HomeModel> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        holder.userNameTv.setText(list.get(position).getName());
        holder.timeTv.setText("" + list.get(position).getTimestamp());
        List<String> likeList = list.get(position).getLikes();
        int count = likeList.size();
        if (count == 0) {
            holder.likeCountTv.setText("0 like");
        } else if (count == 1) {
            holder.likeCountTv.setText(count + " like");
        } else {
            holder.likeCountTv.setText(count + " likes");
        }
        holder.likeCheckBox.setChecked(likeList.contains(user.getUid()));
        holder.descriptionTv.setText(list.get(position).getDescription());
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.profileImage);

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.imageView);

        holder.clickListener(position,
                list.get(position).getId(),
                list.get(position).getName(),
                list.get(position).getUid(),
                list.get(position).getLikes(),
                list.get(position).getImageUrl()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage;
        private TextView userNameTv, timeTv, likeCountTv, descriptionTv, commentTV;
        private ImageView imageView;
        private ImageButton commentBtn, shareBtn;
        private CheckBox likeCheckBox;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            userNameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);
            imageView = itemView.findViewById(R.id.imageView);
            likeCheckBox = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            descriptionTv = itemView.findViewById(R.id.descTv);
            commentTV = itemView.findViewById(R.id.commentTV);
            onPressed.setCommentCount(commentTV);
        }

        public void clickListener(int position, String id, String name, String uid, List<String> likes, String imageUrl) {
            likeCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> onPressed.onLiked(position, id, uid, likes, isChecked));

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ReplacerActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("uid", uid);
                    intent.putExtra("isComment", true);
                    context.startActivity(intent);
                }
            });

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, imageUrl);
                    intent.setType("text/*");
                    context.startActivity(Intent.createChooser(intent, "Share link using...."));
                }
            });
        }
    }

    public interface OnPressed {
        void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked);
//        void onComment(int position, String id, String uid, String comment, LinearLayout commentLayout, EditText commentET);
        void setCommentCount(TextView textView);
    }

    public void OnPressed(OnPressed onPressed) {
        this.onPressed = onPressed;
    }
}
