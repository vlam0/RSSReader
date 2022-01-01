package eecs40.rssmanager;

import de.vogella.rss.model.Feed;
import de.vogella.rss.model.FeedMessage;
import eecs40.observer.RSSFeedObserver;
import eecs40.rssfeed.RSSFeed;
import eecs40.rssfeed.RSSFeedInterface;

import java.net.ConnectException;
import java.util.*;

public class RSSManager implements RSSManagerInterface {

    long interval;

    final List<RSSFeedInterface> feedEntries = new ArrayList<RSSFeedInterface>();
    final List<RSSFeedObserver> feedObservers = new ArrayList<RSSFeedObserver>();

    // Add RSSFeed
    public void addFeed (RSSFeedInterface f) {
        feedEntries.add(f);
    }
    // remove RSSFeed
    public void removeFeed (RSSFeedInterface f) {
        feedEntries.remove(f);
    }
    // getFeedList
    public List<RSSFeedInterface> getFeedList () {
        return feedEntries;
    }
    // add observer
    public void addObserver (RSSFeedObserver ob) {
        feedObservers.add(ob);
    }
    // remove observer
    public void removeObserver (RSSFeedObserver ob) {
        feedObservers.remove(ob);
    }
    // remove observer
    public List <RSSFeedObserver> getObserverList () {
        return feedObservers;
    }

    @Override
    public void notifyObservers(RSSFeedInterface fi) {
        for (RSSFeedObserver o : this.getObserverList()) {
            o.newFeedArrived(fi);
        }
    }
    @Override
    public void readAll() {
        for (RSSFeedInterface f : getFeedList()) {
            int numChanged = f.read();

            if (numChanged > 0) {
                notifyObservers(f);
            }
        }
    }
    // set fetch interval
    public void setInterval (long interval) {
        this.interval = interval;
    }
    // get fetch interval
    public long getInterval () {
        return interval;
    }

    @Override
    public void run () {
        while (true) {
            readAll();

            try {
                Thread.sleep(getInterval()); // every x seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
