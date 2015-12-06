package test;

import java.lang.reflect.Field;
import java.util.StringTokenizer;

import org.springframework.stereotype.Component;

@Component
public class ReflectionHelper {
	public Object getValueForField(Object sourceObject, String fieldName) {
		if (sourceObject == null) {
			return null;
		}
		try {
			Field field = sourceObject.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(sourceObject);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getValueForPath(Object sourceObject, String fieldPath) {
		StringTokenizer strTok = new StringTokenizer(fieldPath, ".");
		Object currentObject = sourceObject;

		while (strTok.hasMoreTokens()) {
			String fieldName = strTok.nextToken();
			currentObject = getValueForField(currentObject, fieldName);
		}
		return currentObject;
	}
}
