package com.onval.capstone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.Task;
import com.onval.capstone.R;
import com.onval.capstone.utility.GuiUtility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageAccountActivity extends AppCompatActivity {
    public static final int SIGNIN_REQUEST_CODE = 1;
    public static final String UPLOAD_FOLDER_NAME = "Recordings";

    private GoogleSignInClient googleSignInClient;

    @BindView(R.id.account_name)
    public TextView accountName;

    @BindView(R.id.account_email)
    public TextView accountEmail;

    @BindView(R.id.account_txt)
    public TextView accountTxt;

    @BindView(R.id.logout_btn)
    public Button logoutButton;

    @BindView(R.id.login_btn)
    public SignInButton loginButton;

    private String signedInName;
    private String signedInEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GuiUtility.initCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        setToolbar();

        ButterKnife.bind(this);

        signIn();
    }

    private void signIn() {
        googleSignInClient = buildGoogleSignInClient();
        startActivityForResult(googleSignInClient.getSignInIntent(), SIGNIN_REQUEST_CODE);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SIGNIN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    updateViewWithGoogleSignInAccountTask(task);
                } else {
                    finish();
                }
        }

    }

    private void updateViewWithGoogleSignInAccountTask(Task<GoogleSignInAccount> task) {
        task.addOnSuccessListener(
                googleSignInAccount -> {
                    String accountName = googleSignInAccount.getGivenName();
                    String accountLastName= googleSignInAccount.getFamilyName();
                    signedInName = accountName + " " + accountLastName;
                    signedInEmail = googleSignInAccount.getEmail();
                    displaySignedInUser();
                })
                .addOnFailureListener(
                        e -> {
                            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                            displayNoUser();
                        });
    }

    private void displaySignedInUser() {
        accountName.setText(signedInName);
        accountEmail.setText(signedInEmail);
        accountTxt.setText(R.string.account_msg);
        loginButton.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    private void displayNoUser() {
        accountEmail.setText("");
        accountName.setText("");
        accountTxt.setText(R.string.sign_in_message);
        loginButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.INVISIBLE);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar2);
        toolbar.setTitle(R.string.manage_account_act_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @OnClick(R.id.login_btn)
    public void clickSignIn() {
        signIn();
    }

    @OnClick(R.id.logout_btn)
    public void clickLogout() {
        Task<Void> task = googleSignInClient.signOut();
        task.addOnSuccessListener(o -> displayNoUser());
    }

}
