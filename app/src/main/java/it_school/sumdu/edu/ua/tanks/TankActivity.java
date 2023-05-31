package it_school.sumdu.edu.ua.tanks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class TankActivity extends AppCompatActivity {
    public static final String TANK = "TANK";

    private ImageView mImage, mType;
    private TextView mDescription, mNation, mTier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tank);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImage = findViewById(R.id.image);
        mDescription = findViewById(R.id.description_text_view);
        mNation = findViewById(R.id.nation_text_view);
        mTier = findViewById(R.id.tier_text_view);
        mType = findViewById(R.id.image_type);

        if (getIntent().hasExtra(TANK)) {
            showTank((Tank) getIntent().getSerializableExtra(TANK));
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTank(Tank tank) {
        setTitle(tank.getName());
        mDescription.setText(tank.getDescription());
        mTier.setText(String.valueOf(tank.getTier()));
        mNation.setText(tank.getNation());
        if(tank.getType().equals("heavyTank")) {
            mType.setImageResource(R.drawable.heavy_tank_icon);
        } else if (tank.getType().equals("mediumTank")) {
            mType.setImageResource(R.drawable.medium_tank_icon);
        } else if (tank.getType().equals("lightTank")) {
            mType.setImageResource(R.drawable.light_tank_icon);
        } else mType.setImageResource(R.drawable.tank_destroyer_icon);
        Glide.with(this).load(tank.getImage()).into(mImage);
    }
}