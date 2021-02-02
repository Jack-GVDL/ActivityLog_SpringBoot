package JackGVDL.ActivityLog.Service;

import JackGVDL.ActivityLog.DAO.ActivityLogDAO;
import JackGVDL.ActivityLog.Model.LogDate;
import JackGVDL.ActivityLog.Model.LogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    // Data
    private final ActivityLogDAO activity_log_DAO;

    // Operation
    @Autowired
    public ActivityLogService(@Qualifier("Postgres") ActivityLogDAO activity_log_DAO) {
        this.activity_log_DAO = activity_log_DAO;
    }

    public int addEvent(int[] date, LogEvent event) {
        return activity_log_DAO.addEvent(date, event);
    }

    public int rmEventByIndex(int[] date, int index) {
        return activity_log_DAO.rmEventByIndex(date, index);
    }

    public int configEventByIndex(int[] date, int index, LogEvent event) {
        return activity_log_DAO.configEventByIndex(date, index, event);
    }

    public List<LogDate> getDateList() {
        return activity_log_DAO.getDateList();
    }

    public List<LogEvent> getEventByDate(int[] date) {
        return activity_log_DAO.getEventByDate(date);
    }
}
