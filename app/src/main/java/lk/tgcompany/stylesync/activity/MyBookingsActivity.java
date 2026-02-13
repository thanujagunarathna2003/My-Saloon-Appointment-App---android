package lk.tgcompany.stylesync.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import lk.tgcompany.stylesync.R;
import lk.tgcompany.stylesync.adapter.BookingAdapter;
import lk.tgcompany.stylesync.models.BookingModel;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView rvMyBookings;
    private FirebaseFirestore db;
    private List<BookingModel> myBookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        rvMyBookings = findViewById(R.id.rvMyBookings);
        rvMyBookings.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        myBookingList = new ArrayList<>();

        loadMyBookings();
    }

    private void loadMyBookings() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query: Get only bookings where userId matches current user
        db.collection("Bookings")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myBookingList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BookingModel booking = document.toObject(BookingModel.class);
                            myBookingList.add(booking);
                        }
                        // Reuse or Create an Adapter
                        BookingAdapter adapter = new BookingAdapter(myBookingList);
                        rvMyBookings.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}