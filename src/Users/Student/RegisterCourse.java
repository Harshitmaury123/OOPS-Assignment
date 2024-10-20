package Users.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterCourse {
    final Connection con;

    public RegisterCourse(Connection con) {
        this.con = con;
    }

    public void registerCourse(String rollNo,String course_id)
    {
        String query="INSERT INTO enrolledStudents(student_id,course_id) VALUES(?,?)";
        PreparedStatement ps=null;
        try{
            ps=con.prepareStatement(query);
            ps.setString(1,rollNo);
            ps.setString(2,course_id);
           int rowsAffected=ps.executeUpdate();
            System.out.println(rowsAffected>0?"Course Succesfully Registered":"Course couldn't be registered try again later");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
