package JackGVDL.ActivityLog.Utility;

import java.util.ArrayList;
import java.util.List;

public class ObjectStringBuilder {

    // Data
    private List<Pair<String, ObjectToStringConversion>> converter_list;

    private String separator = ",";
    private String prefix = "[";
    private String suffix = "]";

    // Operation
    public ObjectStringBuilder() {
        converter_list = new ArrayList();
    }

    // TODO: uniqueness check
    public int addConverter(String typename, ObjectToStringConversion converter) {
        if (typename == null) return 1;
        if (converter == null) return 1;

        Pair<String, ObjectToStringConversion> pair = new Pair(typename, converter);
        converter_list.add(pair);
        return 0;
    }

    // TODO
    public int rmConverter(String typename) {
        return 1;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    // TODO
    public void buildString(ObjectElement element, StringBuilder builder) {
        // CONFIG
        int index = 0;

        // prefix
        builder.append(prefix);

        // element first, then object
        // element
        index = 0;
        for (ObjectElement item : element.getElementList()) {
            if (index != 0) builder.append(separator);
            buildString(item, builder);
            index++;
        }

        // object
        index = 0;
        for (Object object : element.getObjectList()) {
            ObjectToStringConversion converter = _findConverter_(object.getClass().getName());
            if (converter == null) continue;

            if (index != 0) builder.append(separator);
            builder.append(converter.convert(object));

            index++;
        }

        // suffix
        builder.append(suffix);
    }

    public String getString(ObjectElement element) {
        StringBuilder builder = new StringBuilder();
        buildString(element, builder);
        return builder.toString();
    }

    // Protected
    @Override
    protected Object clone() throws CloneNotSupportedException {
        ObjectStringBuilder builder = new ObjectStringBuilder();
        builder.converter_list = this.converter_list;
        return builder;
    }

    ObjectToStringConversion _findConverter_(String typename) {
        for (Pair<String, ObjectToStringConversion> converter : converter_list) {
            if (!converter.first.equals(typename)) continue;
            return converter.second;
        }
        return null;
    }
}
