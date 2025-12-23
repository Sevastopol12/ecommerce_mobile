package lab.week.buchs.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lab.week.buchs.R;
import lab.week.buchs.database.AppDatabase;

public class BookListActivity extends AppCompatActivity {

    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private AppDatabase appDb;
    private LinearLayout emptyView;
    private String currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        TextView categoryTitle = findViewById(R.id.category_title);
        booksRecyclerView = findViewById(R.id.books_recycler_view);
        emptyView = findViewById(R.id.empty_view);

        Intent intent = getIntent();
        currentCategory = intent.getStringExtra("category");

        if (currentCategory != null) {
            categoryTitle.setText(currentCategory);
            appDb = AppDatabase.getDatabase(this);
            initRecyclerView();
            observeBooks(currentCategory);
        } else {
            showEmptyView(true);
        }
    }

    private void initRecyclerView() {
        int columns = getResources().getInteger(R.integer.book_list_columns);
        RecyclerView.LayoutManager layoutManager;
        if (columns > 1) {
            layoutManager = new GridLayoutManager(this, columns);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }

        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList);
        booksRecyclerView.setLayoutManager(layoutManager);
        booksRecyclerView.setAdapter(bookAdapter);
    }

    private void observeBooks(String category) {
        appDb.bookDao().getBooksByCategory(category).observe(this, books -> {
            bookList.clear();
            bookList.addAll(books);
            bookAdapter.notifyDataSetChanged();
            showEmptyView(books.isEmpty());
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
