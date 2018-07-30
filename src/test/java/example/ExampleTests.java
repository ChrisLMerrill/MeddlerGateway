package example;

import com.webperformance.browserext.meddler.*;
import com.webperformance.browserext.meddler.clients.*;
import org.junit.*;
import org.openqa.selenium.*;

import java.util.concurrent.TimeoutException;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class ExampleTests
	{
	@Test
	public void detect404s()
		{
		_counter = new UrlStatusCounter(_connector, 404);
		_driver.get("http://httpbin.org/status/404");
		}

	@Test
	public void detectTransactionFailures()
		{
		_counter = new UrlStatusCounter(_connector, status -> status >= 400);
		_driver.get("http://httpbin.org/status/500");
		}

	@Test
	public void multiplePages()
		{
		_counter = new UrlStatusCounter(_connector, status -> status >= 400);
		_driver.get("http://automationpractice.com/");
		_driver.get("http://webperformance.com/");
		_driver.get("https://www.webperformance.com/load-testing-consulting-services/");
		_driver.get("https://www.webperformance.com/load-testing-tools/");
		_driver.get("https://www.webperformance.com/qa-testing-selenium-ide/index.html");
		_driver.get("https://www.webperformance.com/purchase/prices.html");
		_driver.get("https://www.webperformance.com/load-testing-tools/blog/");
		_driver.get("https://www.webperformance.com/support/");
		}

	@Before
	public void setup() throws TimeoutException, InterruptedException
		{
		try
			{
			_driver = DriverSetup.firefox();
			_connector = ExtensionConnection.establishConnection(_driver);
			}
		catch (Throwable t)
			{
			if (_driver != null)
				_driver.quit();
			throw t;
			}
		}

	@After
	public void teardown()
		{
		_driver.quit();
		Assert.assertFalse("404s were detected:\n" + _counter.report(), _counter.getTotal() > 0);
		}

	private UrlStatusCounter _counter;
	private WebDriver _driver;
	private ExtensionConnection _connector;

/*
	// Turn on debugging output before anything starts
	static
		{
	    MeddlerExtensionGateway.getMonitor().setDebugOutput(true);
		}
*/
	}
