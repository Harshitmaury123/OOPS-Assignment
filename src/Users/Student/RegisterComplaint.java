package Users.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterComplaint {
    private final Connection con;
    RegisterComplaint(Connection con)
    {
        this.con=con;
    }
    public void giveComplaints(String rollNo, String msg) {
        String query = "INSERT INTO complaint(sid,description,status) values(?,?,?)";
        PreparedStatement ps=null;
        try {
            //Query to insert Complaints


            //Compiling Query
             ps = con.prepareStatement(query);

            //setting values
            ps.setString(1, rollNo);
            ps.setString(2,msg);
            ps.setString(3,"pending");

            //return no of rows affected
            int rowsAffected = ps.executeUpdate();

            //checking Conditions
            if (rowsAffected>0){
                System.out.println("Complaint Registered ");
            }
            else {
                System.out.println("Complaint Can't Be Registered, Plz Try Again Later");
            }


        } catch (SQLException E) {
            System.out.println(E.getMessage());
        }
        finally{
            if(ps!=null)
            {
                try {
                    ps.close();
                    ps=null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
