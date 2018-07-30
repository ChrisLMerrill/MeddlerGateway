package com.webperformance.browserext.meddler;

import com.webperformance.browserext.meddler.message.*;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.openqa.selenium.*;

import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@WebSocket
public class ExtensionConnection implements ExtensionClient
	{
	/**
	 * This convenience method creates a connection, registers it with the gateway, bootstraps the extension with connection
	 * details via WebDriver and then waits for the connection to complete.
	 */
	public static ExtensionConnection establishConnection(WebDriver driver) throws TimeoutException, InterruptedException
		{
		MeddlerExtensionGateway gateway = MeddlerExtensionGateway.getMonitor();
		ExtensionConnection connection = new ExtensionConnection();
		gateway.registerConnector(connection);

		// We need to tell the extension how to connect to the gateway. Sending the browser to a page allows us to
		// set a cookie. The extension can listen for cookie changes, so we can use this to send the connection info.
		// This seems like a hack...but I have not found any other way to pass dynamic parameters into the extension
		// from outside the browser.
		driver.get(MeddlerExtensionGateway.getMonitor().getReadyUrl());  // can't set a cookie until there is a document
		driver.manage().addCookie(new Cookie("meddler-bootstrap", gateway.getBootstrapParameterString(connection.getId())));

		connection.waitForConnect(3000);
		return connection;
		}

	public synchronized void waitForConnect(int timeout) throws InterruptedException, TimeoutException
		{
		long started = System.currentTimeMillis();
		while (_session == null)
			{
			this.wait(timeout);
			if (System.currentTimeMillis() - started >= timeout)
				throw new TimeoutException();
			}
		}

	public void addListener(MessageListener listener)
		{
		_listeners.add(listener);
		}

	public void removeListener(MessageListener listener)
		{
		_listeners.remove(listener);
		}

	/**
	 * Use this to shutdown the WebSocket connection from the client side.
	 *
	 * Note that if this connection was initiated through WebDriver and you shut down the browser, it will close
	 * the socket from that end, so calling shutdown is not needed (tested only with Firefox).
	 */
	public void shutdown()
		{
		if (_session != null)
			_session.close(1000, "Finished");
		}

	public int getId()
		{
		return _id;
		}

	@Override
	public synchronized void connect(Session session)
		{
		_session = session;
		this.notify();
		}

	@Override
	public void setId(int id)
		{
		_id = id;
		}

	@Override
	public void closed(int status, String reason)
		{
		_session = null;
		}

	@Override
	public void deliverMessage(Message message)
		{
		for (MessageListener listener : _listeners)
			listener.messageReceived(message);
		}

	private int _id;
	private Session _session = null;
	private Set<MessageListener> _listeners = new HashSet<>();
	}
