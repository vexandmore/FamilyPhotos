/*
 * Copyright Marc Scattolin 2020
 */
package marc.FamilyPhotos;

import jakarta.servlet.*;
import java.sql.*;
import java.util.Enumeration;

/**
 * Registers and deregisters JDBC driver on start and stop
 * @author Marc
 */
public class Listener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//register driver
		/*try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			sce.getServletContext().log("Could not register JDBC driver", e);
		}*/
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		/*Enumeration<Driver> drivers = DriverManager.getDrivers();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader().equals(cl)) {
				sce.getServletContext().log("Deregistering jdbc driver");
				try {
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e) {
					sce.getServletContext().log("Error deregistering jdbc driver", e);
				}
			}
		}*/
	}
}
