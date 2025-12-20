package lab.week.buchs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        boolean isExpanded = book.isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            book.setExpanded(!isExpanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookName;
        TextView bookAuthor;
        TextView bookPrice;
        TextView bookDescription;
        LinearLayout expandableLayout;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.book_name);
            bookAuthor = itemView.findViewById(R.id.book_author);
            bookPrice = itemView.findViewById(R.id.book_price);
            bookDescription = itemView.findViewById(R.id.book_description);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
        }
    }
}
