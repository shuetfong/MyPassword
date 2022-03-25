package my.edu.utar.mypassword;

public class Password extends Data {

    private Integer passwordID;
    private String title;
    private String username;
    private String password;
    private Integer categoryID;
    private String url;
    private String note;

    public Password() {}

    public Password(String title, String username, String password, Integer categoryID, String url, String note) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.categoryID = categoryID;
        this.url = url;
        this.note = note;
    }

    public Password(Integer passwordID, String title, String username, String password, Integer categoryID, String url, String note) {
        this.passwordID = passwordID;
        this.title = title;
        this.username = username;
        this.password = password;
        this.categoryID = categoryID;
        this.url = url;
        this.note = note;
    }

    public void setPasswordID(Integer passwordID) {
        this.passwordID = passwordID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getPasswordID() {
        return passwordID;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public String getUrl() {
        return url;
    }

    public String getNote() {
        return note;
    }
}
