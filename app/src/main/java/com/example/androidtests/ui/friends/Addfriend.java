package com.example.androidtests.ui.friends;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.androidtests.R;
import com.example.androidtests.models.Friend;
import com.example.androidtests.models.User;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.FriendRequestViewModel;
import com.example.androidtests.viewModels.UserViewModel;
import com.example.androidtests.utils.Notifications;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class Addfriend extends Fragment {
    private Button addFriendButton;
    private TextView searchIdTextView;
    private UserViewModel viewModel;
    private FriendRequestViewModel friendRequestsViewModel;
    private List<Friend> friends;
    private User searchedUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Friend[] friendsArray = (Friend[]) getArguments().getParcelableArray("friends");
            friends = Arrays.asList(friendsArray);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addfriend, container, false);
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        friendRequestsViewModel = new ViewModelProvider(this).get(FriendRequestViewModel.class);

        addFriendButton = root.findViewById(R.id.searchUserButton);
        searchIdTextView = root.findViewById(R.id.searchIdEditText);

        viewModel.user.observe(getViewLifecycleOwner(), this::displayConfirmAddMessage);
        friendRequestsViewModel.getSent().observe(getViewLifecycleOwner(), res -> {
            if(res) {
                Toast.makeText(getContext(), getString(R.string.request_send_confirmation), Toast.LENGTH_SHORT).show();
                try {
                    Notifications.sendFriendRequestNotification(getActivity(), searchedUser.getFirebaseToken());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getContext(), getString(R.string.request_not_send) , Toast.LENGTH_SHORT).show();
            }
        });
        friendRequestsViewModel.getDeleted().observe(getViewLifecycleOwner(), res -> {
            if(res) {
                Toast.makeText(getContext(), getString(R.string.request_deleted_confirmation), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), getString(R.string.request_not_deleted) , Toast.LENGTH_SHORT).show();
            }
        });

        addFriendButton.setOnClickListener(view -> {
            String searchedId = searchIdTextView.getText().toString();
            if(!searchedId.isEmpty() && searchedId.matches("^[1-9]\\d*$") &&
                    !searchedId.equals(SaveSharedPreference.getLogedInUser(getContext()).getId().toString())) {
                if(isAfriend(Integer.parseInt(searchedId))) {
                    Toast.makeText(getContext(), getString(R.string.already_friend), Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.getUserById(Integer.parseInt(searchedId));
                }
            }
        });
        return root;
    }

    private Boolean isAfriend(int id) {
        return friends.stream().parallel()
                .anyMatch(friend -> friend.getUser().getId() == id);
    }

    private void displayConfirmAddMessage(User user) {
        searchedUser = user;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.results);
        builder.setMessage(getString(R.string.add_user_confirmation) + " : " + user.getFirstName() + " " + user.getLastName());
        builder.setPositiveButton(R.string.yes, (dialog, which) -> friendRequestsViewModel.sendFriendRequest(user.getId()));
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}