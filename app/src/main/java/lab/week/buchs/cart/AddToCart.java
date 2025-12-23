package lab.week.buchs.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import lab.week.buchs.R;
import lab.week.buchs.book.Book;

public class AddToCart extends BottomSheetDialogFragment {

    private Book book;
    private int quantity = 1;
    private TextView tvQuantity, tvTotalPrice;
    private double totalPrice;

    public static AddToCart newInstance(Book book) {
        AddToCart fragment = new AddToCart();
        Bundle args = new Bundle();
        args.putString("name", book.getName());
        args.putString("author", book.getAuthor());
        args.putDouble("price", book.getPrice());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_to_cart, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name");
            double price = args.getDouble("price");

            TextView tvBookName = view.findViewById(R.id.dialog_book_name);
            TextView tvBookPrice = view.findViewById(R.id.dialog_book_price);
            tvQuantity = view.findViewById(R.id.tv_quantity);
            tvTotalPrice = view.findViewById(R.id.dialog_total_price);
            MaterialButton btnDecrease = view.findViewById(R.id.btn_decrease);
            MaterialButton btnIncrease = view.findViewById(R.id.btn_increase);
            MaterialButton btnAdd = view.findViewById(R.id.btn_add_to_cart);

            tvBookName.setText(name);
            tvBookPrice.setText(String.format("Price: $%.2f", price));
            updateTotal(price);

            btnDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                    updateTotal(price);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotal(price);
            });

            btnAdd.setOnClickListener(v -> addToCart(name, args.getString("author"), price));
        }

        return view;
    }

    private void updateTotal(double price) {
        totalPrice = quantity * price;
        tvTotalPrice.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void addToCart(String name, String author, double price) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("bookName", name);
            cartItem.put("author", author);
            cartItem.put("price", price);
            cartItem.put("quantity", quantity);

            db.collection("users").document(userId).collection("cart").document(name)
                    .set(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Added to My Cart!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Please log in!", Toast.LENGTH_SHORT).show();
        }
    }
}