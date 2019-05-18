package de.rudisch.restsql.persistance;

import static eu.rudisch.database.SqlService.query;
import static eu.rudisch.database.SqlService.insert;
import static eu.rudisch.database.SqlService.update;
import static eu.rudisch.database.SqlService.delete;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.rudisch.database.SqlService;

public class ShapePersistance {
	private static final Logger LOG = LogManager.getLogger(ShapePersistance.class);

	private static final String INSERT_SHAPE = "INSERT INTO geometric_shapes"
			+ " (name, dimensions, area)"
			+ " VALUES(?, ?, ?)";
	private static final String UPDATE_SHAPE = "UPDATE geometric_shapes"
			+ " SET dimensions = ?, area = ?"
			+ " WHERE name = ?";
	private static final String SELECT_SHAPE = "SELECT * FROM geometric_shapes";
	private static final String DELETE_SHAPE = "DELETE FROM geometric_shapes"
			+ " WHERE name = ?";
	
	private SqlService sqlService;
	private List<Shape> shapes = new ArrayList<>();
	
	public ShapePersistance(SqlService sqlService) {
		this.sqlService = sqlService;
	}

	public void insertShape(Shape shape) {
		LOG.info("Insert shape: " + shape.toString());
		sqlService.doCalls(con -> insert(con,
				resultSet -> {/* TODO: get inserted keys from resultset */},
				INSERT_SHAPE, 
				shape.getName(),
				shape.getDimension(),
				shape.getArea()));
	}

	public void updateShape(Shape shape) {
		LOG.info("Insert shape: " + shape.toString());
		sqlService.doCalls(con -> update(con,
				updatedRows -> {},
				UPDATE_SHAPE, 
				shape.getDimension(),
				shape.getArea(),
				shape.getName()));
	}

	public List<Shape> selectShapes() {
		LOG.info("Select shapes");
		shapes.clear();
		sqlService.doCalls(con -> query(con,
				resultSet -> addShapes(resultSet),
				SELECT_SHAPE));
		return shapes;
	}
	
	public Shape selectShape(String name) {
		LOG.info("Select shape: " + name);
		shapes.clear();
		sqlService.doCalls(con -> query(con,
				resultSet -> addShapes(resultSet),
				SELECT_SHAPE + "WHERE sku = ?",
				name));
		return shapes.size() == 1 ? shapes.get(0) : null;
	}

	private void addShapes(ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			shapes.add(Shape.fromResultSet(resultSet));
		}
	}

	public boolean deleteShape(String name) {
		LOG.info("Delete shape: " + name);
		return sqlService.doCalls(con -> delete(con,
				deletedRows -> {},
				DELETE_SHAPE, 
				name));
	}
}
