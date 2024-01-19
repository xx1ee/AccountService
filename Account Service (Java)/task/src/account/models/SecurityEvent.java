package account.models;

import jakarta.persistence.*;

@Entity
@Table
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String date;
    String action;
    String subject;
    String object;
    String path;

    public SecurityEvent() {
    }

    public SecurityEvent(String date, String action, String subject, String object, String path) {
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
        this.date =date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
