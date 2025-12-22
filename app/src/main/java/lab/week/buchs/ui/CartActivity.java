package lab.week.buchs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.Serializable;
import java.util.ArrayList;
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

    private final ActivityResultLauncher<Intent> checkoutLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                cartItems.clear();
                cartAdapter.notifyDataSetChanged();
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        checkoutButton = findViewById(R.id.checkout_button);
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this::removeFromCart);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        checkoutButton.setOnClickListener(v -> openCheckout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCartItems();
    }

    private void fetchCartItems() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in!", Toast.LENGTH_SHORT).show();
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
                            Map<String, Object> item = document.getData();
                            item.put("documentId", document.getId()); // Keep track of doc ID
                            cartItems.add(item);
                        }
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Shopping cart loading error!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromCart(String documentId) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Removed from My Cart!", Toast.LENGTH_SHORT).show();
                    fetchCartItems(); // Refresh list
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Empty cart!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("cartItems", (Serializable) cartItems);
        intent.putExtra("totalPrice", calculateTotal());
        checkoutLauncher.launch(intent);
    }

    private double calculateTotal() {
        double total = 0;
        for (Map<String, Object> item : cartItems) {
            Object priceObj = item.get("price");
            Object quantityObj = item.get("quantity");
            if (priceObj instanceof Number && quantityObj instanceof Number) {
                total += ((Number) priceObj).doubleValue() * ((Number) quantityObj).longValue();
            }
        }
        return total;
    }
}
