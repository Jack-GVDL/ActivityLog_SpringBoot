package JackGVDL.ActivityLog.DAO;

import JackGVDL.ActivityLog.Model.LogDate;
import JackGVDL.ActivityLog.Model.LogEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("Postgres")
public class PostgreSQLActivityLogDataAccessService implements ActivityLogDAO {

    // Data
    private final JdbcTemplate jdbc_template;

    // Operation
    @Autowired
    public PostgreSQLActivityLogDataAccessService(JdbcTemplate jdbc_template) {
        this.jdbc_template = jdbc_template;
    }

    @Override
    public int addEvent(int[] date, LogEvent event) {
        final String sql = "";

        return jdbc_template.query(sql, (resultSet, i) -> {
        });
    }

    @Override
    public int rmEventByIndex(int[] date, int index) {
        final String sql = "";

        return jdbc_template.query(sql, (resultSet, i) -> {
        });
    }

    @Override
    public int configEventByIndex(int[] date, int index, LogEvent event) {
        final String sql = "";

        return jdbc_template.query(sql, (resultSet, i) -> {
        });
    }

    @Override
    public List<LogDate> getDateList() {
        final String sql = "SELECT event_date FROM LogDate";

        return jdbc_template.query(sql, (resultSet, i) -> {
        });
    }

    @Override
    public List<LogEvent> getEventByDate(int[] date) {
        final String sql = "";

        return jdbc_template.query(sql, (resultSet, i) -> {
        });
    }
}
