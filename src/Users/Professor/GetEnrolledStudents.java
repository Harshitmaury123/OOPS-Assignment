package Users.Professor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GetEnrolledStudents {
    private final Connection connection;
    private final Scanner scanner;
    private String rollNo;

    public GetEnrolledStudents(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;

    }

    public void getEnrolledStudents(String rollNo) {
        Professor pr=new Professor(connection,scanner);
        pr.viewProfessorCourses(rollNo);
        System.out.println("Enter the course_id:");
        String course_id = scanner.nextLine();
        try {
            String query = "SELECT s.name, e.student_id " +
                    "FROM enrolledStudents e " +
                    "INNER JOIN student s ON e.student_id = s.sid " +
                    "WHERE e.course_id = ?";
            PreparedStatement prepstmt = connection.prepareStatement(query);
            prepstmt.setString(1, course_id);
            ResultSet rs = prepstmt.executeQuery();
            while (rs.next()) {
                String sid = rs.getString("student_id");
                String name = rs.getString("name");
                System.out.println();
                System.out.println("Name: " + name);
                System.out.println("ID: " + sid);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
