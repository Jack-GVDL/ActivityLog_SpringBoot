package JackGVDL.ActivityLog.DAO;

import JackGVDL.ActivityLog.Model.LogDate;
import JackGVDL.ActivityLog.Model.LogEvent;

import java.util.List;

public interface ActivityLogDAO {

    int addEvent(int[] date, LogEvent event);

    // remove event of given date by index
    int rmEventByIndex(int[] date, int index);

    int configEventByIndex(int[] date, int index, LogEvent event);

    List<LogDate> getDateList();

    List<LogEvent> getEventByDate(int[] date);
}
