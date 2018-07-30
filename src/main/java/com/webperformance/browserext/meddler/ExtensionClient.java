package com.webperformance.browserext.meddler;

import com.webperformance.browserext.meddler.message.*;
import org.eclipse.jetty.websocket.api.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public interface ExtensionClient
	{
	void setId(int id);
	void connect(Session session);
	void closed(int status, String reason);
	void deliverMessage(Message message);
	}
