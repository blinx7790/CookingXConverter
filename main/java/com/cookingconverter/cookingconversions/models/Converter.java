package com.cookingconverter.cookingconversions.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "converters")
public class Converter implements Parcelable {

//
//sql information table and column names etc...
//


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "leftSpinner")
    private int leftSpinner;

    @ColumnInfo(name = "rightSpinner")
    private int rightSpinner;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "number")
    private String number;

    @ColumnInfo(name = "answer")
    private String answer;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    //
    //constructor
    //

    public Converter(String title, int leftSpinner, int rightSpinner, String content, String number, String answer, String timestamp) {
        this.title = title;
        this.leftSpinner = leftSpinner;
        this.rightSpinner = rightSpinner;
        this.content = content;
        this.number = number;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    @Ignore
    public Converter() {

    }


    protected Converter(Parcel in) {
        id = in.readInt();
        title = in.readString();
        leftSpinner = in.readInt();
        rightSpinner = in.readInt();
        content = in.readString();
        number = in.readString();
        answer = in.readString();
        timestamp = in.readString();
    }

    public static final Creator<Converter> CREATOR = new Creator<Converter>() {
        @Override
        public Converter createFromParcel(Parcel in) {
            return new Converter(in);
        }

        @Override
        public Converter[] newArray(int size) {
            return new Converter[size];
        }
    };

    //
    //getters and setters
    //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLeftSpinner(){return leftSpinner;}

    public void setLeftSpinner(int leftSpinner){ this.leftSpinner = leftSpinner;}

    public int getRightSpinner(){return rightSpinner;}

    public void setRightSpinner(int rightSpinner){ this.rightSpinner = rightSpinner;}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNumber(){return number;}

    public void setNumber(String number){this.number = number;}

    public String getAnswer(){return answer;}

    public void setAnswer(String answer){this.answer = answer;}

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "Converter{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", leftSpinner=" + leftSpinner +
                ", rightSpinner=" + rightSpinner +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeInt(leftSpinner);
        parcel.writeInt(rightSpinner);
        parcel.writeString(content);
        parcel.writeString(number);
        parcel.writeString(answer);
        parcel.writeString(timestamp);
    }
}
