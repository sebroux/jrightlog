
import java.sql.*;

/**
 *
 * @author seb
 */
public class DB {

    private static String dbURL = "jdbc:derby:jrightlog2;create=true;create=true;user=adm;password=xxx";
    private static String tableName = "logs";
    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;

    @SuppressWarnings("static-access")
    public static void main(String args[]) {

        Reconstruct reconstruct = new Reconstruct();

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            //Get a connection
            conn = DriverManager.getConnection(dbURL);
            Statement sta = conn.createStatement();

            try {
                sta.executeUpdate("drop table log");
            } catch (SQLException sqle) {
            }

            PreparedStatement pst;
/*
            if (reconstruct.getCategories() == true) {
                sta.executeUpdate("create table log (sdate date, stime time, svr varchar(32), app varchar(32), dbs varchar(32), usr varchar(32), level varchar(11), msgcode varchar(8), cat varchar(60), des varchar(1000))");
                pst = conn.prepareStatement("insert into logs values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                
                pst.setDate(1, );
                pst.setTime(2, );
                pst.setString(3, );
                pst.setString(4, );
                pst.setString(5, );
                pst.setString(6, );
                pst.setString(7, );
                pst.setString(8, );
                pst.setString(9, );
                pst.setString(10, );
                pst.executeUpdate();
            } else {
                sta.executeUpdate("create table log (sdate date, stime time, svr varchar(32), app varchar(32), dbs varchar(32), usr varchar(32), level varchar(11), msgcode varchar(8), des varchar(1000))");
                pst = conn.prepareStatement("insert into logs values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

                pst.setDate(1, );
                pst.setTime(2, );
                pst.setString(3, );
                pst.setString(4, );
                pst.setString(5, );
                pst.setString(6, );
                pst.setString(7, );
                pst.setString(8, );
                pst.setString(9, );
                pst.executeUpdate();
            }

            pst.close();
            */
            //sta.executeUpdate("insert into log (id) values (1000)");
            //sta.executeUpdate("insert into log (id) values (2000)");
            //sta.executeUpdate("insert into log (id) values (3000)");

            //ResultSet res = sta.executeQuery("select * from log");

            /*
            while (res.next()) {
            System.out.println(
            res.getInt("ID"));
            }
             */
            //res.close();
            sta.close();
            conn.close();
        } catch (Exception except) {
            except.printStackTrace();
        }


    }
}
