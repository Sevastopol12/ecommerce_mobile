package lab.week.buchs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
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

    private static final String TAG = "HomeActivity";
    private AppDatabase appDb;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appDb = AppDatabase.getDatabase(this);
        db = FirebaseFirestore.getInstance();

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

    private void fetchAllBooksAndCache() {
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Book> fetchedBooks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
                            fetchedBooks.add(book);
                        }
                        Log.d(TAG, "Firebase fetch successful. " + fetchedBooks.size() + " books fetched.");
                        cacheBooks(fetchedBooks);
                    } else {
                        Log.w(TAG, "Error getting documents from Firebase.", task.getException());
                    }
                });
    }

    private void cacheBooks(List<Book> books) {
        Log.d(TAG, "Caching " + books.size() + " books to local database.");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            appDb.bookDao().deleteAll();
            appDb.bookDao().insertAll(books);
        });
    }
}
