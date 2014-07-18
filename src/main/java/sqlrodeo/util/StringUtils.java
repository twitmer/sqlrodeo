package sqlrodeo.util;

public final class StringUtils {

	public static boolean isEmpty(String value) {
		if (value != null && value.length() > 0) {
			return false;
		}
		return true;
	}

	private StringUtils() {
	}
}
