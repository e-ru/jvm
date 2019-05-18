/* Licensed under Apache-2.0 */
package eu.rudisch.database;

import java.sql.Types;

enum DatabaseTypes {
    CHAR(Types.CHAR), INTEGER(Types.INTEGER);

    private final int id;

    DatabaseTypes(int id) {
	this.id = id;
    }

    public int getValue() {
	return id;
    }

}
