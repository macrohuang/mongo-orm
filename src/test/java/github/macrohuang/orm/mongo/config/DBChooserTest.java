package github.macrohuang.orm.mongo.config;

import org.junit.Assert;
import org.junit.Test;

public class DBChooserTest {

	@Test(expected = RuntimeException.class)
	public void testChooseDBNull() {
		DBChooser.getDbAndCollection(null, null);
	}

	@Test(expected = RuntimeException.class)
	public void testChooseDBEmpty() {
		DBChooser.getDbAndCollection("    ", "    ");
	}

	@Test
	public void testChooseDB() {
		DBChooser dbChooser = DBChooser.getDbAndCollection("test", "test");
		Assert.assertNotNull(dbChooser);
	}
}
