package com.webperformance.browserext.meddler;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class TransactionRecord
	{
	public long getStarted()
		{
		return _started;
		}

	public void setStarted(long started)
		{
		_started = started;
		}

	public long getResponseStarted()
		{
		return _response_started;
		}

	public void setResponseStarted(long response_started)
		{
		_response_started = response_started;
		}

	public long getResponseCompleted()
		{
		return _response_completed;
		}

	public void setResponseCompleted(long response_completed)
		{
		_response_completed = response_completed;
		}

	public int getStatusCode()
		{
		return _status_code;
		}

	public void setStatusCode(int status_code)
		{
		_status_code = status_code;
		}

	public String getUrl()
		{
		return _url;
		}

	public void setUrl(String url)
		{
		_url = url;
		}

	public boolean isFromCache()
		{
		return _from_cache;
		}

	public void setFromCache(boolean from_cache)
		{
		_from_cache = from_cache;
		}

	@Override
	public String toString()
		{
		return String.format("%d %s in %sms (TTFB=%d)", _status_code, _url, _response_completed - _started, _response_started - _started);
		}

	private long _started;
	private long _response_started;
	private long _response_completed;
	private int _status_code;
	private boolean _from_cache;
	private String _url;
	}
