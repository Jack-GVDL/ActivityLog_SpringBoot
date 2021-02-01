package JackGVDL.ActivityLog.Utility;

import java.util.ArrayList;
import java.util.List;

public class ObjectElement {
    // Data
    // normally, the builder will first go deep (element first)
    private final List<ObjectElement>   element_list  = new ArrayList<>();
    private final List<Object>          object_list   = new ArrayList<>();

    // Operation
    public int addElement(ObjectElement element) {
        element_list.add(element);
        return 0;
    }

    public int addObject(Object object) {
        object_list.add(object);
        return 0;
    }

    // TODO
    // remove

    public List<ObjectElement> getElementList() {
        return element_list;
    }

    public List<Object> getObjectList() {
        return object_list;
    }
}
