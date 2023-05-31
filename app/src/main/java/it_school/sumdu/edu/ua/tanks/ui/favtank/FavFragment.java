package it_school.sumdu.edu.ua.tanks.ui.favtank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it_school.sumdu.edu.ua.tanks.ListAdapter;
import it_school.sumdu.edu.ua.tanks.MainActivity;
import it_school.sumdu.edu.ua.tanks.SessionManager;
import it_school.sumdu.edu.ua.tanks.Tank;
import it_school.sumdu.edu.ua.tanks.TankActivity;
import it_school.sumdu.edu.ua.tanks.databinding.FragmentFavtankBinding;

public class FavFragment extends Fragment {
    private FragmentFavtankBinding binding;
    private RecyclerView mListTanks;
    private ListAdapter listAdapter;
    private static final List<Tank> tanks = new ArrayList<>();
    private static SessionManager sessionManager;
    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.101:1433/Tanks";
    private static final String DB_USER = "test"; // Leave it empty for Windows Authentication
    private static final String DB_PASSWORD = "test"; // Leave it empty for Windows Authentication
    private static final String BASE_URL = "https://api.wotblitz.eu/wotb/encyclopedia/vehicles/";
    private static final String APPLICATION_ID = "1a09db49cec194ebb840d3a8b1d83a40";
    private List<Integer> tanks_Id = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavtankBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mListTanks = binding.recyclerView;

        new checkLikedTask().execute(sessionManager.getUsername());
        readTanks();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tanks.clear();
        binding = null;
    }

    public static void setSessionManager(SessionManager sesManager) {
        sessionManager = sesManager;
    }

    public void performSearch(String query) {
        List<Tank> filteredTanks = new ArrayList<>();
        for (Tank tank : tanks) {
            if (tank.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredTanks.add(tank);
            }
        }
        listAdapter = new ListAdapter(
                MainActivity.getContext(),
                filteredTanks,
                tank -> {
                    Intent intent = new Intent(requireContext(), TankActivity.class);
                    intent.putExtra(TankActivity.TANK, tank);
                    startActivity(intent);
                },
                sessionManager
        );
        mListTanks.setAdapter(listAdapter);
    }

    private void readTanks() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = BASE_URL + "?application_id=" + APPLICATION_ID;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject dataObject = jsonResponse.getJSONObject("data");
                            Iterator<String> keys = dataObject.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                JSONObject tankObject = dataObject.getJSONObject(key);
                                JSONObject tankImageObject = tankObject.getJSONObject("images");

                                int tier = tankObject.getInt("tier");
                                int tankId = tankObject.getInt("tank_id");
                                String description = tankObject.getString("description");
                                String name = tankObject.getString("name");
                                String nation = tankObject.getString("nation");
                                String type = tankObject.getString("type");
                                String image = tankImageObject.getString("normal");
                                boolean isLiked = true;
                                for (Integer id : tanks_Id) {
                                    if(id == tankId) {
                                        Tank tank = new Tank(tankId, name, description, nation, tier, image, type, isLiked);
                                        tanks.add(tank);
                                        break;
                                    }
                                }
                            }
                            listAdapter = new ListAdapter(
                                    MainActivity.getContext(),
                                    tanks,
                                    tank -> {
                                        Intent intent = new Intent(requireContext(), TankActivity.class);
                                        intent.putExtra(TankActivity.TANK, tank);
                                        startActivity(intent);
                                    },
                                    sessionManager
                            );
                            mListTanks.setAdapter(listAdapter);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(request);
    }

    private class checkLikedTask extends AsyncTask<String, Void, List<Integer>> {

        @Override
        protected List<Integer> doInBackground(String... credentials) {
            String username = credentials[0];

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet resultSet = null;
            List<Integer> tankIds = new ArrayList<>();
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                System.out.println("OKK");
                String query = "SELECT * FROM favorite_tanks WHERE username = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);

                resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    int tankId = resultSet.getInt("tank_id");
                    tankIds.add(tankId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            return tankIds;
        }

        @Override
        protected void onPostExecute(List<Integer> tankIds) {
            tanks_Id.addAll(tankIds);
        }
    }
}