package JackGVDL.ActivityLog.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class LogDate {

    // Data
    @Size(min=3, max=3)
    private int[] date;

    private List<LogEvent> event_list = new ArrayList<>();

    // Operation
    public LogDate(@JsonProperty("Date") int[] date) {
        this.date = date;
    }

    public int[] getDate() {
        return date;
    }

    public List<LogEvent> getEventList() {
        return event_list;
    }

    public int addEvent(@JsonProperty("Event") LogEvent event) {
        if (event_list.contains(event)) return 1;
        event_list.add(event);
        return 0;
    }

    // remove event by index
    public int rmEvent_Index(@JsonProperty("Index") int index) {
        if (index < 0 || index >= event_list.size()) return 1;
        event_list.remove(index);
        return 0;
    }
}
