package com.example.calculatortestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;


import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.AuthenticationCallback; // Imports MSAL auth methods
import com.microsoft.identity.client.*;

public class MainActivity extends AppCompatActivity {

//    private final static String[] SCOPES = {"Files.Read"};
//    final static String AUTHORITY = "https://login.microsoftonline.com/common";
    private ISingleAccountPublicClientApplication mSingleAccountApp;

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView titleTextView;
    TextView resultTextView;
    TextView resultValueTextView;
    EditText num1;
    EditText num2;
    Spinner operation;
    Button compute_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(),
                R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mSingleAccountApp = application;
                        loadAccount();
                    }
                    @Override
                    public void onError(MsalException exception) {
                    }
                });
    }

    private void initializeUI(){
        titleTextView = findViewById(R.id.titleTextView);
        resultTextView = findViewById(R.id.resultTextView);
        resultValueTextView = findViewById(R.id.resultValueTextView);
        num1 = findViewById(R.id.editTextNumber);
        num2 = findViewById(R.id.editTextNumber2);
        operation = findViewById(R.id.spinner);
        compute_button = findViewById(R.id.button);

        titleTextView.setText(R.string.title_text);
        resultTextView.setText(R.string.result_text);
        resultTextView.setVisibility(View.INVISIBLE);

        compute_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numString1 = num1.getText().toString();
                String numString2 = num2.getText().toString();
                double x = 0;
                if (numString1.length() > 0){
                    x = Double.parseDouble(numString1);
                }

                double y = 0;
                if (numString2.length() > 0){
                    y = Double.parseDouble(numString2);
                }

                char op = operation.getSelectedItem().toString().charAt(0);

                mSingleAccountApp.calculatorApi(x, y, op, new IPublicClientApplication.CalculatorAPICallback() {
                    @Override
                    public void onCalculationResult(double result) {
                        resultValueTextView.setText("" + result);
                        resultTextView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(@NonNull MsalException error) {
                        resultValueTextView.setText("Error: " + error.getMessage());
                        resultTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void loadAccount() {
        if (mSingleAccountApp == null) {
            return;
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
//                updateUI(activeAccount);
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
//                    performOperationOnSignOut();
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
//                displayError(exception);
            }
        });
    }

    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAG, "Successfully authenticated");
                /* Update UI */
//                updateUI(authenticationResult.getAccount());
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
//                displayError(exception);
            }
            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    private SilentAuthenticationCallback getAuthSilentCallback() {
        return new SilentAuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d(TAG, "Successfully authenticated");
//                callGraphAPI(authenticationResult);
            }
            @Override
            public void onError(MsalException exception) {
                Log.d(TAG, "Authentication failed: " + exception.toString());
//                displayError(exception);
            }
        };
    }
}