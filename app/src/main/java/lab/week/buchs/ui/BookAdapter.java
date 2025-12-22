package lab.week.buchs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import lab.week.buchs.R;
import lab.week.buchs.books.Book;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookName.setText(book.getName());
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookPrice.setText(String.format("$%.2f", book.getPrice()));
        holder.addToCartButton.setOnClickListener(v -> {
            AddToCart bottomSheet = AddToCart.newInstance(book);
            bottomSheet.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "AddToCart");
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCover;
        TextView bookName;
        TextView bookAuthor;
        TextView bookPrice;
        MaterialButton addToCartButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.book_cover);
            bookName = itemView.findViewById(R.id.book_name);
            bookAuthor = itemView.findViewById(R.id.book_author);
            bookPrice = itemView.findViewById(R.id.book_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}