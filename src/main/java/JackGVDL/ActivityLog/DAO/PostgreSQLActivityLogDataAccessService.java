package JackGVDL.ActivityLog.DAO;

import JackGVDL.ActivityLog.Model.LogDate;
import JackGVDL.ActivityLog.Model.LogEvent;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
		// check if date exist or not
		// if not exist
		// then create date and get the id_date again
		int id_date = _getID_Date_(date);
		if (id_date == -1) {
			_createDate_(date);
			id_date = _getID_Date_(date);
		}

		// create event
		if (_createEvent_(id_date, event) != 0) return -1;

		// ----- create tag (from tag list) -----
		// first get id_event
		int id_event = _getID_Event_(id_date, -1);
		// if (id_event == -1) return -1;

		// add tag one-by-one
		for (String tag : event.getTagList()) _createTag_(id_event, tag);

		return 0;
	}

	@Override
	public int rmEventByIndex(int[] date, int index) {
		// get id_date
		int id_date = _getID_Date_(date);
		if (id_date == -1) return -1;

		// get id_event
		int id_event = _getID_Event_(id_date, index);
		if (id_event == -1) return -1;

		// remove event
		if (_destroyEvent_(id_event) != 0) return -1;
		return 0;
	}

    @Override
    public int configEventByIndex(int[] date, int index, LogEvent event) {
		return -1;
    }

    @Override
    public List<LogDate> getDateList() {
		return new ArrayList<LogDate>();
    }

    @Override
    public List<LogEvent> getEventByDate(int[] date) {
		return new ArrayList<LogEvent>();
    }

    // private
	private int _getID_Date_(int[] date) {
		// sql
		int 	result 		= -1;
		final 	String sql 	= "SELECT id_date FROM LogDate WHERE event_date = " + _constructString_Date_(date);

		// query
		try {
			result = jdbc_template.queryForObject(sql, (resultSet, i) -> {
				return resultSet.getInt("id_date");
			});
		} catch (Exception e) {}
		return result;
	}

	// if index == -1, then get the back (if exist)
	private int _getID_Event_(int id_date, int index) {
		// sql
		int 			result			= -1;
		List<Integer>	id_event_list;
		final String	sql				= "SELECT id_event FROM LogEvent WHERE id_date=" + id_date;

		// query
		try {
			id_event_list = jdbc_template.query(sql, (resultSet, i) -> {
				return resultSet.getInt("id_event");
			});
		} catch (Exception e) {
			return -1;
		}

		// get id by the given index
		// check if id_event_list is empty or not, OR
		// index is invalid
		if (id_event_list.isEmpty()) return -1;
		if (index >= 0 && index >= id_event_list.size()) return -1;

		// if index == -1, then get the back (if exist)
		if (index == -1) result = id_event_list.get(id_event_list.size() - 1);
		else			 result = id_event_list.get(index);

		return result;
	}

	private int _createDate_(int[] date) {
		// sql
		final String sql = "INSERT INTO LogDate (event_date) VALUES (" + _constructString_Date_(date) + ")";

		// query
		try {
			jdbc_template.update(sql);
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	// assumed: id_date must be valid
	private int _createEvent_(int id_date, LogEvent event) {
		// sql
		final String sql =
				"INSERT INTO LogEvent " +
				"(id_date, event_name, time_start_hour, time_start_minute, time_end_hour, time_end_minute) " +
				"VALUES (" +
						id_date + ", " +
						"'', " +
						event.getTimeStart()[0] + ", " +
						event.getTimeStart()[1] + ", " +
						event.getTimeEnd()[0] + ", " +
						event.getTimeEnd()[1] +
				")";

		// query
		try {
			jdbc_template.update(sql);
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	private int _createTag_(int id_event, String tag_name) {
		// sql
		final String sql = "INSERT INTO Tag (id_event, tag_name, tag_type) VALUES (" + id_event + ", '" + tag_name + "' , 0)";

		// query
		try {
			jdbc_template.update(sql);
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	private int _destroyEvent_(int id_event) {
		// sql
		final String sql = "DELETE FROM LogEvent WHERE id_event=" + id_event;

		// query
		try {
			jdbc_template.update(sql);
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	private String _constructString_Date_(int[] date) {
		return "\'" + date[0] + "-" + date[1] + "-" + date[2] + "\'";
	}
}
