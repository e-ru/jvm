/* Licensed under Apache-2.0 */
package eu.rudisch.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SqlService {
    private static final Logger LOG = LogManager.getLogger(SqlService.class);

    private String driver;
    private String url;

    public SqlService(String driver, String connection, String user, String password) {
	this.driver = driver;
	this.url = String.format("%s?user=%s&password=%s", connection, user, password);
    }

    @SafeVarargs
    public final boolean doCalls(IDatabaseConsumer<Connection>... calls) {
	Connection con = null;
	try {
	    con = connectTo();
	    con.setAutoCommit(false);
	    for (IDatabaseConsumer<Connection> call : calls) {
		call.accept(con);
	    }
	    con.commit();
	} catch (SQLException | ClassNotFoundException e) {
	    LOG.error("Failed to read database.", e);
	    rollback(con);
	    return false;
	} finally {
	    closeStatic(con);
	}
	return true;
    }

    synchronized Connection connectTo() throws SQLException, ClassNotFoundException {
	Class.forName(driver);
	return DriverManager.getConnection(url);
    }

    void rollback(Connection con) {
	if (con != null)
	    try {
		con.rollback();
	    } catch (SQLException e) {
		LOG.error("Failed to rollback connection.", e);
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
		} else if (o instanceof DatabaseTypes) {
		    stmt.setNull(k, ((DatabaseTypes) o).getValue());
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
	    Connection con, String sql, Object... params) {
	T res = null;
	PreparedStatement stmt = null;
	try {
	    stmt = prepareStatement(con, false, sql, params);
	    if (stmt.execute() && consumer != null) {
		res = function.apply(stmt);
		consumer.accept(res);
	    }
	} catch (SQLException e) {
	    LOG.error("Failed to query database.", e);
	} finally {
	    closeStatic(stmt);
	    if (res instanceof ResultSet)
		closeStatic((ResultSet) res);
	}
    }

    public static void query(Connection con, IDatabaseConsumer<ResultSet> consumer, String sql, Object... params) {
	execute(consumer, stmt -> stmt.getResultSet(), con, sql, params);
    }

    public static void insert(Connection con, IDatabaseConsumer<ResultSet> consumer, String sql, Object... params) {
	execute(consumer, stmt -> stmt.getGeneratedKeys(), con, sql, params);
    }

    public static void update(Connection con, IDatabaseConsumer<Integer> consumer, String sql, Object... params) {
	execute(consumer, stmt -> stmt.getUpdateCount(), con, sql, params);
    }

    public static void delete(Connection con, IDatabaseConsumer<Integer> consumer, String sql, Object... params) {
	execute(consumer, stmt -> stmt.getUpdateCount(), con, sql, params);
    }

    private static void closeStatic(AutoCloseable autoCloseable) {
	if (autoCloseable != null) {
	    try {
		autoCloseable.close();
	    } catch (Exception e) {
	    }
	}
    }
}
