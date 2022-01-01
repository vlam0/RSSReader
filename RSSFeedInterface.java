package eecs40.rssfeed ;

import de.vogella.rss.model.Feed;
import de.vogella.rss.model.FeedMessage;

public interface RSSFeedInterface extends Iterable <FeedMessage> {
    public Feed getFeed (); // return Feed
    public int read (); // read feed and return # of updated items
    public int size (); // return number of items
    public int getLastNumUpdate (); // return number of items updated after last read() invoked
}
