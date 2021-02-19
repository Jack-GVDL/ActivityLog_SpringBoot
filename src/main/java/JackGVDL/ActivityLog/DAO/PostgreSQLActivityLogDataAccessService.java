package JackGVDL.ActivityLog.DAO;

import JackGVDL.ActivityLog.Model.LogDate;
import JackGVDL.ActivityLog.Model.LogEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
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
		int id_date = _getDate_ID_(date);
		if (id_date == -1) {
			_createDate_(date);
			id_date = _getDate_ID_(date);
		}

		// create event
		if (_createEvent_(id_date, event) != 0) return -1;

		// ----- create tag (from tag list) -----
		// first get id_event
		int id_event = _getEvent_ID_(id_date, -1);
		// if (id_event == -1) return -1;

		// add tag one-by-one
		for (String tag : event.getTagList()) _createTag_(id_event, tag);

		return 0;
	}

	@Override
	public int rmEventByIndex(int[] date, int index) {
		// get id_date
		int id_date = _getDate_ID_(date);
		if (id_date == -1) return -1;

		// get id_event
		int id_event = _getEvent_ID_(id_date, index);
		if (id_event == -1) return -1;

		// remove event
		if (_destroyEvent_(id_event) != 0) return -1;
		return 0;
	}

    @Override
    public int configEventByIndex(int[] date, int index, LogEvent event) {
		// ----- update event -----
		// get id_date
		int id_date = _getDate_ID_(date);
		if (id_date == -1) return -1;

		// get id_event
		int id_event = _getEvent_ID_(id_date, index);
		if (id_event == -1) return -1;

		// update event
		// where tag is not updated
		if (_updateEvent_(id_event, event.getTimeStart(), event.getTimeEnd()) != 0) return -1;

		// ----- update tag -----
		// clear the old tag
		_destroyTag_List_(id_event);

		// add new tag
		for (String tag_name : event.getTagList()) _createTag_(id_event, tag_name);

		return -1;
    }

    @Override
    public List<LogDate> getDateList() {
		return _getDate_List_();
    }

    @Override
    public List<LogEvent> getEventByDate(int[] date) {
		// get id_date
		int id_date = _getDate_ID_(date);
		if (id_date == -1) return new ArrayList<>();

		// get list of id_event
		List<Integer> id_event_list = _getEvent_ID_List_(id_date);

		// foreach event
		// get its id_tag, time_start, time_end
		List<LogEvent> result = new ArrayList<>();

		for (int id_event : id_event_list) {
			LogEvent	event		= _getEvent_(id_event);  // get LogEvent without present of tag_list
			List<String> tag_list	= _getTag_TagName_List_(id_event);

			String[] temp = new String[tag_list.size()];
			event.setTagList(tag_list.toArray(temp));

			result.add(event);
		}

		return result;
    }

    // private
	// get
	/*
		Type			| Description				| Function Naming
		single id		| 1 id is get				| _get{TableName}_ID_
		multiple id		| n ids are get				| _get{TableName}_ID_List_
		single data		| 1 row at max is get		| _get{TableName}_{ItemName: Optional}_
		multiple data	| n rows at max are get		| _get{TableName}_List_{ItemName: Optional}_
	*/
	private int _getDate_ID_(int[] date) {
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
	private int _getEvent_ID_(int id_date, int index) {
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

	private List<Integer> _getEvent_ID_List_(int id_date) {
		// sql
		final String sql = "SELECT id_event FROM LogEvent WHERE id_date=" + id_date;

		// query
		List<Integer> result = new ArrayList<>();
		try {
			jdbc_template.query(sql, (resultSet, i) -> {
				result.add(resultSet.getInt("id_event"));
				return 0;
			});
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	private List<LogDate> _getDate_List_() {
		// sql
		final String sql = "SELECT event_date FROM LogDate";

		// query
		List<LogDate> result = new ArrayList<LogDate>();

		try {
			jdbc_template.query(sql, (resultSet, i) -> {

				Date d = resultSet.getDate("event_date");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(d);

				int[] date = {
						calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH)};

				LogDate temp = new LogDate(date);
				result.add(temp);
				return 0;
			});
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	private List<String> _getTag_TagName_List_(int id_event) {
		// sql
		final String sql = "SELECT tag_name FROM Tag WHERE id_event=" + id_event;

		// query
		List<String> result = new ArrayList<>();

		try {
			jdbc_template.query(sql, (resultSet, i) -> {

				// assumed: tag_name is not empty string
				String tag_name = resultSet.getString("tag_name");
				tag_name = tag_name.trim();
				result.add(tag_name);
				return 0;
			});
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	private LogEvent _getEvent_(int id_event) {
		// sql
		final String sql = "SELECT * FROM LogEvent WHERE id_event=" + id_event;

		// query
		List<LogEvent> event_list;

		try {
			event_list = jdbc_template.query(sql, (resultSet, i) -> {
				final int[] time_start = {
						resultSet.getInt("time_start_hour"),
						resultSet.getInt("time_start_minute")};

				final int[] time_end = {
						resultSet.getInt("time_end_hour"),
						resultSet.getInt("time_end_minute")};

				return new LogEvent(time_start, time_end, null);
			});
		} catch (Exception e) {
			return null;
		}

		if (event_list.isEmpty()) return null;
		return event_list.get(0);
	}

	// create
	/*
		Type		| Description				| Function Naming
		single		| 1 row at max is added		| _create{TableName}_
	*/
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

	// destroy
	/*
		Type		| Description 				| Function Naming
		single		| 1 row at max is removed	| _destroy{TableName}_
		multiple	| n rows at max are removed	| _destroy{TableName}_List_
	*/
	//
	// - date should not be destoryed
	// - tag will be destroyed by database if corresponding event is destroyed
	//
	// therefore
	// the only thing that can be destoryed (by client side, in db perspective) is event
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

	private int _destroyTag_List_(int id_event) {
		// sql
		final String sql = "DELETE FROM Tag WHERE id_event=" + id_event;

		// query
		try {
			jdbc_template.update(sql);
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	// update
	/*
		Type		| Description				| Function Naming
		single		| 1 row at max is handled	| _update{TableName}_
	*/
	private int _updateEvent_(int id_event, int[] time_start, int[] time_end) {
		// sql
		final String sql = "" +
				"UPDATE LogEvent " +
				"SET " +
				"time_start_hour="  + time_start[0] + ", " +
				"time_start_minute=" + time_start[1] + ", " +
				"time_end_hour=" + time_end[0] +  ", " +
				"time_end_minute=" + time_end[1] + " " +
				"WHERE id_event=" + id_event;

		// query
		try {
			jdbc_template.update(sql);
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	// get string
	private String _constructString_Date_(int[] date) {
		return "\'" + date[0] + "-" + date[1] + "-" + date[2] + "\'";
	}
}
