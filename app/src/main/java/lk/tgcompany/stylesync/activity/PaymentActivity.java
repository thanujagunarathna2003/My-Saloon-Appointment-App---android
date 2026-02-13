package lk.tgcompany.stylesync.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import lk.tgcompany.stylesync.R;

public class PaymentActivity extends AppCompatActivity {

    private TextView amountText, cardDisplayNumber, cardDisplayName;
    private TextInputEditText cardHolderName, cardNumber, expiryDate, cvv;
    private MaterialButton btnPayNow;

    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        fStore = FirebaseFirestore.getInstance();

        // 1. UI Initialization
        amountText = findViewById(R.id.amountText);
        cardDisplayNumber = findViewById(R.id.cardDisplayNumber);
        cardDisplayName = findViewById(R.id.cardDisplayName);
        cardHolderName = findViewById(R.id.cardHolderName);
        cardNumber = findViewById(R.id.cardNumber);
        expiryDate = findViewById(R.id.expiryDate);
        cvv = findViewById(R.id.cvv);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Get and display amount from BookingActivity
        double price = getIntent().getDoubleExtra("servicePrice", 0.0);
        amountText.setText("Total: Rs. " + price);

        setupCardUpdateLogic();

        btnPayNow.setOnClickListener(v -> processMCommercePayment());
    }

    private void setupCardUpdateLogic() {
        cardHolderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardDisplayName.setText(s.toString().isEmpty() ? "CARD HOLDER" : s.toString().toUpperCase());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardDisplayNumber.setText(s.toString().isEmpty() ? "**** **** **** ****" : s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void processMCommercePayment() {
        // Validation for card details
        if (cardNumber.getText().toString().length() < 16) {
            cardNumber.setError("Invalid Card Number");
            return;
        }

        // Generate a simulated transaction ID
        String txnId = "PAY-" + System.currentTimeMillis();

        // Final Save: Calling the combined method
        saveBookingAndPaymentToFirestore(txnId);
    }

    private void saveBookingAndPaymentToFirestore(String txnId) {
        // Get data passed from BookingActivity
        String sName = getIntent().getStringExtra("serviceName");
        String sDate = getIntent().getStringExtra("date");
        String sTime = getIntent().getStringExtra("time");
        double sPrice = getIntent().getDoubleExtra("servicePrice", 0.0);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create Map with combined Payment and Booking details
        Map<String, Object> booking = new HashMap<>();
        booking.put("serviceName", sName);
        booking.put("date", sDate);
        booking.put("time", sTime);
        booking.put("price", sPrice);
        booking.put("userId", uid);
        booking.put("transactionId", txnId); // M-commerce proof
        booking.put("status", "Paid & Confirmed");
        booking.put("timestamp", FieldValue.serverTimestamp());

        // Save to "Bookings" collection
        // Firestore "Bookings"
        fStore.collection("Bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(this, "Booking Successful!", Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(PaymentActivity.this, MyBookingsActivity.class);


                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {

                    Log.e("DB_ERROR", "Error saving booking: " + e.getMessage());
                    Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}