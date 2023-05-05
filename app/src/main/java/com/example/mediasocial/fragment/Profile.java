package com.example.mediasocial.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mediasocial.Model.PostImageModel;
import com.example.mediasocial.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {

    private TextView nameTv, toolbarNameTv, statusTv, followingCountTv, followerCountTv, postCountTv;
    private CircleImageView profileImage;
    private Button followBtn;
    private RecyclerView recyclerView;
    private LinearLayout countLayout;
    private FirebaseUser user;
    private boolean isMyProfile = true;
    private FirestoreRecyclerAdapter<PostImageModel, PostImageHolder> adapter;
    private String uid;
    private ImageButton editProfileBtn;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        if (isMyProfile) {
            followBtn.setVisibility(View.GONE);
            countLayout.setVisibility(View.VISIBLE);
        } else {
            followBtn.setVisibility(View.VISIBLE);
            countLayout.setVisibility(View.GONE);
        }

        loadBasicData();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        loadPostImages();
        recyclerView.setAdapter(adapter);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), Profile.this);
            }
        });
    }

    private void loadBasicData() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    return;
                assert value != null;
                if (value.exists()) {
                    String name = value.getString("name");
                    String status = value.getString("status");
                    int following = value.getLong("following").intValue();
                    int follower = value.getLong("followers").intValue();

                    String proflieURL = value.getString("profileImage");

                    nameTv.setText(name);
                    statusTv.setText(status);
                    followingCountTv.setText(following+"");
                    followerCountTv.setText(follower+"");
                    toolbarNameTv.setText(name);

                    Glide.with(getContext().getApplicationContext())
                            .load(proflieURL)
                            .placeholder(R.drawable.ic_person)
                            .timeout(6500)
                            .into(profileImage);
                }
            }
        });
    }

    private void init(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        assert getActivity() != null;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        nameTv = view.findViewById(R.id.nameTv);
        toolbarNameTv = view.findViewById(R.id.toolbarNameTV);
        statusTv = view.findViewById(R.id.statusTV);
        followingCountTv = view.findViewById(R.id.followingCountTv);
        followerCountTv = view.findViewById(R.id.followerCountTv);
        postCountTv = view.findViewById(R.id.postCountTv);
        profileImage = view.findViewById(R.id.profileImage);
        followBtn = view.findViewById(R.id.followBtn);
        recyclerView = view.findViewById(R.id.recycler_view);
        countLayout = view.findViewById(R.id.countLayout);
        editProfileBtn = view.findViewById(R.id.edit_profileImage);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadPostImages() {

        if (isMyProfile) {
            uid = user.getUid();
        } else {

        }

        uid = user.getUid();
        DocumentReference reference = FirebaseFirestore.getInstance().collection("Images").document(uid);
        Query query = reference.collection("Images");
        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_items, parent, false);
                return new PostImageHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {
                Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(model.getImageURL())
                        .timeout(6500)
                        .into(holder.imageView);
            }
        };
    }

    private class PostImageHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri = result.getUri();
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        reference.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageURL = uri.toString();
                                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                                            request.setPhotoUri(uri);
                                            user.updateProfile(request.build());
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("profileImage", imageURL);
                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(user.getUid())
                                                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(getContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }else{
                            Toast.makeText(getContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}