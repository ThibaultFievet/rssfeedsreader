package fievetthibault.rssfeedreader;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RssParser {
    // We don't use namespaces
    private static final String ns = null;
    public List<Item> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRss(parser);
        } finally {
            in.close();
        }
    }
    private List<Item> readRss(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Item> entries = new ArrayList<Item>();
        parser.require(XmlPullParser.START_TAG, null, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
// Starts by looking for the channel tag
            if (name.equals("channel")) {
                return readChannel(parser);
            } else {
                skip(parser);
            }
        }
        return entries;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    private List<Item> readChannel(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        List<Item> entries = new ArrayList<Item>();
        parser.require(XmlPullParser.START_TAG, null, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }
    private Item readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String link = null;
        String description = null;
        String pubDate = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("pubDate")) {
                pubDate = readPubDate(parser);
            } else {
                skip(parser);
            }
        }
        try {
            return new Item(title, link, description, pubDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readRequiredTag(parser, "pubDate");
    }
    private String readDescription(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        return readRequiredTag(parser, "description");
    }
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readRequiredTag(parser, "link");
    }
    private String readRequiredTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readRequiredTag(parser, "title");
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    public static class Item implements Serializable {
        public final String title;
        public final String link;
        public final String description;
        public final String pubDate;

        public Item(String title, String link, String description, String pubDate) {
            this.title = title;
            this.link = link;
            this.description = description;
            String tmp;
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                Date date = inputFormat.parse(pubDate);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
                tmp = outputFormat.format(date);
            } catch(ParseException e) {
                tmp = "Bad Date";
                e.printStackTrace();
            }

            this.pubDate = tmp;
        }
        @Override
        public String toString() {
            return "{" + title + "," + link + "," + description + "," + pubDate + "}";
        }
    }
}