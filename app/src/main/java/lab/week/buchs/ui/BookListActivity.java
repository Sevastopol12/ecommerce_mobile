package lab.week.buchs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lab.week.buchs.R;
import lab.week.buchs.books.Book;
import lab.week.buchs.database.AppDatabase;

public class BookListActivity extends AppCompatActivity {

    private static final String TAG = "BookListActivity";

    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private AppDatabase appDb;
    private FirebaseFirestore db;
    private LinearLayout emptyView;
    private String currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        TextView categoryTitle = findViewById(R.id.category_title);
        booksRecyclerView = findViewById(R.id.books_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        Button retryButton = findViewById(R.id.retry_button);

        Intent intent = getIntent();
        currentCategory = intent.getStringExtra("category");

        if (currentCategory != null) {
            Log.d(TAG, "Category received: " + currentCategory);
            categoryTitle.setText(currentCategory);
            appDb = AppDatabase.getDatabase(this);
            db = FirebaseFirestore.getInstance();
            initRecyclerView();
            observeBooks(currentCategory);
            fetchBooksFromFirebase(currentCategory);
            retryButton.setOnClickListener(v -> fetchBooksFromFirebase(currentCategory));
        } else {
            Log.e(TAG, "Category is null. Cannot load books.");
            showEmptyView(true);
        }
    }

    private void initRecyclerView() {
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setAdapter(bookAdapter);
        Log.d(TAG, "RecyclerView initialized.");
    }

    private void observeBooks(String category) {
        Log.d(TAG, "Setting up database observer for category: " + category);
        appDb.bookDao().getBooksByCategory(category).observe(this, books -> {
            Log.d(TAG, "Database observer triggered. " + books.size() + " books found in local DB.");
            bookList.clear();
            bookList.addAll(books);
            bookAdapter.notifyDataSetChanged();
            showEmptyView(books.isEmpty());
        });
    }

    private void fetchBooksFromFirebase(String category) {
        Log.d(TAG, "Fetching books from Firebase for category: " + category);
        db.collection("books")
                .whereEqualTo("category", category)
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
                        showEmptyView(bookList.isEmpty());
                    }
                });
    }

    private void cacheBooks(List<Book> books) {
        Log.d(TAG, "Caching " + books.size() + " books to local database.");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            appDb.bookDao().deleteByCategory(currentCategory);
            appDb.bookDao().insertAll(books);
        });
    }

    private void showEmptyView(boolean show) {
        if (show) {
            emptyView.setVisibility(View.VISIBLE);
            booksRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            booksRecyclerView.setVisibility(View.VISIBLE);
        } 
    }
}
