
package id.alikhsan778_udacity.popularmovie;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class TrailerResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailers> videoResults = new ArrayList<Trailers>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailers> getVideoResults() {
        return videoResults;
    }

    public void setVideoResults(List<Trailers> videoResults) {
        this.videoResults = videoResults;
    }

}
