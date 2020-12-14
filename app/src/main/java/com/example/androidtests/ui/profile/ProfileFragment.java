package com.example.androidtests.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.androidtests.R;
import com.example.androidtests.models.NetworkError;
import com.example.androidtests.models.User;
import com.example.androidtests.databinding.FragmentProfileBinding;
import com.example.androidtests.models.UserChallenge;
import com.example.androidtests.utils.Profile;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.UserChallengesViewModel;
import com.example.androidtests.viewModels.UserViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    private static final int PICK_PHOTO_FOR_AVATAR = 8;
    private FragmentProfileBinding binding;
    RecyclerView completedChallengesRecycler, challengesRecycler;
    Button showCompletedChallengesButton, showChallengesButton;
    private UserChallengesViewModel userChallengesViewModel;
    private UserViewModel userViewModel;
    private static final int PERMISSION_CODE =1;
    private static final int PICK_IMAGE=1;
    User connectedUser;
    String filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userChallengesViewModel = new ViewModelProvider(this).get(UserChallengesViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
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

        connectedUser = SaveSharedPreference.getLogedInUser(getContext());
        binding.profileNameTextView.setText(connectedUser.getFirstName());

        showChallengesButton.setOnClickListener(this::displayChallenges);
        showCompletedChallengesButton.setOnClickListener(this::displayCompletedChallenges);

        displayChallenges(binding.getRoot());

        userChallengesViewModel.getUserChallenges().observe(getViewLifecycleOwner(), challenges -> {
            binding.challengeProgressBar.setVisibility(View.GONE);
            binding.errorImageView.setVisibility(View.GONE);
            binding.visibleLayout.setVisibility(View.VISIBLE);
            connectedUser.setScore(Profile.calculateUserScore(challenges));
            binding.nbPointsTextView.setText(connectedUser.getScore() + " POINTS");
            loadProfilePicture(connectedUser);
            adapter.setChallenges(challenges.stream()
                    .filter(challenge -> challenge.getenddate() == null)
                    .collect(Collectors.toList()));
            completedAdapter.setChallenges(challenges.stream()
                    .filter(challenge -> challenge.getenddate() != null)
                    .collect(Collectors.toList()));
        });

        binding.profileImageView.setOnClickListener(v -> getProfilePicture());

        userChallengesViewModel.getError().observe(getViewLifecycleOwner(), this::displayErrorScreen);

        sendRequest(connectedUser.getId());

        return binding.getRoot();
    }

    private void getProfilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.upload_profil_picture);
        builder.setMessage(R.string.upload_profil_picture_message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            requestPermission();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void requestPermission(){
        if(ContextCompat.checkSelfPermission
                (getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ){
            accessTheGallery();
        } else {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessTheGallery();
            }else {
                Toast.makeText(getContext(), "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void accessTheGallery(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the image's file location
        filePath = getRealPathFromUri(data.getData(), getActivity());

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            uploadToCloudinary(filePath);
            //set picked image to the mProfile
                /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                binding.profileImageView.setImageBitmap(bitmap);*/
        }
    }

    private String getRealPathFromUri(Uri imageUri, Activity activity){
        Cursor cursor = activity.getContentResolver().query(imageUri, null, null, null, null);

        if(cursor==null) {
            return imageUri.getPath();
        }else{
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void uploadToCloudinary(String filePath) {
        Log.d("A", "sign up uploadToCloudinary- ");
        MediaManager.get().upload(filePath).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                binding.uploadStatusTextView.setText("start");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                binding.uploadStatusTextView.setText("Uploading... ");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String httpsUrl = resultData.get("url").toString().split(":")[0] + "s:" +
                        resultData.get("url").toString().split(":")[1];
                connectedUser.setPhoto(httpsUrl);
                userViewModel.updateUser(connectedUser);
                loadProfilePicture(connectedUser);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                binding.uploadStatusTextView.setText("error "+ error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                binding.uploadStatusTextView.setText("Reshedule "+error.getDescription());
            }
        }).dispatch();
    }

    private void sendRequest(int id) {
        userChallengesViewModel.sendRequest(id);
        binding.visibleLayout.setVisibility(View.GONE);
        binding.errorImageView.setVisibility(View.GONE);
        binding.challengeProgressBar.setVisibility(View.VISIBLE);
    }

    private void displayChallenges(View v) {
        ViewGroup.LayoutParams params = completedChallengesRecycler.getLayoutParams();
        params.height = 0;
        completedChallengesRecycler.setLayoutParams(params);
        ViewGroup.LayoutParams paramsB = challengesRecycler.getLayoutParams();
        paramsB.height = dpToPx(80);
        challengesRecycler.setLayoutParams(paramsB);
    }

    private void displayCompletedChallenges(View v) {
        ViewGroup.LayoutParams params = challengesRecycler.getLayoutParams();
        params.height = 0;
        challengesRecycler.setLayoutParams(params);
        ViewGroup.LayoutParams paramsB = completedChallengesRecycler.getLayoutParams();
        paramsB.height = dpToPx(80);
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
        String url = user.getPhoto();
        binding.profileImageView.setBackgroundResource(getUserRankingDrawableId(user.getScore()));

        if(url != null) {
            Glide.with(this).asBitmap().centerCrop().load(url).into(new BitmapImageViewTarget(binding.profileImageView){
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circular = RoundedBitmapDrawableFactory.create(getActivity().getApplicationContext().getResources(),resource);
                    circular.setCircular(true);
                    binding.profileImageView.setImageDrawable(circular);
                }
            });
        }
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
                    .inflate(R.layout.user_challenge_item_layout, parent, false);
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
                    .inflate(R.layout.user_challenge_item_layout, parent, false);
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