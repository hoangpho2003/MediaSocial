package com.example.mediasocial.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediasocial.R;
import com.example.mediasocial.ReplacerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends Fragment {

    private TextView loginTv;
    private Button recoverBtn;
    private EditText emailEdt;

    private FirebaseAuth auth;
    private ProgressBar progressBar;

    public static final String EMAIL_REGEX = "^(.+)@(.+)$";

    public ForgotPassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        init(view);

        clickListener();
        
    }

    private void init(View view) {
        loginTv = view.findViewById(R.id.loginTV);
        recoverBtn = view.findViewById(R.id.recoverBtn);
        emailEdt = view.findViewById(R.id.emailET);
        progressBar = view.findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();
    }

    private void clickListener(){
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReplacerActivity)getActivity()).setFragment(new LoginFragment());
            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdt.getText().toString();

                if(email.isEmpty() || !email.matches(EMAIL_REGEX)){
                    emailEdt.setError("Input valid email!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(), "Password reset email send successfully!", Toast.LENGTH_SHORT).show();
                                    emailEdt.setText("");
                                }else {
                                    String exception = task.getException().getMessage();
                                    Toast.makeText(getContext(), "Error: "+exception, Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });
    }
}