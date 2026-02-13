package lk.tgcompany.stylesync.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.tgcompany.stylesync.R;

public class BookingActivity extends AppCompatActivity {

    private TextView txtSelectedService, txtSelectedPrice, txtBusySlots;
    private Button btnSelectDate, btnSelectTime, btnProceedPayment;
    private ImageButton btnCallSaloon;

    private String selectedDate = "";
    private String selectedTime = "";
    private String serviceName;
    private double servicePrice;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();

        // Intent data
        serviceName = getIntent().getStringExtra("serviceName");
        servicePrice = getIntent().getDoubleExtra("servicePrice", 0.0);

        // UI IDs (Oya dapu XML eke hatiyata)
        txtSelectedService = findViewById(R.id.txtSelectedService);
        txtSelectedPrice = findViewById(R.id.txtSelectedPrice);
        txtBusySlots = findViewById(R.id.txtBusySlots);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnProceedPayment = findViewById(R.id.btnProceedPayment);
        btnCallSaloon = findViewById(R.id.btnCallSaloon);

        // Display Info
        txtSelectedService.setText("Service: " + serviceName);
        txtSelectedPrice.setText("Price: Rs. " + servicePrice);

        // Listeners
        btnSelectDate.setOnClickListener(v -> openDatePicker());
        btnSelectTime.setOnClickListener(v -> openTimePicker());

        btnCallSaloon.setOnClickListener(v -> {
            String number = "0712345678"; // Call logic
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        });

        btnProceedPayment.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please select Date and Time", Toast.LENGTH_SHORT).show();
            } else {
                checkSlotAvailability(selectedDate, selectedTime);
            }
        });
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate = d + "/" + (m + 1) + "/" + y;
            btnSelectDate.setText(selectedDate);
            loadBusySlots(selectedDate); // Busy slots check karanawa
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
    }

    private void openTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, h, m) -> {
            selectedTime = String.format("%02d:%02d", h, m);
            btnSelectTime.setText(selectedTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }

    private void loadBusySlots(String date) {
        db.collection("Bookings")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Integer> counts = new HashMap<>();
                    List<String> busy = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String time = doc.getString("time");
                        counts.put(time, counts.getOrDefault(time, 0) + 1);
                    }

                    for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                        if (entry.getValue() >= 3) busy.add(entry.getKey());
                    }

                    if(busy.isEmpty()) {
                        txtBusySlots.setText("All slots available!");
                        txtBusySlots.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else {
                        txtBusySlots.setText("Fully Booked: " + busy.toString());
                        txtBusySlots.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                });
    }

    private void checkSlotAvailability(String date, String time) {
        // Get the current logged-in user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query Firestore to find bookings for the selected date and time
        db.collection("Bookings")
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {

                        boolean alreadyBookedByMe = false;
                        int count = task.getResult().size();

                        // Check if any existing booking in this slot belongs to the current user
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String bookedUserId = doc.getString("userId");
                            if (bookedUserId != null && bookedUserId.equals(currentUserId)) {
                                alreadyBookedByMe = true;
                                break;
                            }
                        }


                        if (alreadyBookedByMe) {
                            // Prevention: Prevent the same user from booking the same slot twice
                            Toast.makeText(this, "You have already booked an appointment for this time!", Toast.LENGTH_LONG).show();
                        } else if (count >= 3) {
                            // Capacity Control: Check if the slot reached its maximum capacity (3 people)
                            Toast.makeText(this, "This slot is full (3/3). Please choose another time.", Toast.LENGTH_LONG).show();
                        } else {
                            // Success: Slot is available and user hasn't booked it yet
                            navigateToPayment();
                        }

                    } else {
                        Log.e("FirestoreError", "Availability check failed", task.getException());
                        Toast.makeText(this, "Error checking availability.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("servicePrice", servicePrice);
        intent.putExtra("date", selectedDate);
        intent.putExtra("time", selectedTime);
        startActivity(intent);
    }
}