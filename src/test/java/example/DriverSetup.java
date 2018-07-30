package example;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;

import java.io.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DriverSetup
	{
	private final static String FIREFOX_DRIVER_PATH = "D:/Work/MuseProject/muse-examples/webdrivers/geckodriver/0.20.1/geckodriver_win64.exe";
//	private final static String EXTENSION_PATH = "D:/WPI/web_performance_http_monitor-1.0-an+fx.xpi";
	private final static String EXTENSION_PATH = "D:/WPI/WebPerformanceHttpMonitorExtension";
	//private final static String FIREFOX_DEVELOPER_BINARY_PATH = "C:\\Program Files\\Firefox Developer Edition\\firefox.exe";

	public static WebDriver firefox()
		{
		// point to the Firefox WebDriver
		System.setProperty("webdriver.gecko.driver", FIREFOX_DRIVER_PATH);

		FirefoxProfile profile = new FirefoxProfile();
		profile.addExtension(new File(EXTENSION_PATH));

		final FirefoxOptions options = new FirefoxOptions();

		// When developing the extension, it will be exploded and unsigned - Firefox will refuse to run it this way.
		// But Developer Edition CAN run it (after toggling xpinstall.signatures.required in about:config).
		//options.setBinary(FIREFOX_DEVELOPER_BINARY_PATH);

		options.setProfile(profile);
		final FirefoxDriver driver = new FirefoxDriver(options);
		return driver;
		}
	}
