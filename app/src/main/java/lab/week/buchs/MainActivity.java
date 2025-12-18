package lab.week.buchs;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra trạng thái đăng nhập
        if (mAuth.getCurrentUser() != null) {
            // Đã đăng nhập, đi đến Home
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            // Chưa đăng nhập, đi đến Login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        finish();  // Đóng MainActivity để không quay lại
    }
}