package Users.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DropCourse {
    Connection con;

    DropCourse(Connection con)
    {
        this.con=con;
    }

    public void dropCourse(String rollNo,String course_id)
    {
        String query= """
                DELETE FROM enrolledStudents
                WHERE course_id=? and student_id=?
               """;
        PreparedStatement ps = null;
        try {
             ps=con.prepareStatement(query);
            ps.setString(1,course_id);
            ps.setString(2,rollNo);
            int rowsAffected=ps.executeUpdate();
            System.out.println(rowsAffected>0?"Course Dropped Successfully":"Course Couldn't be dropped,please try again later");
        }
        catch (SQLException e)
        {
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
