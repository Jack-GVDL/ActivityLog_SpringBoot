package JackGVDL.ActivityLog.API;

import JackGVDL.ActivityLog.Model.LogDate;
import JackGVDL.ActivityLog.Model.LogEvent;
import JackGVDL.ActivityLog.Service.ActivityLogService;
import JackGVDL.ActivityLog.Utility.ObjectElement;
import JackGVDL.ActivityLog.Utility.ObjectStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/")
@RestController
@CrossOrigin
public class ActivityLogController {

    // Data
    private final ActivityLogService activityLogService;
    private final String INTERFACE_V_1 = "";
    private final String INTERFACE_V_2 = "/v2";

    private final ObjectStringBuilder builder = new ObjectStringBuilder();

    // Operation
    @Autowired
    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;

        // int[]
        int[] type_int_array = new int[0];
        builder.addConverter(type_int_array.getClass().getName(), (Object object) -> {
             int[] array = (int[])object;
             return Arrays.toString(array);
        });

        // String[]
        String[] type_string_array = new String[0];
        builder.addConverter(type_string_array.getClass().getName(), (Object object) -> {
            String[] array = (String[])object;

            StringBuilder builder = new StringBuilder();
             builder.append("[");

            int index = 0;
            for (String item : array) {
                if (index != 0) builder.append(",");
                builder.append("\"");
                builder.append(item);
                builder.append("\"");
                index++;
            }

             builder.append("]");
            return builder.toString();
        });
    }

    // interface v1
    @GetMapping(INTERFACE_V_1 + "/GetDateList")
    public String V1_getDateList() {
        // log
        System.out.println("Request: GetDateList");

        // get list of LogDate
        List<LogDate> date_list = activityLogService.getDateList();

        // data to output string
        ObjectElement element_base = new ObjectElement();
        for (var date : date_list) {
            element_base.addObject(date.getDate());
        }

        return builder.getString(element_base);
    }

    @GetMapping(INTERFACE_V_1 + "/GetEvent_Date")
    public String V1_getEventByDate(
            @RequestParam(required=true) String date) {

        // log
        System.out.println("Request: GetEvent_Date");

        // get date and then list of event
        int[] date_int = splitStringToInt(date, "_");
        List<LogEvent> event_list = activityLogService.getEventByDate(date_int);

        // convert event list to string
        var element_base = new ObjectElement();

        // foreach LogEvent
        for (LogEvent event : event_list) {
            var element = new ObjectElement();
            element_base.addElement(element);

            // time
            int[] time_int = {
                    event.getTimeStart()[0], event.getTimeStart()[1],
                    event.getTimeEnd()[0], event.getTimeEnd()[1]};
            element.addObject(time_int);

            // tag list
            element.addObject(event.getTagList());
        }

        // build string and return
        return builder.getString(element_base);
    }

    @PostMapping(INTERFACE_V_1 + "/AddEvent")
    public void V1_addEvent(
            @RequestParam(required=true) String date,
            @RequestParam(required=true) String time_start,
            @RequestParam(required=false) String time_end,
            @RequestParam(required=true) String tag) {

        // log
        System.out.println("Request: AddEvent");

        // string to target data type
        int[]       date_int       = splitStringToInt(date, "_");
        int[]       time_start_int = splitStringToInt(time_start, "_");
        int[]       time_end_int   = null;
        String[]    tag_string     = tag.split(",");

        if (time_end != null) time_end_int = splitStringToInt(time_end, "_");
        else                  time_end_int = time_start_int;

        // create LogEvent and add to database
        var log_event = new LogEvent(time_start_int, time_end_int, tag_string);
        activityLogService.addEvent(date_int, log_event);
    }

    @PostMapping(INTERFACE_V_1 + "/RmEvent")
    public void V1_rmEvent(
            @RequestParam(required=true) String date,
            @RequestParam(required=true) String index) {

        // log
        System.out.println("Request: RmEvent");

        // string to target data type
        int[]   date_int    = splitStringToInt(date, "_");
        int     index_int   = Integer.parseInt(index);

        // remove event
        activityLogService.rmEventByIndex(date_int, index_int);
    }

    @PostMapping(INTERFACE_V_1 + "/ConfigEvent")
    public void V1_configEvent(
            @RequestParam(required=true) String date,
            @RequestParam(required=true) String index,
            @RequestParam(required=false) String time_start,
            @RequestParam(required=false) String time_end,
            @RequestParam(required=false) String tag) {

        // log
        System.out.println("Request: ConfigEvent");

        // log
        System.out.println("Request: AddEvent");

        // string to target data type
        int[]       date_int       = splitStringToInt(date, "_");
        int         index_int      = Integer.parseInt(index);
        int[]       time_start_int = null;
        int[]       time_end_int   = null;
        String[]    tag_string     = null;

        if (time_start != null) time_start_int = splitStringToInt(time_start, "_");
        if (time_end != null)   time_end_int = splitStringToInt(time_end, "_");
        if (tag != null)        tag_string = tag.split(",");

        // config event
        activityLogService.configEventByIndex(
                date_int,
                index_int,
                new LogEvent(time_start_int, time_end_int, tag_string));
    }

    // interface v2
    @PostMapping(INTERFACE_V_2 + "/AddEvent")
    public void V2_addEvent(@RequestParam(required=true) String date, @RequestBody LogEvent event) {
        String[] string_list = date.split("_");
        if (string_list.length != 3) throw new RuntimeException();

        int[] date_value = {
                Integer.parseInt(string_list[0]),
                Integer.parseInt(string_list[1]),
                Integer.parseInt(string_list[2])};

        this.activityLogService.addEvent(date_value, event);
    }

    // Exception
    @ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Runtime Error")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> Bad_Request() {
        return new ResponseEntity<Object>(
                "RUNTIME ERROR",
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    // Protected
    int[] splitStringToInt(String s, String separator, int length) {
        String[] string_list = s.split(separator);
        int[] int_list = new int[string_list.length];

        for (int i = 0; i < string_list.length; ++i) {
            int_list[i] = Integer.parseInt(string_list[i]);
        }

        return int_list;
    }

    int[] splitStringToInt(String s, String separator) {
        return splitStringToInt(s, separator, -1);
    }
}
