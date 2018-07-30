package com.webperformance.browserext.meddler.message;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused") // class is de/serialized via reflection (from JSON) and needs all getters/setters public.
public class MessageHeader
	{
	public MessageHeader()
		{
		}

	public MessageHeader(String type)
		{
		_type = type;
		}

	public String getType()
		{
		return _type;
		}

	public void setType(String type)
		{
		_type = type;
		}

	public boolean isResponseExpected()
		{
		return _response_expected;
		}

	public void setResponseExpected(boolean response_expected)
		{
		_response_expected = response_expected;
		}

	public Map<String, Object> getParameters()
		{
		return _parameters;
		}

	public void setParameters(Map<String, Object> parameters)
		{
		_parameters = parameters;
		}

	private String _type;
	private boolean _response_expected = false;
	private Map<String, Object> _parameters = new HashMap<>();
	}
