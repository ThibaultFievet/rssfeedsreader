package fievetthibault.rssfeedreader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddFeed extends Activity {
    EditText itemURL;
    EditText itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        itemURL = (EditText) findViewById(R.id.item_add_url);
        itemName = (EditText) findViewById(R.id.item_add_name);
        Button submit = (Button) findViewById((R.id.item_add_submit));

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FeedsDB feedsDB = new FeedsDB(getBaseContext());

                RssFeed feed = new RssFeed(itemURL.getText().toString(), itemName.getText().toString());

                feedsDB.open();
                feedsDB.insertFeed(feed);
                feedsDB.close();

                Intent home = new Intent(AddFeed.this, Feeds.class);
                startActivity(home);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_feed, menu);

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
                Intent feeds = new Intent(AddFeed.this, Feeds.class);
                startActivity(feeds);
                return true;
            case R.id.settings_manage:
                // Comportement du bouton "Paramètres"
                Intent manageFeeds = new Intent(AddFeed.this, ManageFeeds.class);
                startActivity(manageFeeds);
                return true;
            case R.id.settings_display:
                // Comportement du bouton "Paramètres"
                Intent displayFeeds = new Intent(AddFeed.this, DisplaySettings.class);
                startActivity(displayFeeds);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
