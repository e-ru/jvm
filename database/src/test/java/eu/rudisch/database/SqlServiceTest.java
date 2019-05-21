/* Licensed under Apache-2.0 */
package eu.rudisch.database;

import static eu.rudisch.database.SqlService.create;
import static eu.rudisch.database.SqlService.delete;
import static eu.rudisch.database.SqlService.drop;
import static eu.rudisch.database.SqlService.insert;
import static eu.rudisch.database.SqlService.query;
import static eu.rudisch.database.SqlService.update;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class SqlServiceTest {

	private static final String DB_CONNECTION = "jdbc:h2:./db/test";
	private static final String DB_USER = "sa";
	private static final String DB_PASSWORD = "";

	private static final String CREATE_TEST_TABLE = "CREATE TABLE IF NOT EXISTS test_table(id int auto_increment primary key, param_1 varchar(255), param_2 int)";
	private static final String DROP_TEST_TABLE = "DROP TABLE IF EXISTS test_table;";

	private static final String INSERT_ENTRY = "INSERT INTO test_table(param_1, param_2) VALUES(?, ?)";
	private static final String SELECT = "SELECT * from test_table";
	private static final String SELECT_ENTRY = "SELECT * from test_table WHERE id = ?";
	private static final String UPDATE_ENTRY = "UPDATE test_table SET param_1 = ?, param_2 = ? WHERE id = ?";
	private static final String DELETE_ENTRY = "DELETE from test_table WHERE id = ?";

	static SqlService sqlService;

	Integer resultCount = null;

	String param1 = null;
	Integer param2 = null;

	@BeforeAll
	static void init() {
		sqlService = new SqlService(DB_CONNECTION, DB_USER, DB_PASSWORD);
	}

	@AfterEach
	void destroyEach() {
		resultCount = null;
	}

	@AfterAll
	static void destroy() {
		sqlService = null;
	}

	@Test
	@Order(1)
	void shouldCreateTable() {
		sqlService.doCalls(con -> create(con,
				uc -> this.resultCount = uc,
				CREATE_TEST_TABLE));
		assertNotNull(resultCount);
	}

	@Test
	@Order(2)
	void shouldInsertEntry() {
		sqlService.doCalls(con -> insert(con,
				resultSet -> setResultCount(resultSet),
				INSERT_ENTRY,
				"test_param1",
				1));
		assertNotNull(resultCount);
	}

	void setResultCount(ResultSet resultSet) throws SQLException {
		if (resultSet.next()) {
			resultCount = resultSet.getInt("id");
		}
	}

	@Test
	@Order(3)
	void shouldSelectAll() {
		sqlService.doCalls(con -> query(con,
				resultSet -> setSqlResult(resultSet),
				SELECT));
		assertEquals(1, resultCount);
		assertEquals("test_param1", param1);
		assertEquals(1, param2);
	}

	@Test
	@Order(4)
	void shouldSelectById() {
		sqlService.doCalls(con -> query(con,
				resultSet -> setSqlResult(resultSet),
				SELECT_ENTRY,
				1));
		assertEquals(1, resultCount);
		assertEquals("test_param1", param1);
		assertEquals(1, param2);
	}

	void setSqlResult(ResultSet resultSet) throws SQLException {
		resultCount = 0;
		while (resultSet.next()) {
			param1 = resultSet.getString("param_1");
			param2 = resultSet.getInt("param_2");
			resultCount++;
		}
	}

	@Test
	@Order(5)
	void shouldUpdateById() {
		sqlService.doCalls(con -> update(con,
				uc -> this.resultCount = uc,
				UPDATE_ENTRY,
				"test_param1_modified",
				2,
				1));
		assertNotNull(resultCount);
	}

	@Test
	@Order(6)
	void shouldDeleteEntryById() {
		sqlService.doCalls(con -> delete(con,
				uc -> this.resultCount = uc,
				DELETE_ENTRY,
				1));
		assertNotNull(resultCount);
	}

	@Test
	@Order(7)
	void shouldDropTable() {
		sqlService.doCalls(con -> drop(con,
				uc -> this.resultCount = uc,
				DROP_TEST_TABLE));
		assertNotNull(resultCount);
	}

}
