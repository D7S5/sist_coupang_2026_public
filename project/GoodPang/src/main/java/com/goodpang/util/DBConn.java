package com.goodpang.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {
	
	
	// DCL Conn 객체가 완전히 생성 되기 전에 다른 스레드가 접근하는 문제를 방지하는 중요한 역할
	private static volatile Connection conn = null;
	
	private DBConn() {}
	
public static Connection getConnection(String url, String user, String password) {
		
		if ( conn == null ) {
			// Connection 객체를 생성
			String className = "oracle.jdbc.driver.OracleDriver";
			
			synchronized ( DBConn.class ) {
				if ( conn == null ) {
					try {
						Class.forName(className);
						conn =
							DriverManager.getConnection(url, user, password);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return conn;
		
	}
	
	public static Connection getConnection() {
		
		if ( conn == null ) {
			// Connection 객체를 생성
			String className = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
			String user = "scott";
			String password = "tiger";
			
			synchronized ( DBConn.class ) {
				if ( conn == null ) {
					try {
						Class.forName(className);
						conn =
							DriverManager.getConnection(url, user, password);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return conn;
		
	}
	
	public static void close() {
		
		try {
			if (conn != null && !conn.isClosed())
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn = null;
		}	
	}
}
