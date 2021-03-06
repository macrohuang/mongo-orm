package github.macrohuang.orm.mongo.core;

import java.util.Collection;

import org.springframework.util.StringUtils;

public final class Assert {
	public static void assertNotNull(Object object) {
		if (object == null)
			throw new RuntimeException("not null assert fail!");
	}

	public static void assertNotEmpty(Collection<?> object) {
		if (object == null)
			throw new RuntimeException("not null assert fail!");
		if (object.isEmpty())
			throw new RuntimeException("not empty collection assert fail!");
	}

	public static void assertNotBlank(String string) {
		if (!StringUtils.hasText(string)) {
			throw new RuntimeException("Empty string are not allowed");
		}
	}
}
