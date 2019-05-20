/* Licensed under Apache-2.0 */
package eu.rudisch.database;

import java.sql.SQLException;

@FunctionalInterface
public interface IDatabaseConsumer<T> {
	void accept(T t) throws SQLException;
}
