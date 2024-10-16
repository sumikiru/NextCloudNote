package it.niedermann.owncloud.notes.main.menu;

import android.content.Context;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import it.niedermann.owncloud.notes.NotesApplication;
import it.niedermann.owncloud.notes.R;
import it.niedermann.owncloud.notes.preferences.DarkModeSetting;

public class DarkModeActivity extends AppCompatActivity {

    private Switch darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 建立UI布局
        setContentView(R.layout.activity_dark_mode);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            setDarkMode(this, isChecked);
        });
    }

    private void setDarkMode(Context context, boolean enabled) {
        NotesApplication myApp = (NotesApplication) context.getApplicationContext();
        if (enabled) {
            // 应用暗色主题
            myApp.setAppTheme(DarkModeSetting.fromModeID(AppCompatDelegate.MODE_NIGHT_NO));
        } else {
            // 应用亮色主题
            myApp.setAppTheme(DarkModeSetting.fromModeID(AppCompatDelegate.MODE_NIGHT_YES));
        }
        NotesApplication.updateLastInteraction();

        // 重启以相应变化
        recreate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
