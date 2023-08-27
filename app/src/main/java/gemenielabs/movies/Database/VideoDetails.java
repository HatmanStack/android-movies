package gemenielabs.movies.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "video_details")
public class VideoDetails {

    @PrimaryKey(autoGenerate = true)
    public int identity;

    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "image_url")
    public String imageURL;

    @ColumnInfo(name = "iso_639_1")
    public String iso_639_1;

    @ColumnInfo(name = "iso_3166_1")
    public String iso_3166_1;

    @ColumnInfo(name = "key")
    public String key;

    @ColumnInfo(name = "site")
    public String site;

    @ColumnInfo(name = "size")
    public String size;

    @ColumnInfo(name = "type")
    public String type;

    public VideoDetails(int id, String iso_639_1, String iso_3166_1, String key, String site,
                        String size, String type) {
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.iso_3166_1 = iso_3166_1;
        this.key = key;
        this.site = site;
        this.size = size;
        this.type = type;
    }
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key){ this.key = key;}

    public void setSite(String site) {
        this.site = site;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
