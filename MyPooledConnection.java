package poolconnection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

public class MyPooledConnection implements PooledConnection {

	private final Connection connection;

	private final ConnectionEventListener listener;

	public MyPooledConnection(final Connection connection, final ConnectionEventListener listener) {
		this.connection = connection;
		this.listener = listener;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}

	@Override
	public void close() throws SQLException {
		listener.connectionClosed(new ConnectionEvent(this));
	}

	@Override
	public void addConnectionEventListener(final ConnectionEventListener listener) {
	}

	@Override
	public void removeConnectionEventListener(final ConnectionEventListener listener) {
	}

	@Override
	public void addStatementEventListener(final StatementEventListener listener) {
	}

	@Override
	public void removeStatementEventListener(final StatementEventListener listener) {
	}

}
