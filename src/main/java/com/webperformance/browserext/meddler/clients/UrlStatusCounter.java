package com.webperformance.browserext.meddler.clients;

import com.webperformance.browserext.meddler.*;
import com.webperformance.browserext.meddler.message.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class UrlStatusCounter implements MessageListener
	{
	public UrlStatusCounter(ExtensionConnection connection, int count_status)
		{
		_connection = connection;
		_checker = status -> count_status == status;
		_connection.addListener(this);
		}

	public UrlStatusCounter(ExtensionConnection connection, StatusCheck checker)
		{
		_connection = connection;
		_checker = checker;
		_connection.addListener(this);
		}

	public synchronized String report()
		{
		if (_count == 0)
			return "No matching transactions were encountered.";
		return _report.toString();
		}

	public int getTotal()
		{
		return _count;
		}

	public int getTransactionReceivedCount()
		{
		return _txn_count;
		}

	@Override
	public void messageReceived(Message message)
		{
		if ("transaction_summary".equals(message.getHeader().getType()))
			{
			TransactionRecord txn = message.getPayload(TransactionRecord.class);
			if (txn != null)
				{
				_txn_count++;
				if (_checker.countStatus(txn.getStatusCode()))
					{
					if (_count > 0)
						_report.append("\n");
					_report.append(txn.getUrl());
					_count++;
					}
				}
			}
		}

	private final StatusCheck _checker;
	private final ExtensionConnection _connection;
	private int _txn_count = 0;
	private int _count = 0;
	private StringBuilder _report = new StringBuilder();

	public interface StatusCheck
		{
		boolean countStatus(int status);
		}
	}
