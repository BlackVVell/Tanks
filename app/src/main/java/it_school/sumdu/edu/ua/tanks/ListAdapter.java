package it_school.sumdu.edu.ua.tanks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    public interface OnItemClick {
        void OnItemClicked(Tank tank);
    }

    private List<Tank> mTankList;
    private final LayoutInflater mInflater;
    private final OnItemClick mOnItemClick;
    private SessionManager sessionManage;
    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.101:1433/Tanks";
    private static final String DB_USER = "test"; // Leave it empty for Windows Authentication
    private static final String DB_PASSWORD = "test"; // Leave it empty for Windows Authentication



    class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView mCard;
        public final TextView mTitle, mTier;
        public final Button favBtn;
        public final ImageView mImage, mType;

        public ViewHolder(View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.card);
            mTitle = itemView.findViewById(R.id.name_text_view);
            mTier = itemView.findViewById(R.id.tier_text_view);
            mImage = itemView.findViewById(R.id.image);
            mType = itemView.findViewById(R.id.image_type);
            favBtn = itemView.findViewById(R.id.favBtn);

            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    Tank tank = mTankList.get(position);
                    if(tank.getLiked() == true){
                        tank.setLiked(false);
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_unfavorite_24);
                        new UnlikedTask().execute(sessionManage.getUsername(), String.valueOf(tank.getId()));
                    } else {
                        tank.setLiked(true);
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                        new LikedTask().execute(sessionManage.getUsername(), String.valueOf(tank.getId()));
                    }
                }
            });
        }
    }

    public ListAdapter(Context context, List<Tank> tankList, OnItemClick onItemClick, SessionManager sessionManager) {
        mInflater = LayoutInflater.from(context);
        mTankList = tankList;
        mOnItemClick = onItemClick;
        sessionManage = sessionManager;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_item_tank, parent, false);
        return new ViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        Tank mCurrent = mTankList.get(position);
        Glide.with(holder.itemView.getContext()).load(mCurrent.getImage()).into(holder.mImage);
        holder.mTitle.setText(mCurrent.getName());
        holder.mTier.setText(Integer.toString(mCurrent.getTier()));
        holder.mCard.setOnClickListener(view -> mOnItemClick.OnItemClicked(mCurrent));
        if(mCurrent.getType().equals("heavyTank")) {
            holder.mType.setImageResource(R.drawable.heavy_tank_icon);
        } else if (mCurrent.getType().equals("mediumTank")) {
            holder.mType.setImageResource(R.drawable.medium_tank_icon);
        } else if (mCurrent.getType().equals("lightTank")) {
            holder.mType.setImageResource(R.drawable.light_tank_icon);
        } else holder.mType.setImageResource(R.drawable.tank_destroyer_icon);
        if(mCurrent.getLiked() == true) {
            holder.favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
        } else {
            holder.favBtn.setBackgroundResource(R.drawable.ic_baseline_unfavorite_24);
        }
    }

    @Override
    public int getItemCount() {
        return mTankList.size();
    }

    private class LikedTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... credentials) {
            String username = credentials[0];
            String id = credentials[1];

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet resultSet = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                System.out.println("OKK");
                String query = "INSERT INTO favorite_tanks (username, tank_id) VALUES (?, ?)";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, id);

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

        }
    }

    private class UnlikedTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... credentials) {
            String username = credentials[0];
            String id = credentials[1];

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet resultSet = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                System.out.println("OKK");
                String query = "DELETE FROM favorite_tanks WHERE username = ? AND tank_id = ?;";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, id);

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

        }
    }
}
