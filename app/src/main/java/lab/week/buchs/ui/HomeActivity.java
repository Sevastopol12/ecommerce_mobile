package lab.week.buchs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lab.week.buchs.R;
import lab.week.buchs.books.Book;
import lab.week.buchs.database.AppDatabase;

public class HomeActivity extends AppCompatActivity {

    private AppDatabase appDb;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView searchResultsRecyclerView;
    private BookAdapter bookAdapter;
    private NestedScrollView contentScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already home
            } else if (id == R.id.nav_my_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        appDb = AppDatabase.getDatabase(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullname = documentSnapshot.getString("fullname");
                            String email = documentSnapshot.getString("email");

                            View headerView = navigationView.getHeaderView(0);
                            TextView navTitle = headerView.findViewById(R.id.nav_header_title);
                            TextView navSubtitle = headerView.findViewById(R.id.nav_header_subtitle);

                            if (fullname != null) navTitle.setText(fullname);
                            if (email != null) navSubtitle.setText(email);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error loading user information!", Toast.LENGTH_SHORT).show());
        }

        fetchAllBooksAndCache();

        contentScrollView = findViewById(R.id.content_scroll_view);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchView searchView = findViewById(R.id.search_view);
        setupSearchView(searchView);

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

    private void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showDefaultContent();
                } else {
                    searchBooks(newText);
                }
                return true;
            }
        });
    }

    private void searchBooks(String query) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Book> allBooks = appDb.bookDao().getAllBooks();
            String lowerCaseQuery = query.toLowerCase();
            List<Book> searchResults = allBooks.stream()
                    .filter(book -> book.getName().toLowerCase().contains(lowerCaseQuery) ||
                                   book.getAuthor().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());

            runOnUiThread(() -> displaySearchResults(searchResults));
        });
    }

    private void displaySearchResults(List<Book> books) {
        bookAdapter = new BookAdapter(books);
        searchResultsRecyclerView.setAdapter(bookAdapter);
        searchResultsRecyclerView.setVisibility(View.VISIBLE);
        contentScrollView.setVisibility(View.GONE);
    }

    private void showDefaultContent() {
        searchResultsRecyclerView.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.VISIBLE);
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
