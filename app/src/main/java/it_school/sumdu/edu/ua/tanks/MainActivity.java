package it_school.sumdu.edu.ua.tanks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

import it_school.sumdu.edu.ua.tanks.databinding.ActivityMainBinding;
import it_school.sumdu.edu.ua.tanks.ui.favtank.FavFragment;
import it_school.sumdu.edu.ua.tanks.ui.home.HomeFragment;
import it_school.sumdu.edu.ua.tanks.ui.logout.LogoutFragment;


public class MainActivity extends AppCompatActivity {
    private static Context context;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();



        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            HomeFragment.setSessionManager(sessionManager);
            FavFragment.setSessionManager(sessionManager);
            LogoutFragment.setSessionManager(sessionManager);

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setSupportActionBar(binding.appBarMain.toolbar);

            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_favtank)
                    .setOpenableLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

        } else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                if (navHostFragment != null) {
                    HomeFragment homeFragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    if (homeFragment != null) {
                        homeFragment.performSearch(query);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                if (navHostFragment != null) {
                    HomeFragment homeFragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    if (homeFragment != null) {
                        homeFragment.performSearch(query);
                    }
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void logout() {
        sessionManager.logout();
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


    public static Context getContext() {
        return context;
    }
}
