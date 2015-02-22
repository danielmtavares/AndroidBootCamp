/**
 * Created by dtavares on 2/16/15.
 */
public class ImageLinkCount {
    int id;
    String url;
    int timesSeen;

    public ImageLinkCount(String URL, int timesSeen) {
        setUrl(URL);
        setTimesSeen(timesSeen);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimesSeen() {
        return timesSeen;
    }

    public void setTimesSeen(int timesSeen) {
        this.timesSeen = timesSeen;
    }
}
