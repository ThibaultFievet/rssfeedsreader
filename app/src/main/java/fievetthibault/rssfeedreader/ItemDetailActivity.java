package fievetthibault.rssfeedreader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ItemDetailActivity extends Activity {
    private static final String ITEM_EXTRA = "item";
    private TextView itemDescription;
    private SpannableStringBuilder htmlSpannable;

    public static Intent getStartIntent(Context context, RssItem item) {
        Intent intent = new Intent(context, ItemDetailActivity.class);
        intent.putExtra(ITEM_EXTRA, item);
        return intent;
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
                Intent feeds = new Intent(ItemDetailActivity.this, Feeds.class);
                startActivity(feeds);
                return true;
            case R.id.settings_manage:
                Intent manageFeeds = new Intent(ItemDetailActivity.this, ManageFeeds.class);
                startActivity(manageFeeds);
                return true;
            case R.id.settings_display:
                Intent displayFeeds = new Intent(ItemDetailActivity.this, DisplaySettings.class);
                startActivity(displayFeeds);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        TextView itemDate = (TextView) findViewById(R.id.item_date);
        TextView itemTitle = (TextView) findViewById(R.id.item_title);
        itemDescription = (TextView) findViewById(R.id.item_description);
        RssItem item =
                (RssItem) getIntent().getSerializableExtra(ITEM_EXTRA);
        if (item != null) {
            itemTitle.setText(Html.fromHtml("<h1><a href='" + item.getLink() + "'>"+item.getTitle()+"</a></h1>"));
            itemTitle.setMovementMethod(LinkMovementMethod.getInstance());

            itemDate.setText(item.getSource() + " - " + item.getDate());

            Spanned spanned = Html.fromHtml(item.getDescription());

            if (spanned instanceof SpannableStringBuilder) {
                htmlSpannable = (SpannableStringBuilder) spanned;
            } else {
                new SpannableStringBuilder(spanned);
            }

            itemDescription.setText(htmlSpannable);
            itemDescription.setMovementMethod(LinkMovementMethod.getInstance());

            new ImageLoadTask().execute(); // Get the images asynchronously.
        }
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, List<ImageSpan>> {

        DisplayMetrics metrics = new DisplayMetrics();
        List<File> files = new ArrayList<File>();

        @Override
        protected void onPreExecute() {
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }

        @Override
        protected List<ImageSpan> doInBackground(Void... params) {
            List<ImageSpan> imgs = new ArrayList<ImageSpan>();
            for (ImageSpan img : htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class)) {
                File image = null;
                if (getImageFile(img).isFile()) {
                    image = getImageFile(img);
                }
                imgs.add(img);
                files.add(image);
            }

            return imgs;
        }

        @Override
        protected void onPostExecute(List<ImageSpan> imgs) {
            // Create the images.
            for (int i = 0; i < imgs.size(); ++i) {

                Drawable d = new BitmapDrawable(getResources(),
                        files.get(i).getAbsolutePath());

                int width, height;
                int originalWidthScaled = (int) (d.getIntrinsicWidth() * metrics.density);
                int originalHeightScaled = (int) (d.getIntrinsicHeight() * metrics.density);
                if (originalWidthScaled > metrics.widthPixels) {
                    height = d.getIntrinsicHeight() * metrics.widthPixels
                            / d.getIntrinsicWidth();
                    width = metrics.widthPixels;
                } else {
                    height = originalHeightScaled;
                    width = originalWidthScaled;
                }

                d.setBounds(0, 0, width, height);

                ImageSpan newImg = new ImageSpan(d, imgs.get(i).getSource());

                int start = htmlSpannable.getSpanStart(imgs.get(i));
                int end = htmlSpannable.getSpanEnd(imgs.get(i));

                htmlSpannable.removeSpan(imgs.get(i));

                htmlSpannable.setSpan(newImg, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                itemDescription.setText(htmlSpannable);
            }
        }

        private File getImageFile(ImageSpan img) {
            // Download the images and put them in the cache.
            try {
                URL url = new URL(img.getSource());
                String fileName = img.getSource().substring(img.getSource().lastIndexOf('/') + 1);
                File file = new File(getFilesDir() + "/" + fileName);

                URLConnection ucon = url.openConnection();

                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(2048);
                int current;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();

                return file;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}