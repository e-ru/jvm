/* Licensed under Apache-2.0 */
package eu.rudisch.database;

import java.sql.SQLException;

@FunctionalInterface
interface IDatabaseFunction<T, R> {
	R apply(T t) throws SQLException;
}
