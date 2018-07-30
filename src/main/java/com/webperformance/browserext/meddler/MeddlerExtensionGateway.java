package com.webperformance.browserext.meddler;

import com.webperformance.browserext.meddler.message.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import spark.*;

import java.io.*;
import java.util.*;

import static spark.Spark.*;

/**
 * Provides a gateway for exchanging messages with the Meddler extension
 */
public class MeddlerExtensionGateway
	{
	public static MeddlerExtensionGateway getMonitor()
		{
		if (INSTANCE == null)
			INSTANCE = new MeddlerExtensionGateway();
		return INSTANCE;
		}

	public void registerConnector(ExtensionClient connector)
		{
		int id = _connector_id_generator.nextInt();
		connector.setId(id);
		_pending_connectors.put(id, connector);
		if (_debug)
			System.out.println("registered connector " + id);
		}

	public String getBootstrapParameterString(int connection_id)
		{
		return String.format("{\"host\":\"%s\",\"port\":\"%s\",\"path\":\"%s\",\"id\":\"%d\"}", "localhost", port(), SOCKET_PATH, connection_id);
		}

	public String getReadyUrl()
		{
		return "http://localhost:" + port() + PREBOOT_PATH;
		}

	private MeddlerExtensionGateway()
		{
		port(8411);
		webSocket(SOCKET_PATH, new WebsocketHandler());
		get("/ping", (Request req, Response res) -> "pong");
		get(PREBOOT_PATH, (Request req, Response res) -> "A page is required to set the bootstrap cookie. Could be any page. Doesn't even need to be HTML.");

		if (_debug)
			System.out.println("Listening on port " + port());
		}

	public void setDebugOutput(boolean enabled)
		{
		System.out.println("MeddlerExtensionGateway: debugging enabled=" + enabled);
		_debug = enabled;
		}

	private void initiateConnector(int id, Session session)
		{
		if (_debug)
			System.out.println("initiating connector " + id);
		ExtensionClient connector = _pending_connectors.remove(id);
		if (connector == null)
			System.out.println("connect request failed: no connector registered with id=" + id);
		else
			{
			_connectors.put(session, connector);
			connector.connect(session);
			}
		}

	private boolean _debug = false;
	private final Map<Integer, ExtensionClient> _pending_connectors = new HashMap<>();
	private final Map<Session, ExtensionClient> _connectors = new HashMap<>();
	private final Random _connector_id_generator = new Random();

	private static MeddlerExtensionGateway INSTANCE = null;

	@SuppressWarnings("unused")
	@WebSocket
	public class WebsocketHandler
		{
		@OnWebSocketConnect
		public void connected(Session session)
			{
			if (_debug)
				System.out.println("Connected to " + session.getRemote().getInetSocketAddress());
			}

		@OnWebSocketClose
		public void closed(Session session, int statusCode, String reason)
			{
			ExtensionClient connector = _connectors.remove(session);
			if (connector != null)
				{
				connector.closed(statusCode, reason);
				if (_debug)
					System.out.println(String.format("Closed %s with status %d because %s", session.getRemote().getInetSocketAddress(), statusCode, reason));
				}
			}

		@OnWebSocketMessage
		public void message(Session session, String message)
			{
			if (_debug)
				System.out.println("Message received: " + message);
			if (message.startsWith("ConnectTo:"))
				{
				StringTokenizer tokenizer = new StringTokenizer(message, ":");
				tokenizer.nextToken(); // skip prefix
				Integer connetor_id = Integer.parseInt(tokenizer.nextToken());
				initiateConnector(connetor_id, session);
				}
			else
				{
				try
					{
					_connectors.get(session).deliverMessage(new Message(message));
					}
				catch (IOException e)
					{
					e.printStackTrace();
					}
				}
			}
		}

	private final static String SOCKET_PATH = "/socket";
	private final static String PREBOOT_PATH = "/preboot";
	}
