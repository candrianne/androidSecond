package com.example.androidtests.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.androidtests.R;
import com.example.androidtests.models.User;
import com.example.androidtests.databinding.FragmentProfileBinding;
import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.UserChallengesViewModel;

import java.util.List;
import java.util.stream.Collectors;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    RecyclerView completedChallengesRecycler, challengesRecycler;
    Button showCompletedChallengesButton, showChallengesButton;
    private UserChallengesViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(UserChallengesViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        completedChallengesRecycler = binding.completedChallengesRecyclerView;
        challengesRecycler = binding.challengesRecyclerView;
        ChallengeAdapter adapter = new ChallengeAdapter();
        CompletedChallengeAdapter completedAdapter = new CompletedChallengeAdapter();

        completedChallengesRecycler.setAdapter(completedAdapter);
        challengesRecycler.setAdapter(adapter);
        completedChallengesRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        challengesRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        //displayChallenges(binding.getRoot());


        showChallengesButton = binding.challengesButton;
        showCompletedChallengesButton = binding.completedChallengesButton;

        User user = SaveSharedPreference.getLogedInUser(getContext());
        binding.profileNameTextView.setText(user.getFirstName());

        loadProfilePicture(user);

        showChallengesButton.setOnClickListener(this::displayChallenges);
        showCompletedChallengesButton.setOnClickListener(this::displayCompletedChallenges);

        viewModel.getUserChallenges().observe(getViewLifecycleOwner(), challenges -> {
            adapter.setChallenges(challenges.stream()
                    .filter(challenge -> challenge.getenddate() == null)
                    .collect(Collectors.toList()));
            completedAdapter.setChallenges(challenges.stream()
                    .filter(challenge -> challenge.getenddate() != null)
                    .collect(Collectors.toList()));
        });

        viewModel.sendRequest(user.getId());

        return binding.getRoot();
    }

    private void displayChallenges(View v) {
        completedChallengesRecycler.setLayoutParams(new ConstraintLayout.LayoutParams(completedChallengesRecycler.getWidth(),0));
        challengesRecycler.setLayoutParams(new ConstraintLayout.LayoutParams(challengesRecycler.getWidth(),150));
    }

    private void displayCompletedChallenges(View v) {
        completedChallengesRecycler.setLayoutParams(new RecyclerView.LayoutParams(completedChallengesRecycler.getWidth(),150));
        challengesRecycler.setLayoutParams(new RecyclerView.LayoutParams(challengesRecycler.getWidth(),0));
    }

    private void loadProfilePicture(User user) {
        String firstName = user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1);
        String lastName = user.getLastName().substring(0, 1).toUpperCase() + user.getLastName().substring(1);
        String url = "https://res.cloudinary.com/dq4qdktus/image/upload/v1606763072/ecoChallenge/"
                + lastName + firstName + ".png";
        Glide.with(this)
                .load(url)
                .circleCrop()
                .into(binding.profileImageView);
    }

    private static class UserChallengeViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public UserChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.challengeNameTextView);
        }
    }

    private static class ChallengeAdapter extends RecyclerView.Adapter<UserChallengeViewHolder> {
        private List<UserChallenge> challenges;

        @NonNull
        @Override
        public UserChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.challenge_item_layout, parent, false);
            UserChallengeViewHolder vh = new UserChallengeViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull UserChallengeViewHolder holder, int position) {
            UserChallenge challenge = challenges.get(position);
            holder.name.setText(challenge.getName());
        }

        @Override
        public int getItemCount() {
            return challenges == null ? 0 : challenges.size();
        }

        public void setChallenges(List<UserChallenge> challenges) {
            this.challenges = challenges;
            notifyDataSetChanged();
        }
    }

    private static class CompletedChallengeAdapter extends RecyclerView.Adapter<UserChallengeViewHolder> {
        private List<UserChallenge> challenges;

        @NonNull
        @Override
        public UserChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.challenge_item_layout, parent, false);
            UserChallengeViewHolder vh = new UserChallengeViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull UserChallengeViewHolder holder, int position) {
            UserChallenge challenge = challenges.get(position);
            holder.name.setText(challenge.getName());
        }

        @Override
        public int getItemCount() {
            return challenges == null ? 0 : challenges.size();
        }

        public void setChallenges(List<UserChallenge> challenges) {
            this.challenges = challenges;
            notifyDataSetChanged();
        }
    }
}