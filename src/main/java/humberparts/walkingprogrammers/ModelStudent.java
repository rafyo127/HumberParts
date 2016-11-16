package humberparts.walkingprogrammers;

import android.support.v7.widget.Toolbar;

/**
 * Created by Ash on 2016-11-13.
 */


public class ModelStudent {
    private String id;
    private String number;
    private String date;
    private String partNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
}
