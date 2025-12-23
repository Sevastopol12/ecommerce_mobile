package lab.week.buchs.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import lab.week.buchs.books.Book;
import lab.week.buchs.books.BookDao;
import lab.week.buchs.local_data.Order;
import lab.week.buchs.local_data.OrderDao;

@Database(entities = {Book.class, Order.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
    public abstract OrderDao orderDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "book_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
