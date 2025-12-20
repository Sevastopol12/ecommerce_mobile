package lab.week.buchs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Book> books);

    @Query("SELECT * FROM books WHERE category = :category")
    LiveData<List<Book>> getBooksByCategory(String category);

    @Query("DELETE FROM books WHERE category = :category")
    void deleteByCategory(String category);
}
