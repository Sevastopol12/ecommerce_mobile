package lab.week.buchs.history;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import lab.week.buchs.R;
import lab.week.buchs.local_data.Order;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView ordersRecyclerView;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUserOrders();
    }

    private void loadUserOrders() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("order_list")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        return;
                    }

                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        Order order = doc.toObject(Order.class);
                        orders.add(order);
                    }

                    historyAdapter = new HistoryAdapter(orders);
                    ordersRecyclerView.setAdapter(historyAdapter);
                });
    }
}
