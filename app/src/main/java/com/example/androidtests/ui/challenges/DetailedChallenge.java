package com.example.androidtests.ui.challenges;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.androidtests.R;
import com.example.androidtests.databinding.FragmentChallengesBinding;
import com.example.androidtests.databinding.FragmentDetailedChallengeBinding;
import com.example.androidtests.models.Challenge;
import com.example.androidtests.models.User;
import com.example.androidtests.models.UserChallenge;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DetailedChallenge extends Fragment {
    FragmentDetailedChallengeBinding binding;
    private Challenge challenge;
    private List<UserChallenge> userChallenges;
    private ImageView challengeImageView;
    private Button challengeActionButton;

    public DetailedChallenge() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challenge = getArguments().getParcelable("challenge");
            UserChallenge[] challengesArray = (UserChallenge[]) getArguments().getParcelableArray("userChallenges");
            userChallenges = Arrays.asList(challengesArray);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailedChallengeBinding.inflate(inflater, container, false);
        challengeImageView = binding.challengeImageView;
        challengeActionButton = binding.challengeActionButton;

        binding.challengeTextView.setText(challenge.getName());
        loadChallengePicture(challenge.getPhoto());
        binding.descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        binding.descriptionTextView.setText(challenge.getDescription());
        binding.difficultyView.setText(getResources().getString(R.string.Difficulty) + " : " + challenge.getDifficulty());
        challengeActionButton.setText(getChallengeState(challenge));

        return binding.getRoot();
    }

    private void loadChallengePicture(String url) {
        challengeImageView.setBackgroundResource(R.drawable.circle_green);

        if(url != null) {
            Glide.with(this).asBitmap().centerCrop().load(url).into(new BitmapImageViewTarget(challengeImageView){
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circular = RoundedBitmapDrawableFactory.create(getActivity().getApplicationContext().getResources(),resource);
                    circular.setCircular(true);
                    challengeImageView.setImageDrawable(circular);
                }
            });
        }
    }

    private String getChallengeState(Challenge challenge) {
        List<UserChallenge> challenges =
                userChallenges.stream().filter(userChallenge -> userChallenge.getName().equals(challenge.getName()))
                        .collect(Collectors.toList());

        if(challenges.size() == 1) {
            return challenges.get(0).getenddate() == null ?
                    getResources().getString(R.string.in_progress) :
                    getResources().getString(R.string.resume);
        }
        return getResources().getString(R.string.start);
    }
}