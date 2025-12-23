package lab.week.buchs.local_data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "orders")
@TypeConverters(Converters.class)
public class Order {
    @PrimaryKey
    @NonNull
    public String ID;
    public String userId;
    public String customer_name;
    public String address;
    public String phone;
    public String email;
    public List<Product> products;
    public double total_price;
    public String status;
    public long timestamp;
}