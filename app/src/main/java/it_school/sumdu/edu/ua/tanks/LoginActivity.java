package it_school.sumdu.edu.ua.tanks;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it_school.sumdu.edu.ua.tanks.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    ActivityLoginBinding binding;
    private SessionManager sessionManager;

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.101:1433/Tanks";
    private static final String DB_USER = "test"; // Leave it empty for Windows Authentication
    private static final String DB_PASSWORD = "test"; // Leave it empty for Windows Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usernameEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        sessionManager = new SessionManager(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                new AuthenticateTask().execute(username, password);
            }
        });

        binding.signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class AuthenticateTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... credentials) {
            String username = credentials[0];
            String password = credentials[1];

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet resultSet = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                System.out.println("OKK");
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);

                resultSet = stmt.executeQuery();
                boolean isAuthenticated = resultSet.next();

                return isAuthenticated;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isAuthenticated) {
            if (isAuthenticated) {
                sessionManager.setLoggedIn(true);
                sessionManager.setUsername(usernameEditText.getText().toString());
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(loginIntent);
                finish();
            } else {
            }
        }
    }
}

