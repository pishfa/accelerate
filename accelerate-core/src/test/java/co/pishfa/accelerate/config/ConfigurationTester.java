/**
 * 
 */
package co.pishfa.accelerate.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Taha Ghasemi
 * 
 */
public class ConfigurationTester {

	@Test
	public void testReadXmlFile() throws Exception {
        HashTableConfig config = new HashTableConfig();
		XmlConfigReader reader = new XmlConfigReader(config);
		reader.load(getClass().getResourceAsStream("test_config_1.xml"));

		Assert.assertEquals(config.getString("services.userService.location"), "localhost");
		Assert.assertTrue(config.getInteger("services.userService.interval") == 10);
	}
}
