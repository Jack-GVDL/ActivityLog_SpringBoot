package JackGVDL.ActivityLog.DAO;

import JackGVDL.ActivityLog.Model.LogDate;
import JackGVDL.ActivityLog.Model.LogEvent;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("fakeDAO")
public class FakeActivityLogDataAccessService implements ActivityLogDAO {

    // Data
    private static List<LogDate> database = new ArrayList<>();


    // Operation
    @Override
    public int addEvent(int[] date, LogEvent event) {
        Optional<LogDate> target = database.stream()
                .filter(d -> Arrays.equals(d.getDate(), date))
                .findFirst();

        LogDate log_date = null;
        if (target.isPresent()) {
            log_date = target.get();
        } else {
            log_date = new LogDate(date);
            database.add(log_date);
        }

        log_date.addEvent(event);
        return 0;
    }

    @Override
    public int rmEventByIndex(int[] date, int index) {
        Optional<LogDate> target = database.stream()
                .filter(d -> Arrays.equals(d.getDate(), date))
                .findFirst();

        if (target.isEmpty()) return 1;
        LogDate log_date = target.get();

        log_date.rmEvent_Index(index);
        return 0;
    }

    @Override
    public int configEventByIndex(int[] date, int index, LogEvent event) {
        Optional<LogDate> target = database.stream()
                .filter(d -> Arrays.equals(d.getDate(), date))
                .findFirst();

        if (target.isEmpty()) return 1;
        LogDate log_date = target.get();

        LogEvent log_event = log_date.getEventList().get(index);
        if (log_event == null) return 1;

        log_event.set(event);
        return 0;
    }

    @Override
    public List<LogDate> getDateList() {
        return database;
    }

    @Override
    public List<LogEvent> getEventByDate(int[] date) {
        Optional<LogDate> target = database.stream()
                .filter(d -> Arrays.equals(d.getDate(), date))
                .findFirst();

        if (target.isEmpty()) return new ArrayList<>();
        LogDate log_date = target.get();

        return log_date.getEventList();
    }
}
