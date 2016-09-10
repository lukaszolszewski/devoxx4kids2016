package pl.devoxx4kids.devoxx4kids.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Hero extends RealmObject {

    @PrimaryKey
    private String  url;
    private Integer  color;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
