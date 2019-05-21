/* Licensed under Apache-2.0 */
package eu.rudisch.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SqlService {

	private static final Logger LOG = LogManager.getLogger(SqlService.class);

	private String url;
	private String user;
	private String password;

	private BasicDataSource dataSource = null;

	public SqlService(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
		initDataSource();
	}

	@SafeVarargs
	public final boolean doCalls(IDatabaseConsumer<Connection>... calls) {
		Connection con = null;
		try {
			con = getConnection();
			con.setAutoCommit(false);
			for (IDatabaseConsumer<Connection> call : calls) {
				call.accept(con);
			}
			con.commit();
		} catch (SQLException e) {
			LOG.error("Failed to read database.", e);
			rollback(con);
			return false;
		} finally {
			closeStatic(con);
		}
		return true;
	}

	void initDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(password);
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
		ds.setMaxOpenPreparedStatements(100);
		dataSource = ds;
	}

	Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	void rollback(Connection con) {
		if (con != null) {
			try {
				con.rollback();
			} catch (SQLException e) {
				LOG.error("Failed to rollback connection.", e);
			}
		}
	}

	private static PreparedStatement prepareStatement(Connection con, boolean autoGenKeys, String sql,
			Object... params) {
		PreparedStatement stmt = null;
		try {
			stmt = autoGenKeys ? con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) : con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				int k = i + 1;
				Object o = params[i];
				if (o instanceof String) {
					stmt.setString(k, String.valueOf(o));
				} else if (o instanceof Long) {
					stmt.setLong(k, (Long) o);
				} else if (o instanceof Boolean) {
					stmt.setBoolean(k, (Boolean) o);
				} else if (o instanceof DatabaseType) {
					stmt.setNull(k, ((DatabaseType) o).getValue());
				} else {
					stmt.setObject(k, o);
				}
			}
		} catch (SQLException e) {
			LOG.error("Error creating statement: ", e);
		}
		return stmt;
	}

	private static <T> void execute(IDatabaseConsumer<T> consumer, IDatabaseFunction<PreparedStatement, T> function,
			Connection con, boolean returnGeneratedKeys, String sql, Object... params) {
		T res = null;
		PreparedStatement stmt = null;
		try {
			stmt = prepareStatement(con, returnGeneratedKeys, sql, params);
			stmt.execute();
			if (consumer != null && function != null) {
				res = function.apply(stmt);
				consumer.accept(res);
			}
		} catch (SQLException e) {
			LOG.error("Failed to query database.", e);
		} finally {
			closeStatic(stmt);
			if (res instanceof ResultSet) {
				closeStatic((ResultSet) res);
			}
		}
	}

	private static void updateCount(Connection con, IDatabaseConsumer<Integer> consumer, String sql, Object... params) {
		execute(consumer, stmt -> stmt.getUpdateCount(), con, false, sql, params);
	}

	public static void create(Connection con, IDatabaseConsumer<Integer> consumer, String sql, Object... params) {
		updateCount(con, consumer, sql, params);
	}

	public static void drop(Connection con, IDatabaseConsumer<Integer> consumer, String sql, Object... params) {
		updateCount(con, consumer, sql, params);
	}

	public static void query(Connection con, IDatabaseConsumer<ResultSet> consumer, String sql, Object... params) {
		execute(consumer, stmt -> stmt.getResultSet(), con, false, sql, params);
	}

	public static void insert(Connection con, IDatabaseConsumer<ResultSet> consumer, String sql, Object... params) {
		execute(consumer, stmt -> stmt.getGeneratedKeys(), con, true, sql, params);
	}

	public static void update(Connection con, IDatabaseConsumer<Integer> consumer, String sql, Object... params) {
		updateCount(con, consumer, sql, params);
	}

	public static void delete(Connection con, IDatabaseConsumer<Integer> consumer, String sql, Object... params) {
		updateCount(con, consumer, sql, params);
	}

	private static void closeStatic(AutoCloseable autoCloseable) {
		if (autoCloseable != null) {
			try {
				autoCloseable.close();
			} catch (Exception e) {
				LOG.error("Error in closing resource", e);
			}
		}
	}
}
