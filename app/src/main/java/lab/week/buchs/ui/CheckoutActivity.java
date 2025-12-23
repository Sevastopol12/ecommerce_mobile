package lab.week.buchs.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lab.week.buchs.R;

public class CheckoutActivity extends AppCompatActivity {

    private EditText recipientName, email, address, phoneNumber;
    private Button confirmOrderButton, backButton;
    private TextView totalPriceTextView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<Map<String, Object>> cartItems;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recipientName = findViewById(R.id.recipient_name);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        confirmOrderButton = findViewById(R.id.confirm_order_button);
        backButton = findViewById(R.id.back_button_checkout);
        totalPriceTextView = findViewById(R.id.total_price_checkout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get data from Intent
        cartItems = (List<Map<String, Object>>) getIntent().getSerializableExtra("cartItems");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);

        totalPriceTextView.setText(String.format("Total amount: $%.2f", totalPrice));

        loadUserInfo();

        confirmOrderButton.setOnClickListener(v -> confirmOrder());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                recipientName.setText(user.getDisplayName());
            }
            email.setText(user.getEmail());
        }
    }

    private void confirmOrder() {
        confirmOrderButton.setEnabled(false);

        String name = recipientName.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        String shippingAddress = address.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(shippingAddress) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill in all the information.", Toast.LENGTH_SHORT).show();
            confirmOrderButton.setEnabled(true);
            return;
        }

        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "There are no products available to order.", Toast.LENGTH_SHORT).show();
            confirmOrderButton.setEnabled(true);
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        List<Map<String, Object>> products = new ArrayList<>();
        for (Map<String, Object> item : cartItems) {
            Map<String, Object> product = new HashMap<>();
            product.put("book_name", item.get("bookName"));
            product.put("quantity", item.get("quantity"));

            double price = 0.0;
            if (item.get("price") instanceof Number) {
                price = ((Number) item.get("price")).doubleValue();
            }

            long quantity = 0;
            if (item.get("quantity") instanceof Number) {
                quantity = ((Number) item.get("quantity")).longValue();
            }
            product.put("total_price", price * quantity);
            products.add(product);
        }

        long timestamp = System.currentTimeMillis();
        String orderId = String.valueOf(timestamp);

        Map<String, Object> order = new HashMap<>();
        order.put("ID", orderId);
        order.put("userId", userId);
        order.put("customer_name", name);
        order.put("address", shippingAddress);
        order.put("phone", phone);
        order.put("email", emailAddress);
        order.put("products", products);
        order.put("total_price", totalPrice);
        order.put("status", "Pending");
        order.put("timestamp", timestamp);

        db.collection("order_list").document(orderId).set(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order successfully!", Toast.LENGTH_SHORT).show();
                    clearCartAndFinish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    confirmOrderButton.setEnabled(true);
                });
    }

    private void clearCartAndFinish() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                WriteBatch batch = db.batch();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    batch.delete(doc.getReference());
                }
                batch.commit().addOnCompleteListener(batchTask -> {
                    setResult(RESULT_OK);
                    finish();
                });
            } else {
                 setResult(RESULT_OK); // Still finish, order was placed
                 finish();
            }
        });
    }
}
