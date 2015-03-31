package fievetthibault.rssfeedreader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class EditFeed extends Activity {
    private static final String ITEM_EXTRA = "item";
    EditText itemURL;
    EditText itemName;
    RssFeed item;

    public static Intent getStartIntent(Context context, RssFeed item) {
        Intent intent = new Intent(context, EditFeed.class);
        intent.putExtra(ITEM_EXTRA, item);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feed);

        itemURL = (EditText) findViewById(R.id.item_feed_url);
        itemName = (EditText) findViewById(R.id.item_feed_name);
        Button submit = (Button) findViewById((R.id.item_feed_submit));
        item = (RssFeed) getIntent().getSerializableExtra(ITEM_EXTRA);
        itemURL.setText(item.getURL());
        itemName.setText(item.getName());

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ArticlesDB articlesDB = new ArticlesDB(getBaseContext());
                articlesDB.open();
                articlesDB.updateSourceArticles(itemName.getText().toString(), item.getName());
                articlesDB.close();

                FeedsDB feedsDB = new FeedsDB(getBaseContext());

                RssFeed feed = new RssFeed(itemURL.getText().toString(), itemName.getText().toString());

                feedsDB.open();
                feedsDB.updateFeed(item.getId(), feed);
                feedsDB.close();

                Intent manageFeed = new Intent(EditFeed.this, ManageFeeds.class);
                startActivity(manageFeed);
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_feed, menu);
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
                Intent home = new Intent(EditFeed.this, ManageFeeds.class);
                startActivity(home);
                return true;
            case R.id.menu_add:
                Intent addFeed = new Intent(EditFeed.this, AddFeed.class);
                startActivity(addFeed);
                return true;
            case R.id.settings_manage:
                // Comportement du bouton "Paramètres"
                Intent manageFeeds = new Intent(EditFeed.this, ManageFeeds.class);
                startActivity(manageFeeds);
                return true;
            case R.id.settings_display:
                // Comportement du bouton "Paramètres"
                Intent displayFeeds = new Intent(EditFeed.this, DisplaySettings.class);
                startActivity(displayFeeds);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
