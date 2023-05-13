package gemenielabs.movies.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "review_details")
public class ReviewDetails {

    @PrimaryKey(autoGenerate = true)
    public int identity;

    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "content")
    public String content;

    public ReviewDetails(int id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public void setReviewId(int id){ this.id = id;}

    public String getAuthor() {return author;}

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { this.content = content;}
    }

