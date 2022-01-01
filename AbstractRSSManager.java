package eecs40.rssmanager ;

import eecs40.observer.RSSFeedObserver ;
import eecs40.rssfeed.RSSFeedInterface ;

public abstract class AbstractRSSManager implements RSSManagerInterface {
    public void notifyObservers (RSSFeedInterface fi) {
        for (RSSFeedObserver o : this.getObserverList()) {
            o.newFeedArrived(fi);
        }
    }
    public void readAll () {
        for (RSSFeedInterface f : getFeedList()) {
            int numChanged = f.read();
            if (numChanged > 0) {
                notifyObservers(f);
            }
        }
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