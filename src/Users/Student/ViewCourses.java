package Users.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ViewCourses {
    private final Connection con;

    public ViewCourses(Connection con) {
        this.con = con;
    }

    //function to view courses on basis of semester and branch
    public Set<String> viewAvailableCourses(int semester, String branch) {
        //query to find courses based on semester and branch
        Set<String> course_ids = new HashSet<>();
        String query = "SELECT availableCourses.course_id as course_id,availableCourses.course_name as course_name,credits FROM Courses INNER JOIN availableCourses ON Courses.course_id=availableCourses.course_id WHERE semesterNo=? and branch=?";
        try {
            //Compiling Query
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, semester);
            ps.setString(2, branch);

            //executing query
            ResultSet results = ps.executeQuery();

// Print table header
            System.out.println("+----------+---------------------------+--------+");
            System.out.println("| COURSE ID| COURSE NAME               | CREDITS|");
            System.out.println("+----------+---------------------------+--------+");

// Print course details
            while (results.next()) {
                String courseId = results.getString("course_id");
                course_ids.add(courseId);
                String courseName = results.getString("course_name");
                int credits = results.getInt("credits");

                // Format and print the course information
                System.out.printf("|%-10s|%-27s|%-8d|\n", courseId, courseName, credits);
            }
//Print table footer
            System.out.println("+----------+---------------------------+--------+");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return course_ids;
    }

    public Set<String> viewRegisteredCourses(String rollNo) {
        Set<String>registered_courses = new HashSet<>();
        String query1 = """
        SELECT enrolledStudents.course_id, course_name, credits
        FROM enrolledStudents
        INNER JOIN Courses ON Courses.course_id = enrolledStudents.course_id
        WHERE student_id = ?""";

        PreparedStatement ps=null;
        try {

            //Query to extract registeredCourses


            //Compiling Query
             ps = con.prepareStatement(query1);

            //setting Values
            ps.setString(1, rollNo);
// Print table header
            System.out.println("+----------+---------------------------+--------+");
            System.out.println("| COURSE ID| COURSE NAME               | CREDITS|");
            System.out.println("+----------+---------------------------+--------+");

// Print course details
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String courseId = results.getString("course_id");
                String courseName = results.getString("course_name");
                int credits = results.getInt("credits");
                registered_courses.add(courseId);
                // Format and print the course information
                System.out.printf("|%-10s|%-27s|%-8d|\n", courseId, courseName, credits);
            }
//Print table footer
            System.out.println("+----------+---------------------------+--------+");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally{
            if(ps!=null)
            {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return registered_courses;
    }
}
