package lk.tgcompany.stylesync.activity;

import android.os.Bundle;
import lk.tgcompany.stylesync.R;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import lk.tgcompany.stylesync.R;

public class SaloonLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_location);

        // Find the map fragment and notify when it's ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Coordinates for the Saloon (Example: Colombo)
        LatLng saloonLocation = new LatLng(6.9271, 79.8612);

        // Add Marker
        mMap.addMarker(new MarkerOptions().position(saloonLocation).title("StyleSync Saloon"));

        // Move Camera and Zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saloonLocation, 15f));
    }
}