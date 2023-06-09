package gemenielabs.movies;

import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.MenuItem;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupActionBar();
    }

    // Set up the action bar with the home button
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle home button clicks in the action bar
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
