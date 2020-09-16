package poolconnection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

public class MyConnectionPoolDataSource implements ConnectionPoolDataSource, ConnectionEventListener {
	private static final Logger log = Logger.getLogger("MyConnectionPoolDataSource");

	private static final String url = "jdbc:postgresql://localhost:5433/db_mce_dev01";

	private static final String user = "postgres";

	private static final String password = "";

	private final int depth;

	private PooledConnection[] pool;

	private boolean[] dispo;

	public MyConnectionPoolDataSource(final int depth) throws SQLException {
		this.depth = depth;
		pool = new MyPooledConnection[depth];
		dispo = new boolean[depth];
		for (int i = 0; i < depth; i++) {
			Connection conn = DriverManager.getConnection(url, user, password);
			pool[i] = new MyPooledConnection(conn, this);
			dispo[i] = true;
		}
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public synchronized PooledConnection getPooledConnection() throws SQLException {
		for (int i = 0; i < depth; i++) {
			if (dispo[i]) {
				dispo[i] = false;
				return pool[i];
			}
		}
		return null;
	}

	@Override
	public PooledConnection getPooledConnection(final String user, final String password) throws SQLException {
		return null;
	}

	@Override
	public synchronized void connectionClosed(final ConnectionEvent event) {
		Object source = event.getSource();
		for (int i = 0; i < depth; i++) {
			PooledConnection pooledConnection = pool[i];
			if (pooledConnection.equals(source)) {
				dispo[i] = true;
			}
		}
	}

	@Override
	public void connectionErrorOccurred(final ConnectionEvent event) {
	}

	public void close() {
		for (PooledConnection pooledConnection : pool) {
			try {
				pooledConnection.getConnection().close();
			} catch (SQLException e) {
				log.warning(e.getMessage());
			}
		}
	}

	public static void main(final String[] args) throws SQLException {
		boolean assertEnabled = false;
		assert assertEnabled = true;
		if (!assertEnabled) {
			throw new AssertionError("Lancer avec l'argument VM -ea");
		}

		final int N = 7;
		MyConnectionPoolDataSource server = new MyConnectionPoolDataSource(N);
		PooledConnection[] mpc = new MyPooledConnection[N];
		for (int i = 0; i < N; i++) {
			mpc[i] = server.getPooledConnection();
		}
		assert server.getPooledConnection() == null;
		mpc[4].close();
		assert server.getPooledConnection() != null;
		server.close();
	}

}
