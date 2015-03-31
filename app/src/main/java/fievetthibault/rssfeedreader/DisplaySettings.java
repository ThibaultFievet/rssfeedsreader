package fievetthibault.rssfeedreader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Build;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class DisplaySettings extends Activity {

    Settings settings;
    SettingsDB settingsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);

        settingsDB = new SettingsDB(this);
        settingsDB.open();
        settings = settingsDB.getSettings();
        settingsDB.close();

        CheckBox readArticles = (CheckBox) findViewById(R.id.id_read_articles);
        readArticles.setChecked(settings.isReadArticles());

        readArticles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                settings.setReadArticles(isChecked);
                settingsDB.open();
                settingsDB.updateSettings(settings);
                settingsDB.close();
            }
        });

        RadioGroup sortGroup = (RadioGroup) findViewById(R.id.display_group_sort);
        if(settings.getSort().equals("OLDEST_TO_NEWEST")) sortGroup.check(R.id.display_sort_radio1);
        else sortGroup.check(R.id.display_sort_radio2);

        sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println(checkedId);
                RadioButton rb = (RadioButton) findViewById(checkedId);

                String sort = rb.getText().toString();
                if(sort.equals("From oldest to newest"))
                    settings.setSort("OLDEST_TO_NEWEST");
                else settings.setSort("NEWEST_TO_OLDEST");

                settingsDB.open();
                settingsDB.updateSettings(settings);
                settingsDB.close();
            }
        });

        CheckBox sortSource = (CheckBox) findViewById(R.id.display_sort_source);
        sortSource.setChecked(settings.isSortSource());

        sortSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                settings.setSortSource(isChecked);
                settingsDB.open();
                settingsDB.updateSettings(settings);
                settingsDB.close();
            }
        });

        RadioGroup deleteGroup = (RadioGroup) findViewById(R.id.display_delete_group);
        if(settings.getDeleteDate().equals("1_MONTH")) deleteGroup.check(R.id.display_delete_radio1);
        else if(settings.getDeleteDate().equals("6_MONTHS")) deleteGroup.check(R.id.display_delete_radio2);
        else deleteGroup.check(R.id.display_delete_radio3);

        deleteGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println(checkedId);
                RadioButton rb = (RadioButton) findViewById(checkedId);

                String delete = rb.getText().toString();
                if(delete.equals("1 month")) settings.setDeleteDate("1_MONTH");
                else if(delete.equals("6 months")) settings.setDeleteDate("6_MONTHS");
                else settings.setDeleteDate("1_YEAR");

                settingsDB.open();
                settingsDB.updateSettings(settings);
                settingsDB.close();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_settings, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent home = new Intent(DisplaySettings.this, Feeds.class);
                startActivity(home);
                return true;
            case R.id.settings_manage:
                // Comportement du bouton "Paramètres"
                Intent manageFeeds = new Intent(DisplaySettings.this, ManageFeeds.class);
                startActivity(manageFeeds);
                return true;
            case R.id.menu_add:
                Intent addFeed = new Intent(DisplaySettings.this, AddFeed.class);
                startActivity(addFeed);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
