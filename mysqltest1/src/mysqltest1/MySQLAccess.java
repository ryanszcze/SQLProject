package mysqltest1;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class MySQLAccess {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    
    
    public void mySQLAccess() throws Exception {
      
    }
    
    
    
    public int getLotCount(String lotNameIn) throws Exception{
    	
    	
    	
    	 try {
         	Class.forName("com.mysql.jdbc.Driver");
             // Setup the connection with the DB
             connect = DriverManager
                     .getConnection("jdbc:mysql://localhost/feedback?"
                             + "user=mydatabase_admin&password=ryan3317");
             // Statements allow to issue SQL queries to the database
             statement = connect.createStatement();
             // Result set get the result of the SQL query
          

          
//obtain the count from the table we want to access

             preparedStatement = connect
                     .prepareStatement("SELECT COUNT(*) FROM mydatabase."+lotNameIn);
             resultSet = preparedStatement.executeQuery();
             
//Access the count from the master table to subtract the difference
             int currCount=0;
             while(resultSet.next())
             {
             	
             
             currCount= resultSet.getInt("COUNT(*)");
            
             
             }
             
             int totalCount=getLotSize("lot1");
             return totalCount-currCount;
         } catch (Exception e) {
             throw e;
         } finally {
             close();
         }

    	
    }
    
    
    public void readDataBase() throws Exception {
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=mydatabase_admin&password=ryan3317");
            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
         

         


            preparedStatement = connect
                    .prepareStatement("SELECT * from mydatabase.lotList");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);

           

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }
    
   // public void removeFromDatabse(String lotNameIn, int LotNumIn) throws Exception
    
    public void wipeDatabase(String lotNameIn) throws Exception
    {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=mydatabase_admin&password=ryan3317");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            

        


            preparedStatement = connect
                    .prepareStatement("DELETE FROM mydatabase.lotlist WHERE lotName='"+lotNameIn+"'");
            preparedStatement.executeUpdate();
            
            preparedStatement = connect.prepareStatement("DROP TABLE mydatabase."+lotNameIn);
            preparedStatement.executeUpdate();
       //prepareconnect

        

         

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }
    
    public void removeCar (String plateNumIn) throws Exception
    {
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=mydatabase_admin&password=ryan3317");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            

        


            preparedStatement = connect
                    .prepareStatement("DELETE FROM mydatabase.lot1 WHERE PlateNumber='"+plateNumIn+"'");
            preparedStatement.executeUpdate();
            
         
    	}
     catch (Exception e) {
        throw e;
    } finally {
        close();
    }
    }
    
    public boolean searchCar(String plateNumIn) throws Exception
    {int matchesFound = 0;
    	try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=mydatabase_admin&password=ryan3317");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
   

            preparedStatement = connect
                    .prepareStatement("SELECT * FROM mydatabase.lot1 where PlateNumber='"+plateNumIn+"'");
            resultSet = preparedStatement.executeQuery();
         
           // preparedStatement.executeUpdate();
          
            while(resultSet.next())
            {
            	
            
            String result1= resultSet.getString("PlateNumber");
            matchesFound++;
            //System.out.println(result);
            if (matchesFound>=1)
            {
            	//System.out.println(matchesFound);
            return true;
            }
            
            else return false;
            }
            
            return false;
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }
    public void addCar(String lotNameIn, String plateNum) throws Exception
    {
    	try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=mydatabase_admin&password=ryan3317");

           
            statement = connect.createStatement();
       
            preparedStatement = connect
                    .prepareStatement("insert into  mydatabase."+lotNameIn+ "(PlateNumber) values ('"+plateNum+"')");
           
            preparedStatement.executeUpdate();
       
            

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }
    
    public void createLot(String lotNameIn, int lotNumIn) throws Exception
    {
    	try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=mydatabase_admin&password=ryan3317");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
        
            //writeResultSet(resultSet);

            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  mydatabase.lotList(lotName, lotSize) values ('"+ lotNameIn+ "','"+ lotNumIn+"')");
           
            preparedStatement.executeUpdate();
         preparedStatement = connect.prepareStatement("CREATE TABLE mydatabase." + lotNameIn +" (PlateNumber VARCHAR(20))");
            preparedStatement.executeUpdate();
           

            

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }
    
    
    
    public int getLotSize(String lotNameIn) throws Exception
    {
    	try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=mydatabase_admin&password=ryan3317");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
   

            preparedStatement = connect
                    .prepareStatement("SELECT * FROM mydatabase.lotList");
            resultSet = preparedStatement.executeQuery();
         
           // preparedStatement.executeUpdate();
          
            while(resultSet.next())
            {
            	
            
            int result= resultSet.getInt("lotSize");
            //System.out.println(result);
            return result;
            }
            
            return 0;
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    	
    	
    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            String lot = resultSet.getString("LotName");
            int lotSize = resultSet.getInt("LotSize");
           
            System.out.println("Lot: " + lot);
            System.out.println("Space Available: " + lotSize);
            
        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

}