package lk.tgcompany.stylesync.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import lk.tgcompany.stylesync.R;
import lk.tgcompany.stylesync.activity.BookingActivity;
import lk.tgcompany.stylesync.models.ServiceModel;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceModel> serviceList;

    public ServiceAdapter(List<ServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_service.xml layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceModel service = serviceList.get(position);

        // Bind data to UI elements
        holder.name.setText(service.getName());
        holder.price.setText("Rs. " + service.getPrice());

        // Handle "Book Now" button click
        holder.btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookingActivity.class);
            // Passing service details to the next activity
            intent.putExtra("serviceName", service.getName());
            intent.putExtra("servicePrice", service.getPrice());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;
        Button btnBook;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtServiceName);
            price = itemView.findViewById(R.id.txtServicePrice);
            image = itemView.findViewById(R.id.imgService);
            btnBook = itemView.findViewById(R.id.btnBookService);
        }
    }
}