package com.cometchat.pushnotificationsample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class CreateUserActivity extends AppCompatActivity {
    private TextInputEditText uid;
    private TextInputEditText name;
    private AppCompatButton createUserBtn;
    private ProgressBar progressBar;

    private RelativeLayout parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        parentView = findViewById(R.id.parent_view);
        progressBar = findViewById(R.id.createUserPb);
        uid = findViewById(R.id.etUID);
        name = findViewById(R.id.etName);
        createUserBtn = findViewById(R.id.createUserBtn);
        createUserBtn.setTextColor(getResources().getColor(R.color.white));
        createUserBtn.setOnClickListener(v -> {
            if (uid.getText().toString().isEmpty())
                uid.setError(getResources().getString(R.string.fill_this_field));
            else if (name.getText().toString().isEmpty())
                name.setError(getResources().getString(R.string.fill_this_field));
            else {
                progressBar.setVisibility(View.VISIBLE);
                createUserBtn.setClickable(false);
                User user = new User();
                user.setUid(uid.getText().toString());
                user.setName(name.getText().toString());
                CometChatUIKit.createUser(user, new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        login(user);
                    }

                    @Override
                    public void onError(CometChatException e) {
                        createUserBtn.setClickable(true);
                        Toast.makeText(CreateUserActivity.this, "Failed to create user", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void login(User user) {
        CometChatUIKit.login(user.getUid(), new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                startActivity(new Intent(CreateUserActivity.this, HomeScreenActivity.class));
                finishAffinity();
            }

            @Override
            public void onError(CometChatException e) {
                if (uid != null)
                    Snackbar.make(uid.getRootView(), "Unable to login", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(CreateUserActivity.this, LoginActivity.class));
                        }
                    }).show();
            }
        });
    }
}