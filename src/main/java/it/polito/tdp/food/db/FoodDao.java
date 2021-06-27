package it.polito.tdp.food.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.food.model.Arco;
import it.polito.tdp.food.model.Condiment;
import it.polito.tdp.food.model.Food;
import it.polito.tdp.food.model.Portion;

public class FoodDao {
	public List<Food> listAllFoods(){
		String sql = "SELECT * FROM food" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Food> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Food(res.getInt("food_code"),
							res.getString("display_name")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

	}
	
	public List<Condiment> listAllCondiments(){
		String sql = "SELECT * FROM condiment" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Condiment> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Condiment(res.getInt("condiment_code"),
							res.getString("display_name"),
							res.getDouble("condiment_calories"), 
							res.getDouble("condiment_saturated_fats")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Portion> listAllPortions(){
		String sql = "SELECT * FROM portion" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Portion> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Portion(res.getInt("portion_id"),
							res.getDouble("portion_amount"),
							res.getString("portion_display_name"), 
							res.getDouble("calories"),
							res.getDouble("saturated_fats"),
							res.getInt("food_code")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

	}
	public void listVertices(int x, Map<Integer,Food> idMap){
		String sql = "SELECT f.food_code as code, f.display_name as name "
				+ "FROM food as f, `portion` as p "
				+ "WHERE p.food_code=f.food_code "
				+ "GROUP BY f.display_name "
				+ "HAVING COUNT(distinct(p.portion_id))>=? " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			
			st.setInt(1, x);
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					idMap.put(res.getInt("food_code"),new Food(res.getInt("food_code"),
							res.getString("display_name")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			

		} catch (SQLException e) {
			e.printStackTrace();
			
		}

	}
	
	public void listEdges(int x, List<Arco> archi, Map<Integer,Food> idMap){
		String sql = "SELECT t1.fc AS f1, t2.fc AS f2, t1.avg-t2.avg AS diff "
				+ "FROM( "
				+ "		(SELECT p.food_code AS fc, AVG(p.saturated_fats) AS avg "
				+ "		FROM  `portion` p "
				+ "		GROUP BY p.food_code "
				+ "		HAVING COUNT(p.portion_id)>=?) AS t1, "
				+ "		(SELECT p.food_code AS fc, AVG(p.saturated_fats) AS avg "
				+ "		FROM  `portion` p "
				+ "		GROUP BY p.food_code "
				+ "		HAVING COUNT( distinct p.portion_id)>=?) "
				+ "		AS t2 "
				+ "		) "
				+ "WHERE t1.fc>t2.fc "
				+ "GROUP BY t1.fc,t2.fc "
				+ "		" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			
			st.setInt(1, x);
			st.setInt(2, x);
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					archi.add(new Arco(idMap.get(res.getInt("f1")),idMap.get(res.getInt("f2")),res.getDouble("diff")));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			

		} catch (SQLException e) {
			e.printStackTrace();
			
		}

	}
}
