package it_school.sumdu.edu.ua.tanks.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it_school.sumdu.edu.ua.tanks.LoginActivity;
import it_school.sumdu.edu.ua.tanks.MainActivity;
import it_school.sumdu.edu.ua.tanks.SessionManager;
import it_school.sumdu.edu.ua.tanks.databinding.FragmentLogoutBinding;

public class LogoutFragment extends Fragment {
    private FragmentLogoutBinding binding;
    private Button btnLogout;
    private static SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnLogout = binding.btnLogout;
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void setSessionManager(SessionManager sesManager) {
        sessionManager = sesManager;
    }

    public void logout() {
        sessionManager.logout();
        Intent loginIntent = new Intent(MainActivity.getContext(), LoginActivity.class);
        startActivity(loginIntent);
    }
}
