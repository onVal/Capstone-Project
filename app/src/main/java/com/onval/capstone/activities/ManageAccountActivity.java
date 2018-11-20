package com.onval.capstone.activities;

import android.content.Intent;

import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.onval.capstone.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageAccountActivity extends AppCompatActivity {
    public static final int SIGNIN_REQUEST_CODE = 1;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;

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
                    this.googleSignInAccount = googleSignInAccount;
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

    @OnClick(R.id.hellobtn)
    public void clickHello() throws IOException {
        //upload 1_hhh.mp4 to google drive
        String path = "/storage/emulated/0/Android/data/com.onval.capstone/cache/";
        File hello = new File(path + "1_hhh.mp4");

        DriveResourceClient resourceClient =  Drive.getDriveResourceClient(this, googleSignInAccount);

        final Task<DriveFolder> rootFolderTask = resourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = resourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    FileInputStream inputStream = new FileInputStream(hello);
                    int readByte;
                    while ((readByte = inputStream.read()) != -1) {
                        baos.write(readByte);
                    }

                    OutputStream outputStream = contents.getOutputStream();
                    outputStream.write(baos.toByteArray());


                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("Hello.mp4")
                            .setMimeType("audio/mp4")
                            .setStarred(false)
                            .build();

                    return resourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            Log.e("derp", "File created successfully");

                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e("derp", "Unable to create file", e);
                    finish();
                });
    }
}
