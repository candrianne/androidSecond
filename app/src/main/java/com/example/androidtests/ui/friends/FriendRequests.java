package com.example.androidtests.ui.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.androidtests.R;
import com.example.androidtests.databinding.FragmentFriendRequestsBinding;
import com.example.androidtests.models.FriendRequest;
import com.example.androidtests.models.Friendship;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.viewModels.FriendRequestViewModel;
import com.example.androidtests.viewModels.FriendshipViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendRequests extends Fragment {
    private FriendRequestViewModel friendRequestViewModel;
    private FriendshipViewModel friendshipViewModel;
    private FragmentFriendRequestsBinding binding;
    private RecyclerView friendsRequestsRecycler;
    private FriendRequestsAdapter adapter;

    public FriendRequests() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendRequestsBinding.inflate(inflater, container, false);

        friendsRequestsRecycler = binding.friendRequestsRecyclerView;
        adapter = new FriendRequestsAdapter();
        friendsRequestsRecycler.setAdapter(adapter);
        friendsRequestsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendRequestViewModel = new ViewModelProvider(this).get(FriendRequestViewModel.class);
        friendRequestViewModel.getFriendRequests().observe(getViewLifecycleOwner(), this::displayRequests);
        friendRequestViewModel.getError().observe(getViewLifecycleOwner(), this::displayErrorScreen);
        friendRequestViewModel.getDeleted().observe(getViewLifecycleOwner(), this::removeRequest);
        friendRequestViewModel.getNoRequests().observe(getViewLifecycleOwner(), res -> {
            adapter.setFriendRequests(new ArrayList<>());
            binding.noFriendRequestsTitle.setVisibility(res ? View.VISIBLE : View.GONE);
        });

        friendshipViewModel = new ViewModelProvider(this).get(FriendshipViewModel.class);
        friendshipViewModel.getCreated().observe(getViewLifecycleOwner(), this::addFriendship);

        sendRequest();

        return binding.getRoot();
    }

    private void addFriendship(Boolean res) {
        String text = getString(res ? R.string.friendship_created_message : R.string.error_creating_friendship_message);
        if(res) {
            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
            sendRequest();
        } else {
            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    private void removeRequest(Boolean res) {
        String text = getString(res ? R.string.request_deleted_confirmation : R.string.request_not_deleted);
        if(res) {
            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
            sendRequest();
        } else {
            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    private void displayRequests(List<FriendRequest> requests) {
        adapter.setFriendRequests(requests);
        binding.visibleLayout.setVisibility(View.VISIBLE);
        binding.errorImageView.setVisibility(View.GONE);
        binding.friendRequestsProgressBar.setVisibility(View.GONE);
    }

    private void displayErrorScreen(NetworkError error) {
        binding.friendRequestsProgressBar.setVisibility(View.GONE);
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

    private void sendRequest() {
        friendRequestViewModel.getRequests();
        binding.visibleLayout.setVisibility(View.GONE);
        binding.errorImageView.setVisibility(View.GONE);
        binding.friendRequestsProgressBar.setVisibility(View.VISIBLE);
    }

    private class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsViewHolder> {
        private List<FriendRequest> friendRequests;
        @NonNull
        @Override
        public FriendRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friendrequests_item_layout, parent, false);
            FriendRequestsViewHolder vh = new FriendRequestsViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull FriendRequestsViewHolder holder, int position) {
            FriendRequest request = friendRequests.get(position);
            holder.name.setText(request.getFirstname() + " " + request.getLastname());
            holder.accept.setOnClickListener(v -> acceptFriend(v, request));
            holder.deny.setOnClickListener(v -> denyFriendRequest(v, request));
            if(request.getPhoto() != null)
                Glide.with(getContext()).load(request.getPhoto()).into(holder.profileImageView);
        }

        @Override
        public int getItemCount() {
            return friendRequests == null ? 0 : friendRequests.size();
        }

        public void setFriendRequests(List<FriendRequest> friendRequests) {
            this.friendRequests = friendRequests;
            notifyDataSetChanged();
        }

        private Boolean acceptFriend(View view, FriendRequest request) {
            friendshipViewModel.createFriendship(request.getSender());
            return true;
        }

        private Boolean denyFriendRequest(View v, FriendRequest request) {
            friendRequestViewModel.deleteFriendRequest(request.getSender());
            return true;
        }
    }

    private class FriendRequestsViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CircleImageView profileImageView;
        public ImageButton accept, deny;

        public FriendRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView);
            profileImageView = itemView.findViewById(R.id.profilePictureImageView);
            accept = itemView.findViewById(R.id.acceptButton);
            deny = itemView.findViewById(R.id.denyButton);
        }
    }
}