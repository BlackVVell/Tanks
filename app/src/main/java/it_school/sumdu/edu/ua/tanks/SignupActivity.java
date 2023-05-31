package it_school.sumdu.edu.ua.tanks;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import it_school.sumdu.edu.ua.tanks.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private Button signupButton;
    ActivitySignupBinding binding;
    private SessionManager sessionManager;

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.101:1433/Tanks";
    private static final String DB_USER = "test"; // Leave it empty for Windows Authentication
    private static final String DB_PASSWORD = "test"; // Leave it empty for Windows Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usernameEditText = findViewById(R.id.signup_email);
        passwordEditText = findViewById(R.id.signup_password);
        confirmEditText = findViewById(R.id.signup_confirm);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirm = confirmEditText.getText().toString();

                System.out.println(password + "   " + confirm);

                if(password.equals(confirm)) {
                    System.out.println("Ok");
                    new SignupTask().execute(username, password);
                }

            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private class SignupTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... credentials) {
            String username = credentials[0];
            String password = credentials[1];

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                String check = "SELECT * FROM users WHERE username = ?";
                PreparedStatement statement = conn.prepareStatement(check);
                statement.setString(1, username);
                System.out.println("OK" + statement.executeQuery().next());
                if(!statement.executeQuery().next()) {
                    String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                    stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    stmt.setString(2, password);

                    int rowsAffected = stmt.executeUpdate();
                    return rowsAffected > 0;
                }
                else {
                    return false;
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isRegistered) {
            if (isRegistered) {
                Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
