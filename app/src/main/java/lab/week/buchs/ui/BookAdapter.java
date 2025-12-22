package lab.week.buchs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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
        holder.bookDescription.setText(book.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(book.getCoverUrl())
                .placeholder(R.drawable.ic_book) // optional placeholder
                .error(R.drawable.ic_book) // optional error image
                .into(holder.bookCover);

        holder.bookDescription.setVisibility(book.isExpanded() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            boolean isExpanded = book.isExpanded();
            holder.bookDescription.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            book.setExpanded(!isExpanded);
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
        TextView bookDescription;
        MaterialButton addToCartButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.book_cover);
            bookName = itemView.findViewById(R.id.book_name);
            bookAuthor = itemView.findViewById(R.id.book_author);
            bookPrice = itemView.findViewById(R.id.book_price);
            bookDescription = itemView.findViewById(R.id.book_description);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}
