package sqlrodeo.version;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sqlrodeo.implementation.SqlRodeo;

// NOTE: Cannot use 'lt' 'le', 'gt', 'ge' as method names, because JEXL assumes it means operators, not methods.
public class Version implements Comparable<Version> {

    private Logger log = LoggerFactory.getLogger(SqlRodeo.class);

    private final Integer[] parts;

    public Version(Integer... args) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("Version: args.length, args".replaceAll(", ", "=%s, ") + "=%s", args.length,
                    Arrays.asList(args)));
        }

        if(args.length > 0 && args[0] == null) {
            throw new IllegalArgumentException("Null not an acceptable integer value");
        }

        this.parts = args;
    }

    public static Version build(Integer... args) {
        return new Version(args);
    }

    public static Version build(String dottedString) {
        return new Version(dottedString);
    }

    public Version(String dottedString) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("Version: dottedString".replaceAll(", ", "=%s, ") + "=%s", dottedString));
        }
        String[] strParts = dottedString.split("\\.");
        parts = new Integer[strParts.length];
        for(int i = 0; i < strParts.length; ++i) {
            parts[i] = Integer.parseInt(strParts[i], 10);
        }
    }

    @Override
    public int compareTo(Version o) {
        log.debug("Comparing " + this + " to " + o);

        // Basically stolen from String::compareTo();
        int len1 = parts.length;
        int len2 = o.parts.length;
        int lim = Math.min(len1, len2);
        Integer v1[] = parts;
        Integer v2[] = o.parts;

        int k = 0;
        while(k < lim) {
            Integer c1 = v1[k];
            Integer c2 = v2[k];
            if(c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    public boolean lessThan(Version v) {
        return compareTo(v) < 0;
    }

    public boolean lessThan(String v) {
        return lessThan(new Version(v));
    }

    public boolean lessThanOrEqualTo(Version v) {
        return compareTo(v) <= 0;
    }

    public boolean lessThanOrEqualTo(String v) {
        return lessThanOrEqualTo(new Version(v));
    }

    public boolean greaterThan(Version v) {
        return compareTo(v) > 0;
    }

    public boolean greaterThan(String v) {
        return greaterThan(new Version(v));
    }

    public boolean greaterThanOrEqualTo(Version v) {
        return compareTo(v) >= 0;
    }

    public boolean greaterThanOrEqualTo(String v) {
        return greaterThanOrEqualTo(new Version(v));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(parts);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        Version other = (Version)obj;
        if(!Arrays.equals(parts, other.parts))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Version " + toDottedString();
    }

    public String toDottedString() {
        StringBuilder sb = new StringBuilder();
        for(Integer part : parts) {
            if(sb.length() > 0) {
                sb.append(".");
            }
            sb.append(part);
        }

        return sb.toString();
    }
}