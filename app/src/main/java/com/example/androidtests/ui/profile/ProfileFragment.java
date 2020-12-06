package com.example.androidtests.ui.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.androidtests.R;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.User;
import com.example.androidtests.databinding.FragmentProfileBinding;
import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.utils.Profile;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.UserChallengesViewModel;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;


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

        completedChallengesRecycler.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        challengesRecycler.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        showChallengesButton = binding.challengesButton;
        showCompletedChallengesButton = binding.completedChallengesButton;

        User user = SaveSharedPreference.getLogedInUser(getContext());
        binding.profileNameTextView.setText(user.getFirstName());

        showChallengesButton.setOnClickListener(this::displayChallenges);
        showCompletedChallengesButton.setOnClickListener(this::displayCompletedChallenges);

        displayChallenges(binding.getRoot());

        viewModel.getUserChallenges().observe(getViewLifecycleOwner(), challenges -> {
            binding.challengeProgressBar.setVisibility(View.GONE);
            binding.errorImageView.setVisibility(View.GONE);
            binding.visibleLayout.setVisibility(View.VISIBLE);
            user.setScore(Profile.calculateUserScore(challenges));
            binding.nbPointsTextView.setText(user.getScore() + " POINTS");
            loadProfilePicture(user);
            adapter.setChallenges(challenges.stream()
                    .filter(challenge -> challenge.getenddate() == null)
                    .collect(Collectors.toList()));
            completedAdapter.setChallenges(challenges.stream()
                    .filter(challenge -> challenge.getenddate() != null)
                    .collect(Collectors.toList()));
        });

        viewModel.getError().observe(getViewLifecycleOwner(), this::displayErrorScreen);

        sendRequest(user.getId());

        return binding.getRoot();
    }

    private void sendRequest(int id) {
        viewModel.sendRequest(id);
        binding.visibleLayout.setVisibility(View.GONE);
        binding.errorImageView.setVisibility(View.GONE);
        binding.challengeProgressBar.setVisibility(View.VISIBLE);
    }

    private void displayChallenges(View v) {
        ViewGroup.LayoutParams params = completedChallengesRecycler.getLayoutParams();
        params.height = 0;
        completedChallengesRecycler.setLayoutParams(params);
        ViewGroup.LayoutParams paramsB = challengesRecycler.getLayoutParams();
        paramsB.height = dpToPx(150);
        challengesRecycler.setLayoutParams(paramsB);
    }

    private void displayCompletedChallenges(View v) {
        ViewGroup.LayoutParams params = challengesRecycler.getLayoutParams();
        params.height = 0;
        challengesRecycler.setLayoutParams(params);
        ViewGroup.LayoutParams paramsB = completedChallengesRecycler.getLayoutParams();
        paramsB.height = dpToPx(150);
        completedChallengesRecycler.setLayoutParams(paramsB);
    }

    private void displayErrorScreen(NetworkError error) {
        binding.challengeProgressBar.setVisibility(View.GONE);
        if (error == null) {
            binding.errorImageView.setVisibility(View.GONE);
            binding.visibleLayout.setVisibility(View.VISIBLE);
            return;
        }

        binding.errorImageView.setVisibility(View.VISIBLE);
        binding.visibleLayout.setVisibility(View.GONE);
        binding.errorImageView.setImageDrawable(getResources().getDrawable(error.getErrorDrawable(),
                getActivity().getTheme()));
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    private void loadProfilePicture(User user) {
        String firstName = user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1);
        String lastName = user.getLastName().substring(0, 1).toUpperCase() + user.getLastName().substring(1);
        String url = "https://res.cloudinary.com/dq4qdktus/image/upload/v1606763072/ecoChallenge/"
                + lastName + firstName + ".png";
        binding.profileImageView.setBackgroundResource(getUserRankingDrawableId(user.getScore()));

        Glide.with(this).asBitmap().load(url).into(new BitmapImageViewTarget(binding.profileImageView){
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circular = RoundedBitmapDrawableFactory.create(getActivity().getApplicationContext().getResources(),resource);
                circular.setCircular(true);
                binding.profileImageView.setImageDrawable(circular);
            }
        });
    }

    private int getUserRankingDrawableId(int points) {
        if(points >= 5475) {
            return R.drawable.circle_diamond;
        } else if(points >= 3475){
            return R.drawable.circle_gold;
        } else if(points >= 2475) {
            return R.drawable.circle_silver;
        } else if(points >= 1000) {
            return R.drawable.circle_bronze;
        }
        return R.drawable.circle_basic;
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