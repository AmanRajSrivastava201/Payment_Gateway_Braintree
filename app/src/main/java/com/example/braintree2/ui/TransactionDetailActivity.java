package com.example.braintree2.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.braintree2.R;
import com.example.braintree2.model.Transaction;

public class TransactionDetailActivity extends AppCompatActivity {

    Transaction transactionModel;
    TextView amount;
    TextView trId;
    TextView status;
    TextView type;
    TextView currIsoCode;
    TextView mAccId;
    TextView smAccId;
    TextView mmAccId;
    TextView orderId;
    TextView createdAt;
    TextView updatedAt;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Bundle bundle = getIntent().getExtras();
        transactionModel = (Transaction) bundle.getSerializable("TRANSACTION_MODEL");
        amount = findViewById(R.id.amount);
        trId = findViewById(R.id.trId);
        status = findViewById(R.id.status);
        type = findViewById(R.id.type);
        currIsoCode = findViewById(R.id.currIsoCode);
        mAccId = findViewById(R.id.mAccId);
        smAccId = findViewById(R.id.smAccId);
        mmAccId = findViewById(R.id.mmAccId);
        orderId = findViewById(R.id.orderId);
        createdAt = findViewById(R.id.createdAt);
        updatedAt = findViewById(R.id.updatedAt);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionDetailActivity.super.onBackPressed();
            }
        });

        setDataValue();


    }

    void setDataValue() {
        amount.setText(transactionModel.getAmount());
        trId.setText(transactionModel.getId());
        status.setText(transactionModel.getStatus());
        type.setText(transactionModel.getType());
        currIsoCode.setText(transactionModel.getCurrencyIsoCode());
        mAccId.setText(transactionModel.getMerchantAccountId());
        smAccId.setText(transactionModel.getSubMerchantAccountId());
        mmAccId.setText(transactionModel.getMasterMerchantAccountId());
        orderId.setText(transactionModel.getOrderId());
        createdAt.setText(transactionModel.getCreatedAt());
        updatedAt.setText(transactionModel.getUpdatedAt());


    }
}
