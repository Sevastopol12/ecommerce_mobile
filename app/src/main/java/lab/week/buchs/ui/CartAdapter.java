package lab.week.buchs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lab.week.buchs.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Map<String, Object>> cartItems;
    private Consumer<String> removeCallback;

    public CartAdapter(List<Map<String, Object>> cartItems, Consumer<String> removeCallback) {
        this.cartItems = cartItems;
        this.removeCallback = removeCallback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map<String, Object> item = cartItems.get(position);
        String name = (String) item.get("bookName");
        String author = (String) item.get("author");
        double price = (double) item.get("price");
        long quantity = (long) item.get("quantity");

        holder.bookName.setText(name);
        holder.bookAuthor.setText(author);
        holder.bookPrice.setText(String.format("$%.2f", price));
        holder.quantity.setText("Quantity: " + quantity);
        holder.totalItem.setText(String.format("Total: $%.2f", price * quantity));

        holder.removeButton.setOnClickListener(v -> removeCallback.accept(name));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView bookName, bookAuthor, bookPrice, quantity, totalItem;
        MaterialButton removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.cart_book_name);
            bookAuthor = itemView.findViewById(R.id.cart_book_author);
            bookPrice = itemView.findViewById(R.id.cart_book_price);
            quantity = itemView.findViewById(R.id.cart_quantity);
            totalItem = itemView.findViewById(R.id.cart_total_item);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}