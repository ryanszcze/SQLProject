package mysqltest1;

import mysqltest1.MySQLAccess;

public class Main {
    public static void main(String[] args) throws Exception {
        MySQLAccess dao = new MySQLAccess();
        dao.mySQLAccess();
       //dao.createLot("lot1", 35);
     // dao.wipeDatabase("Lot1");
        int test=dao.getLotCount("lot1");
      boolean searchval= dao.searchCar("DUD33");
      if (searchval==true)
    	  System.out.println("Found it");
      else
    	  System.out.println("Couldnt find it");
        //dao.addCar("lot1", "ABC123");
        System.out.println(test);
      //  dao.readDataBase();
    
    }

}