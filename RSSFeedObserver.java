package eecs40.observer;

import eecs40.rssfeed.RSSFeedInterface;

public interface RSSFeedObserver {
    public void newFeedArrived(RSSFeedInterface f); //Notify
}
