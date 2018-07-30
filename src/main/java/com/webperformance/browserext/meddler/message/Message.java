package com.webperformance.browserext.meddler.message;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class Message
	{
	public Message(String raw) throws IOException
		{
		StringTokenizer tokenizer = new StringTokenizer(raw, "|");
		_header = MAPPER.readValue(tokenizer.nextToken(), MessageHeader.class);
		if (tokenizer.hasMoreTokens())
			_payload = tokenizer.nextToken();
		else
			_payload = null;
		}

	public Message(MessageHeader header, Object payload)
		{
		_header = header;
		if (payload instanceof String)
			_payload = (String) payload;
		else
			{
			String stringified;
			try
				{
				stringified = MAPPER.writeValueAsString(payload);
				}
			catch (JsonProcessingException e)
				{
				final String message = "Unable to serialize the payload to JSON due to: " + e.getMessage();
				System.err.println(message);
				stringified = message;
				}
			_payload = stringified;
			}
		}

	public MessageHeader getHeader()
		{
		return _header;
		}

	public String getPayload()
		{
		return _payload;
		}

	@SuppressWarnings("TypeParameterExplicitlyExtendsObject")
	public <T extends Object> T getPayload(Class<T> type)
		{
		try
			{
			return MAPPER.readValue(_payload, type);
			}
		catch (IOException e)
			{
			System.err.println("Expected a serialized Transaction record, but message payload was: " + _payload);
			return null;
			}
		}

	@Override
	public String toString()
		{
		StringBuilder builder = new StringBuilder();
		try
			{
			builder.append(MAPPER.writeValueAsString(_header));
			}
		catch (JsonProcessingException e)
			{
			final String message = "Unable to serialize the header to JSON due to: " + e.getMessage();
			System.err.println(message);
			return "message";
			}
		if (_payload != null)
			{
			builder.append("|");
			builder.append("|");
			}
		return builder.toString();
		}

	private final MessageHeader _header;
	private final String _payload;

	private static ObjectMapper MAPPER = new ObjectMapper();
	}
