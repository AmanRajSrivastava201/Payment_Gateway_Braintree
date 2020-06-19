package com.example.braintree2.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.braintree2.R;
import com.example.braintree2.model.BraintreeToken;
import com.example.braintree2.model.BraintreeTransaction;
import com.example.braintree2.retrofit.IBrainTreeApi;
import com.example.braintree2.retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE = 1;
    String token;
    String amount;
    Button btnPay;
    EditText etAmount;
    LinearLayout llHolder;
    ProgressBar mProgressBar;
    IBrainTreeApi brainTreeApi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brainTreeApi = RetrofitClient.getInstance().create(IBrainTreeApi.class);

        mProgressBar = findViewById(R.id.progressBar);
        llHolder = (LinearLayout) findViewById(R.id.llHolder);
        etAmount = (EditText) findViewById(R.id.etPrice);
        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPayment();

            }
        });
        compositeDisposable.add(brainTreeApi.getToken().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BraintreeToken>() {
                    @Override
                    public void accept(BraintreeToken braintreeToken) throws Exception {
                        if (braintreeToken.isSuccess()) {
                            mProgressBar.setVisibility(View.GONE);
                            llHolder.setVisibility(View.VISIBLE);
                            token = braintreeToken.getClientToken();
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Unable to get token", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }));

    }


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void submitPayment() {
        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String stringNonce = nonce.getNonce();
                Log.d("mylog", "Result: " + stringNonce);
                if (!etAmount.getText().toString().isEmpty()) {
                    amount = etAmount.getText().toString();
                    mProgressBar.setVisibility(View.VISIBLE);
                    compositeDisposable.add(brainTreeApi
                            .submitPayment(
                                    amount, nonce.getNonce())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<BraintreeTransaction>() {
                                @Override
                                public void accept(BraintreeTransaction braintreeTransaction) throws Exception {
                                    if (braintreeTransaction.isSuccess()) {
                                        Toast.makeText(MainActivity.this, "Transaction Successfull" + "\n" + "Transaction id" + braintreeTransaction.getTransaction().getId(), Toast.LENGTH_SHORT).show();
                                        Log.i("Transaction details", braintreeTransaction.getTransaction().toString());
                                        mProgressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(MainActivity.this, TransactionDetailActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("TRANSACTION_MODEL", braintreeTransaction.getTransaction());
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(MainActivity.this, "Payment failed!", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            }));
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user canceled");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("mylog", "Error : " + error.toString());
            }
        }
    }
}
