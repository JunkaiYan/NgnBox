package backend;

import java.sql.*;

import org.json.JSONException;
import org.json.JSONObject;

public class DBcontroller {
	// set mySQL DB configurations
	static final String USER = "";
	static final String PASS = "";
	static final String url = "";
	static final String DB_URL = "jdbc:mysql://" + url;

	// set the format of the table
	static final String getPwd = "SELECT * from info where username = ?";
	static final String selectSQL = "SELECT * from box where username = ?";
	static final String deleteSQL = "DELETE FROM box where username = ? and filename = ?";


	// JDBC connection and statement
	Connection conn;
	PreparedStatement stmt;

	public void setConnnection() {
		try {
			System.out.println("configure JDBC driver");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public boolean insertFile(String username, String filename, Timestamp timestamp) {
		if(checkFileExisted(username, filename)) return false;
		
		String FileField = "(username, filename, uploadTime)";
		String valueField = "(?, ?, ?)";
		String insertSQL = "INSERT INTO box " + FileField + " VALUES " + valueField;
		try {
			PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
			insertStmt.setString(1, username);
			insertStmt.setString(2, filename);
			insertStmt.setTimestamp(3, timestamp);
			insertStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean insertUser(String username, String password) {
		if(checkUserExisted(username)) return false;
		
		String FileField = "(username, password)";
		String valueField = "(?, ?)";
		String insertSQL = "INSERT INTO info " + FileField + " VALUES " + valueField;
		try {
			PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
			insertStmt.setString(1, username);
			insertStmt.setString(2, password);
			insertStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void deleteEntry(String username, String filename) {
		try {
			PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
			deleteStmt.setString(1, username);
			deleteStmt.setString(2, filename);
			deleteStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getPassword(String username) {
		String password = null;
		try {
			PreparedStatement selectStmt = conn.prepareStatement(getPwd);
			selectStmt.setString(1, username);
			ResultSet rs = selectStmt.executeQuery();

			while (rs.next()) {
				password = rs.getString("password");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return password;
	}

	public String getMessage(String username) {
		StringBuilder sb = new StringBuilder();
		try {
//			System.out.println(username);
			PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
			selectStmt.setString(1, username);
			ResultSet rs = selectStmt.executeQuery();

			while (rs.next()) {
				JSONObject obj = new JSONObject();
				String timestamp = String.valueOf(rs.getTimestamp("uploadTime"));
				obj.put("file", rs.getString("filename")).put("time", timestamp.substring(0, timestamp.length() - 2));
				sb.append(obj);
				sb.append("\n");
			}
			if(sb.length() == 0) return null;
			sb.deleteCharAt(sb.length() - 1);
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public boolean checkFileExisted(String username, String filename) {
		// TODO Auto-generated method stub
		String checkSQL = "SELECT COUNT(username) AS number FROM box WHERE username = ? and filename = ?";
		try {
			PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
			checkStmt.setString(1, username);
			checkStmt.setString(2, filename);
			ResultSet rs = checkStmt.executeQuery();
			int count = 0;
			while(rs.next()){
				count = rs.getInt("number");
			}
			if(count != 0) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean checkUserExisted(String username) {
		// TODO Auto-generated method stub
		String checkSQL = "SELECT COUNT(username) AS number FROM info WHERE username = ?";
		try {
			PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
			checkStmt.setString(1, username);
			ResultSet rs = checkStmt.executeQuery();
			int count = 0;
			while(rs.next()){
				count = rs.getInt("number");
			}
			if(count != 0) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static java.sql.Timestamp uDateToSDate(java.util.Date uDate) {
		java.sql.Timestamp sDate = new java.sql.Timestamp(uDate.getTime());
		return sDate;
	}
}
