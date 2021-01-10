package com.example.androidtests.ui.friends;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidtests.R;
import com.example.androidtests.databinding.FriendsFragmentBinding;
import com.example.androidtests.models.Friend;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.utils.Profile;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.FriendshipViewModel;

import java.util.List;

public class Friends extends Fragment {
    private FriendshipViewModel friendViewModel;
    private FriendsFragmentBinding binding;
    private RecyclerView friendsRecycler;
    private FriendsAdapter adapter;
    private Button addFriendButton;
    private List<Friend> friends;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FriendsFragmentBinding.inflate(inflater, container, false);
        addFriendButton = binding.addFriendButton;
        friendViewModel = new ViewModelProvider(this).get(FriendshipViewModel.class);
        friendsRecycler = binding.friendsRecyclerView;

        adapter = new FriendsAdapter();
        friendsRecycler.setAdapter(adapter);
        friendsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsRecycler.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));

        friendViewModel.getFriends().observe(getViewLifecycleOwner(), this::displayFriends);
        friendViewModel.getError().observe(getViewLifecycleOwner(), this::displayErrorScreen);
        friendViewModel.getNoFriends().observe(getViewLifecycleOwner(), res -> {
            binding.noFriendTitle.setVisibility(View.VISIBLE);
        });

        addFriendButton.setOnClickListener(this::addFriend);

        sendRequest();
        return binding.getRoot();
    }

    private void addFriend(View view) {
        Friend[] friends = this.friends.toArray(new Friend[0]);
        FriendsDirections.ActionNavigationFriendsToAddfriend direction =
                FriendsDirections.actionNavigationFriendsToAddfriend(friends);
        Navigation.findNavController(view).navigate(direction);
    }

    private void sendRequest() {
        friendViewModel.getAllFriends(SaveSharedPreference.getLogedInUser(getContext()));
        binding.visibleLayout.setVisibility(View.GONE);
        binding.errorImageView.setVisibility(View.GONE);
        binding.friendProgressBar.setVisibility(View.VISIBLE);
    }

    private void displayErrorScreen(NetworkError error) {
        binding.friendProgressBar.setVisibility(View.GONE);
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

    private void displayFriends(List<Friend> friends) {
        this.friends = friends;
        binding.visibleLayout.setVisibility(View.VISIBLE);
        binding.errorImageView.setVisibility(View.GONE);
        binding.friendProgressBar.setVisibility(View.GONE);
        adapter.setFriends(this.friends);
    }

    private class FriendsAdapter extends RecyclerView.Adapter<FriendsViewHolder> {
        private List<Friend> friends;
        @NonNull
        @Override
        public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_item__layout, parent, false);
            FriendsViewHolder vh = new FriendsViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
            Friend friend = friends.get(position);
            holder.name.setText(friend.getUser().getFirstName() + " " + friend.getUser().getLastName());
            holder.points.setText(String.valueOf(Profile.calculateUserScore(friend.getChallenges())));
        }

        @Override
        public int getItemCount() {
            return friends == null ? 0 : friends.size();
        }

        public void setFriends(List<Friend> friends) {
            this.friends = friends;
            notifyDataSetChanged();
        }
    }

    private class FriendsViewHolder extends RecyclerView.ViewHolder {
        public TextView name, points;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView);
            points = itemView.findViewById(R.id.pointsTextView);
        }
    }
}