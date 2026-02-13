package lk.tgcompany.stylesync.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import lk.tgcompany.stylesync.R;
import lk.tgcompany.stylesync.models.BookingModel;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingModel> bookingList;

    public BookingAdapter(List<BookingModel> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);

        holder.serviceName.setText(booking.getServiceName());
        holder.dateTime.setText(booking.getDate() + " at " + booking.getTime());
        holder.price.setText("Rs. " + booking.getPrice());
        holder.status.setText(booking.getStatus());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, dateTime, price, status;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.txtBookingServiceName);
            dateTime = itemView.findViewById(R.id.txtBookingDateTime);
            price = itemView.findViewById(R.id.txtBookingPrice);
            status = itemView.findViewById(R.id.txtBookingStatus);
        }
    }
}