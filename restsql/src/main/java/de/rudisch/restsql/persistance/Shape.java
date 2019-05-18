package de.rudisch.restsql.persistance;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Shape {
	private final String name;
	private final int dimension;
	private final String area;

	private Shape(String name, int dimension, String area) {
		this.name = name;
		this.dimension = dimension;
		this.area = area;
	}
	
	public String getName() {
		return name;
	}

	public int getDimension() {
		return dimension;
	}

	public String getArea() {
		return area;
	}
	
	@Override
	public String toString() {
		return "Shape [name=" + name + ", dimension=" + dimension + ", area=" + area + "]";
	}
	
	public static Shape fromParams(String name, int dimension, String area) {
		return new Shape(name, dimension, area);
	}
	
	public static Shape fromResultSet(ResultSet resultSet) throws SQLException {
		return new Shape(resultSet.getString("name"), resultSet.getInt("dimensions"), 
				resultSet.getString("area"));
	}
}
