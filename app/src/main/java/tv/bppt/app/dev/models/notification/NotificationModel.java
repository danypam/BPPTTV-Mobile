package tv.bppt.app.dev.models.notification;

public class NotificationModel {

    private int id;
    private String title;
    private String message;
    private boolean isUnread;
    private String url;

    public NotificationModel(int id, String title, String message, boolean isUnread, String url) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.isUnread = isUnread;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }


    public String getUrl() {
        return url;
    }

    public boolean isUnread() {
        return isUnread;
    }
}
