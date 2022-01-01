package eecs40.rssfeed;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.vogella.rss.model.Feed;
import de.vogella.rss.model.FeedMessage;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class RSSFeed implements RSSFeedInterface {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";

    final URL url;
    final String title;

    final List<FeedMessage> message = new ArrayList<FeedMessage>();
    ArrayList<FeedMessage> savedMessages = new ArrayList<>();
    ArrayList<String> savedGUID = new ArrayList<>();
    int loc = 0, initial = 0;

    public RSSFeed(String feedUrl, String title) {
        try {
            this.url = new URL(feedUrl);
            this.title = title;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    // return Feed
    public Feed getFeed () {
        Feed feed = null;
        try {
            boolean isFeedHeader = true;
            // Set header values intial to the empty string
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String author = "";
            String pubdate = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = readURL();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    switch (localPart) {
                        case ITEM:
                            if (isFeedHeader) {
                                isFeedHeader = false;
                                feed = new Feed(title, link, description, language,
                                        copyright, pubdate);
                            }
                            event = eventReader.nextEvent();
                            break;
                        case TITLE:
                            title = getCharacterData(event, eventReader);
                            break;
                        case DESCRIPTION:
                            description = getCharacterData(event, eventReader);
                            break;
                        case LINK:
                            link = getCharacterData(event, eventReader);
                            break;
                        case GUID:
                            guid = getCharacterData(event, eventReader);
                            break;
                        case LANGUAGE:
                            language = getCharacterData(event, eventReader);
                            break;
                        case AUTHOR:
                            author = getCharacterData(event, eventReader);
                            break;
                        case PUB_DATE:
                            pubdate = getCharacterData(event, eventReader);
                            break;
                        case COPYRIGHT:
                            copyright = getCharacterData(event, eventReader);
                            break;
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
                        FeedMessage messages = new FeedMessage();
                        messages.setAuthor(author);
                        messages.setDescription(description);
                        messages.setGuid(guid);
                        messages.setLink(link);
                        messages.setTitle(title);
                        feed.getMessages().add(messages);
                        event = eventReader.nextEvent();
                        continue;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return feed;
    }

    private InputStream readURL() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }

    // read feed and return # of updated items
    public int read () {
        int count = 0;
        loc = savedGUID.size();
        Feed feed = getFeed();
        if (initial == 2)
            initial = 0;

        for (FeedMessage message : feed.getMessages()) {
            if (savedGUID.contains(message.getGuid())) {}
            else {
                savedMessages.add(message);
                savedGUID.add(message.getGuid());
            }
            if (initial == 1)
                count++;
        }
        if (initial == 1)
            initial = 2;

        for (int i = loc; i < savedGUID.size(); i++) {
            if (initial == 0)
                count++;
        }

        return count;
    }
    // return number of items
    public int size () {
        return read();
    }
    // return number of items updated after last read() invoked
    public int getLastNumUpdate () {
        int update = 0;
        for (int i = loc; i < savedGUID.size(); i++) {
            update++;
        }
        if (initial == 1)
            return savedGUID.size();
        else
            return update;
    }

    @Override
    public Iterator<FeedMessage> iterator() {
        return (Iterator<FeedMessage>) message;
    }
}
