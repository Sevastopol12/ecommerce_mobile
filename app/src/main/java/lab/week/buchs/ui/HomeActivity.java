package lab.week.buchs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lab.week.buchs.R;
import lab.week.buchs.books.Book;
import lab.week.buchs.database.AppDatabase;

public class HomeActivity extends AppCompatActivity {

    private AppDatabase appDb;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appDb = AppDatabase.getDatabase(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        fetchAllBooksAndCache();

        // Category navigation
        MaterialCardView category1 = findViewById(R.id.category1);
        MaterialCardView category2 = findViewById(R.id.category2);
        MaterialCardView category3 = findViewById(R.id.category3);
        MaterialCardView category4 = findViewById(R.id.category4);
        MaterialCardView category5 = findViewById(R.id.category5);
        MaterialCardView category6 = findViewById(R.id.category6);
        MaterialCardView category7 = findViewById(R.id.category7);
        MaterialCardView category8 = findViewById(R.id.category8);
        MaterialCardView category9 = findViewById(R.id.category9);
        MaterialCardView category10 = findViewById(R.id.category10);
        MaterialCardView category11 = findViewById(R.id.category11);
        MaterialCardView category12 = findViewById(R.id.category12);

        category1.setOnClickListener(v -> openBookList("Education"));
        category2.setOnClickListener(v -> openBookList("Business"));
        category3.setOnClickListener(v -> openBookList("Self-Help"));
        category4.setOnClickListener(v -> openBookList("Children"));
        category5.setOnClickListener(v -> openBookList("Literature"));
        category6.setOnClickListener(v -> openBookList("Science"));
        category7.setOnClickListener(v -> openBookList("Politics"));
        category8.setOnClickListener(v -> openBookList("History"));
        category9.setOnClickListener(v -> openBookList("Technology"));
        category10.setOnClickListener(v -> openBookList("Health & Wellness"));
        category11.setOnClickListener(v -> openBookList("Arts & Design"));
        category12.setOnClickListener(v -> openBookList("Novel"));

        findViewById(R.id.logout_button).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            Toast.makeText(this, "Refreshing book data...", Toast.LENGTH_SHORT).show();
            fetchAllBooksAndCache();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openBookList(String category) {
        Intent intent = new Intent(this, BookListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void fetchAllBooksAndCache() {
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> fetchedBooks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
                            String coverUrl = document.getString("book_cover");
                            book.setCoverUrl(coverUrl);
                            fetchedBooks.add(book);
                        }
                        cacheBooks(fetchedBooks);
                    } else {
                        // Handle the error
                    }
                });
    }

    private void cacheBooks(List<Book> books) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            appDb.bookDao().deleteAll();
            appDb.bookDao().insertAll(books);
        });
    }
}
