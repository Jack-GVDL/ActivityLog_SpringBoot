package JackGVDL.ActivityLog.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;

public class LogEvent {

    // Data
    @Size(min=2, max=2)
    private int[] time_start;

    @Size(min=2, max=2)
    private int[] time_end;

    @Size(min=1)
    private String[] tag_list;

    // Operation
    public LogEvent(@JsonProperty("TimeStart") int[] time_start,
                    @JsonProperty("TimeEnd") int[] time_end,
                    @JsonProperty("TagList") String[] tag_list) {
        this.time_start = time_start;
        this.time_end = time_end;
        this.tag_list = tag_list;
    }

    public int[] getTimeStart() {
        return time_start;
    }

    public int[] getTimeEnd() {
        return time_end;
    }

    public String[] getTagList() {
        return tag_list;
    }

    public void set(LogEvent event) {
        if (event.time_start != null)   this.time_start = event.time_start;
        if (event.time_end != null)     this.time_end   = event.time_end;
        if (event.tag_list != null)     this.tag_list   = event.tag_list;
    }

    @Override
    public String toString() {
        String content_1 = "time_start: " + time_start[0] + ":" + time_start[1];
        String content_2 = "time_end: " + time_end[0] + ":" + time_end[1];

        String content_3 = "";
        for (int i = 0; i < tag_list.length; ++i) {
            if (i != 0) {
                content_3 += ", ";
            }
            content_3 += tag_list[i];
        }

        return content_1 + "; " + content_2 + "; " + content_3;
    }
}
