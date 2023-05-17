package com.example.mediasocial.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mediasocial.Model.HomeModel;
import com.example.mediasocial.Model.StoriesModel;
import com.example.mediasocial.R;
import com.example.mediasocial.adapter.HomeAdapter;
import com.example.mediasocial.adapter.StoriesAdapter;
import com.example.mediasocial.chat.ChatUsersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseUser user;
    HomeAdapter adapter;
    private List<HomeModel> list;
    private int commentCount = 0;
    private RecyclerView storiesRecyclerView;
    private StoriesAdapter storiesAdapter;
    List<StoriesModel> storiesModelList;
    int count = 0;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getActivity());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();
        adapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {
                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);
                if (likeList.contains(user.getUid())) {
                    likeList.remove(user.getUid());
                } else {
                    likeList.add(user.getUid());
                }
                Map<String, Object> map = new HashMap<>();
                map.put("likes", likeList);
                reference.update(map);
            }

            @Override
            public void setCommentCount(final TextView textView) {
                if (commentCount == 0) {
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    StringBuilder builder = new StringBuilder();
                    builder.append("See all ")
                            .append(commentCount)
                            .append(" comments");
                    textView.setText(builder);
//                      textView.setText("See all " + commentCount.getValue() + " comments");
                }
            }
        });

        view.findViewById(R.id.sendBtn).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatUsersActivity.class);
            startActivity(intent);
        });
    }

    private void loadDataFromFirestore() {

        final DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());

        final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");
        reference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("Error: ", error.getMessage());
                return;
            }
            if (value == null)
                return;
            List<String> uidList = (List<String>) value.get("following");
            if (uidList == null || uidList.isEmpty())
                return;
            collectionReference.whereIn("uid", uidList)
                    .addSnapshotListener((value1, error1) -> {
                        if (error1 != null) {
                            Log.d("Error: ", error1.getMessage());
                        }
                        if (value1 == null)
                            return;
                        for (QueryDocumentSnapshot snapshot : value1) {
                            snapshot.getReference().collection("Post Images")
                                    .addSnapshotListener((value11, error11) -> {
                                        if (error11 != null) {
                                            Log.d("Error: ", error11.getMessage());
                                        }
                                        if (value11 == null)
                                            return;
                                        list.clear();
                                        for (final QueryDocumentSnapshot snapshot1 : value11) {
                                            if (!snapshot1.exists())
                                                return;

                                            HomeModel model = snapshot1.toObject(HomeModel.class);
                                            count++;
                                            list.add(new HomeModel(
                                                    model.getName(),
                                                    model.getProfileImage(),
                                                    model.getImageUrl(),
                                                    model.getUid(),
                                                    model.getDescription(),
                                                    model.getId(),
                                                    model.getTimestamp(),
                                                    model.getLikes()));
                                            snapshot1.getReference().collection("Comments").get()
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot commentSnapshot : task
                                                                    .getResult()) {
                                                                if (commentSnapshot.getData() != null)
                                                                    commentCount++;
                                                            }
                                                        }
                                                    });
                                        }

                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    });
            // todo: fetch stories
            loadStories(uidList);
        });
    }

    void loadStories(List<String> followingList) {
        Query query = FirebaseFirestore.getInstance().collection("Stories");
        query.whereIn("uid", followingList).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("Error: ", error.getMessage());
            }
            if (value == null)
                return;
            for (QueryDocumentSnapshot snapshot : value) {
                if (!value.isEmpty()) {
                    StoriesModel model = snapshot.toObject(StoriesModel.class);
                    storiesModelList.add(model);
                }
            }
            storiesAdapter.notifyDataSetChanged();
        });
    }

    private void init(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storiesRecyclerView = view.findViewById(R.id.storiesRecyclerView);
        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        storiesModelList = new ArrayList<>();
        storiesModelList.add(new StoriesModel("", "", "", "", ""));
        storiesAdapter = new StoriesAdapter(storiesModelList, getActivity());
        storiesRecyclerView.setAdapter(storiesAdapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}

