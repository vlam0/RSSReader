package eecs40.rssmanager ;

import eecs40.observer.RSSFeedObserver ;
import eecs40.rssfeed.RSSFeedInterface ;

import java.util.List ;

public interface RSSManagerInterface extends Runnable {
    // Add RSSFeed
    public void addFeed (RSSFeedInterface f);
    // remove RSSFeed
    public void removeFeed (RSSFeedInterface f);
    // getFeedList
    public List <RSSFeedInterface> getFeedList ();
    // add observer
    public void addObserver (RSSFeedObserver ob);
    // remove observer
    public void removeObserver (RSSFeedObserver ob);
    // remove observer
    public List <RSSFeedObserver> getObserverList ();
    // notify observer
    public void notifyObservers (RSSFeedInterface fi);
    // iterate all feeds. If there's any update, it invoke notifyObservers to notify
    public void readAll ();
    // set fetch interval
    public void setInterval (long interval);
    // get fetch interval
    public long getInterval ();
}