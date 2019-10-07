package com.example.allloginsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumber extends AppCompatActivity {

    private String mVerificationId;
    private FirebaseAuth mAuth;
    private EditText phone,SMScode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        mAuth=FirebaseAuth.getInstance();
        phone=findViewById(R.id.phoneNumber);
        SMScode=findViewById(R.id.Code);

    }

    public void submitNumber(View view) {
        String num=phone.getText().toString().trim();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88"+num,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                 new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        String code=credential.getSmsCode();
                        if (code != null){
                           SMScode.setText(code);
                        }
                        Toast.makeText(PhoneNumber.this, "PhoneNumber"+"onVerificationCompleted",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(PhoneNumber.this, "CredentialsException", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Toast.makeText(PhoneNumber.this, " Many Request", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("PhoneNumber","onVerificationFailed");

                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {

                        mVerificationId = verificationId;
                        Toast.makeText(PhoneNumber.this, "PhoneNumber"+"onCodeSent",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneNumber.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(PhoneNumber.this, " Code Not Match", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Toast.makeText(PhoneNumber.this, "PhoneNumber"+"signInWithPhoneAuthCredential",Toast.LENGTH_LONG).show();

                    }
                });
    }

    public void enterCode(View view) {
        String code=SMScode.getText().toString().trim();
        verifVerification(mVerificationId,code);
    }

    private void verifVerification(String mVerificationId, String code) {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }
}
