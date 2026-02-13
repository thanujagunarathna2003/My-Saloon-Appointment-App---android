package lk.tgcompany.stylesync.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import lk.tgcompany.stylesync.R;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

import lk.tgcompany.stylesync.adapter.ServiceAdapter;
import lk.tgcompany.stylesync.models.ServiceModel;

public class CustomerMainActivity extends AppCompatActivity {

    private RecyclerView rvServices;
    private FirebaseFirestore fStore;
    private List<ServiceModel> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        // Initialize Firestore
        fStore = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));

        serviceList = new ArrayList<>();

        // Fetch services from Firestore collection "Services"
        loadServices();

        // Initialize buttons
        Button btnMyBookings = findViewById(R.id.btnGoToMyBookings);
        ImageButton btnLogout = findViewById(R.id.btnLogout);

        ImageButton btnCall = findViewById(R.id.btnCallSaloon);
        btnCall.setOnClickListener(v -> {
            String saloonNumber = "0712345678";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + saloonNumber));
            startActivity(intent);
        });

// My Bookings Navigation
        btnMyBookings.setOnClickListener(v -> {
            startActivity(new Intent(CustomerMainActivity.this, MyBookingsActivity.class));
        });

// Logout Logic
        btnLogout.setOnClickListener(v -> {
            // 1. Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // 2. Redirect to Login Screen
            Intent intent = new Intent(CustomerMainActivity.this, LoginActivity.class);
            // Clear the activity stack so user can't go back
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

    }
    private void loadServices() {
        // 1. Fetching data from "Services" collection in Firestore
        fStore.collection("Services").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 2. Clear the list to prevent duplicate data on refresh
                serviceList.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    // 3. Convert Firestore document to ServiceModel object
                    ServiceModel service = document.toObject(ServiceModel.class);

                    // If you want to store the Document ID in your model:
                    // service.setServiceId(document.getId());

                    serviceList.add(service);
                }

                // 4. Initialize the adapter and set it to RecyclerView
                ServiceAdapter adapter = new ServiceAdapter(serviceList);
                rvServices.setAdapter(adapter);

                // 5. Notify the adapter that data has changed
                adapter.notifyDataSetChanged();

            } else {
                // Show error message if data fetching fails
                Toast.makeText(this, "Error fetching services: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}