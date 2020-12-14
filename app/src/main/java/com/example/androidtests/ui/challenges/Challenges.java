package com.example.androidtests.ui.challenges;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.androidtests.R;
import com.example.androidtests.databinding.FragmentChallengesBinding;
import com.example.androidtests.models.Challenge;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.User;
import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.ChallengesViewModel;
import com.example.androidtests.viewModels.UserChallengesViewModel;

import java.util.List;
import java.util.stream.Collectors;


public class Challenges extends Fragment {
    private FragmentChallengesBinding binding;
    private RecyclerView challengesRecycler;
    private ChallengesViewModel viewModel;
    private UserChallengesViewModel userChallengesViewModel;
    private List<UserChallenge> userChallenges;
    private List<Challenge> allChallenges;
    ChallengesAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ChallengesViewModel.class);
        userChallengesViewModel = new ViewModelProvider(this).get(UserChallengesViewModel.class);
        binding = FragmentChallengesBinding.inflate(inflater, container, false);
        challengesRecycler = binding.challengesRecycler;

        adapter = new ChallengesAdapter();
        challengesRecycler.setAdapter(adapter);
        challengesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        challengesRecycler.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));

        int searchCloseButtonId = binding.challengeSearchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.binding.challengeSearchView.findViewById(searchCloseButtonId);

        viewModel.getChallenges().observe(getViewLifecycleOwner(), adapter::initChallenges);


        binding.challengeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                challengesRecycler.setVisibility(View.VISIBLE);
                binding.suggestChallengeButton.setVisibility(View.VISIBLE);
                adapter.setChallenges(filterChallenges(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            private List<Challenge> filterChallenges(String text) {
                return allChallenges.stream().filter(challenge -> challenge.getName().contains(text))
                        .collect(Collectors.toList());
            }
        });

        closeButton.setOnClickListener(this::closeSearchView);

        binding.challengeSearchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(hasFocus && challengesRecycler.getVisibility() == View.VISIBLE) {
                challengesRecycler.setVisibility(View.GONE);
                binding.suggestChallengeButton.setVisibility(View.GONE);
            }
        });

        userChallengesViewModel.getUserChallenges().observe(getViewLifecycleOwner(), challenges -> {
            this.userChallenges = challenges;
            viewModel.sendRequest();
        });

        userChallengesViewModel.getError().observe(getViewLifecycleOwner(), this::displayErrorScreen);

        sendRequest(SaveSharedPreference.getLogedInUser(getContext()).getId());
        return binding.getRoot();
    }

    private void sendRequest(int id) {
        userChallengesViewModel.sendRequest(id);
        binding.visibleLayout.setVisibility(View.GONE);
        binding.errorImageView.setVisibility(View.GONE);
        binding.challengeProgressBar.setVisibility(View.VISIBLE);
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

    private void closeSearchView(View view) {
        view = getActivity().getCurrentFocus();
        if (view != null) {
            adapter.setChallenges(allChallenges);
            challengesRecycler.setVisibility(View.VISIBLE);
            binding.suggestChallengeButton.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            binding.challengeSearchView.setQuery("", false);
            binding.challengeSearchView.clearFocus();
        }
    }

    private static class ChallengesViewHolder extends RecyclerView.ViewHolder {
        public TextView difficulty, name;
        public Button startAndStop;

        public ChallengesViewHolder(@NonNull View itemView) {
            super(itemView);
            difficulty = itemView.findViewById(R.id.difficultyView);
            name = itemView.findViewById(R.id.challengeNameTextView);
            startAndStop = itemView.findViewById(R.id.startStopButton);
        }
    }

    private class ChallengesAdapter extends RecyclerView.Adapter<ChallengesViewHolder> {
        private List<Challenge> challenges;
        @NonNull
        @Override
        public ChallengesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.challenge_item_layout, parent, false);
            Challenges.ChallengesViewHolder vh = new Challenges.ChallengesViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ChallengesViewHolder holder, int position) {
            Challenge challenge = challenges.get(position);
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.name.setText(challenge.getName());
            holder.name.setOnClickListener(this::displayedDetailedChallenge);
            holder.difficulty.setText(challenge.getDifficulty());
            holder.startAndStop.setText(Challenges.this.getButtonText(challenge));
        }

        @Override
        public int getItemCount() {
            return challenges == null ? 0 : challenges.size();
        }

        public void initChallenges(List<Challenge> challenges) {
            binding.challengeProgressBar.setVisibility(View.GONE);
            binding.errorImageView.setVisibility(View.GONE);
            binding.visibleLayout.setVisibility(View.VISIBLE);
            allChallenges = challenges;
            setChallenges(challenges);
        }

        public void setChallenges(List<Challenge> challenges) {
            this.challenges = challenges;
            notifyDataSetChanged();
        }

        private Challenge getChallengeByName(String name) {
            return challenges.stream().filter(challenge -> challenge.getName()
                    .equals(name)).findFirst().get();
        }

        private void displayedDetailedChallenge(View v) {
            TextView view = (TextView) v;
            Challenge challenge = getChallengeByName(view.getText().toString());
            UserChallenge[] array = userChallenges.toArray(new UserChallenge[0]);
            ChallengesDirections.ActionNavigationChallengesToDetailedChallenge direction =
                    ChallengesDirections.actionNavigationChallengesToDetailedChallenge(challenge, array);
            Navigation.findNavController(v).navigate(direction);
        }
    }

    private String getButtonText(Challenge challenge) {
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

