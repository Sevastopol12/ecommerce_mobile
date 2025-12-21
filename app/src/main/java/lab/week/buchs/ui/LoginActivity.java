package lab.week.buchs.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import lab.week.buchs.R;
import lab.week.buchs.ui.HomeActivity;
import lab.week.buchs.ui.RegisterActivity;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput, passwordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.login_button).setOnClickListener(v -> loginUser());

        findViewById(R.id.go_to_register_text).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Toast.makeText(this, "Đăng nhập thất bại: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}