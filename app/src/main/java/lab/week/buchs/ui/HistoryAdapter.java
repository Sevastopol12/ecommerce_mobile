package lab.week.buchs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lab.week.buchs.R;
import lab.week.buchs.local_data.Order;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.OrderViewHolder> {

    private List<Order> orders;

    public HistoryAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderId, orderDate, orderStatus, orderTotal;
        private RecyclerView productsRecyclerView;
        private ProductAdapter productAdapter;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderTotal = itemView.findViewById(R.id.order_total);
            productsRecyclerView = itemView.findViewById(R.id.products_recycler_view);
        }

        public void bind(Order order) {
            orderId.setText("Order ID: " + order.ID);
            orderStatus.setText("Status: " + order.status);
            orderTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", order.total_price));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = sdf.format(new Date(order.timestamp));
            orderDate.setText("Date: " + date);

            productsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            productAdapter = new ProductAdapter(order.products);
            productsRecyclerView.setAdapter(productAdapter);

            itemView.setOnClickListener(v -> {
                if (productsRecyclerView.getVisibility() == View.GONE) {
                    productsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    productsRecyclerView.setVisibility(View.GONE);
                }
            });
        }
    }
}
