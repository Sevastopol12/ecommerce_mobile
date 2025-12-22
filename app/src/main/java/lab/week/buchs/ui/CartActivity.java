package lab.week.buchs.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lab.week.buchs.R;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Map<String, Object>> cartItems;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        checkoutButton = findViewById(R.id.checkout_button);
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this::removeFromCart);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        fetchCartItems();

        checkoutButton.setOnClickListener(v -> checkout());
    }

    private void fetchCartItems() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cartItems.add(document.getData());
                        }
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi tải giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromCart(String bookName) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart").document(bookName)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    fetchCartItems(); // Refresh list
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> order = new HashMap<>();
        order.put("items", cartItems);
        order.put("total", calculateTotal());
        order.put("timestamp", System.currentTimeMillis());

        db.collection("users").document(userId).collection("orders").add(order)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    clearCart();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi đặt hàng", Toast.LENGTH_SHORT).show());
    }

    private double calculateTotal() {
        double total = 0;
        for (Map<String, Object> item : cartItems) {
            total += (double) item.get("price") * (long) item.get("quantity");
        }
        return total;
    }

    private void clearCart() {
        String userId = mAuth.getCurrentUser().getUid();
        for (Map<String, Object> item : cartItems) {
            db.collection("users").document(userId).collection("cart").document((String) item.get("bookName")).delete();
        }
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
    }
}