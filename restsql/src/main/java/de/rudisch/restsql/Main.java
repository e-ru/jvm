/* Licensed under Apache-2.0 */
package de.rudisch.restsql;

import java.util.List;

import de.rudisch.restsql.persistance.Shape;
import de.rudisch.restsql.persistance.ShapePersistance;
import eu.rudisch.database.SqlService;

public class Main {

	/**
	 * Entry point.
	 * 
	 * @param args cmd line args
	 */
	public static void main(String[] args) {
		SqlService sqlService = new SqlService("com.mysql.cj.jdbc.Driver",
				"jdbc:mysql://172.18.0.3:3306/mathematics",
				"sqluser",
				"sqluserpw");
		ShapePersistance shapePersistance = new ShapePersistance(sqlService);

		Shape shape = Shape.fromParams("circle", 2, "pi*r*r");
		shapePersistance.insertShape(shape);

		shape = Shape.fromParams("circle", 3, "pi*r*r");
		shapePersistance.updateShape(shape);

		List<Shape> shapes = shapePersistance.selectShapes();
		shapes.size();

		shapePersistance.deleteShape(shape.getName());
	}

	public String hello() {
		return "hello";
	}
}
