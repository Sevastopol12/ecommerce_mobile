package lab.week.buchs.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import lab.week.buchs.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Category navigation
        MaterialCardView category1 = findViewById(R.id.category1);
        MaterialCardView category2 = findViewById(R.id.category2);
        MaterialCardView category3 = findViewById(R.id.category3);
        MaterialCardView category4 = findViewById(R.id.category4);
        MaterialCardView category5 = findViewById(R.id.category5);
        MaterialCardView category6 = findViewById(R.id.category6);
        MaterialCardView category7 = findViewById(R.id.category7);
        MaterialCardView category8 = findViewById(R.id.category8);

        category1.setOnClickListener(v -> openBookList("Education"));
        category2.setOnClickListener(v -> openBookList("Literature"));
        category3.setOnClickListener(v -> openBookList("Children"));
        category4.setOnClickListener(v -> openBookList("Business"));
        category5.setOnClickListener(v -> openBookList("Arts & Design"));
        category6.setOnClickListener(v -> openBookList("Politics"));
        category7.setOnClickListener(v -> openBookList("Science"));
        category8.setOnClickListener(v -> openBookList("Technology"));
    }

    private void openBookList(String category) {
        Intent intent = new Intent(this, BookListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
