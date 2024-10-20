package Users.Professor;

import Exceptions.InvalidEmailException;
import Users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Professor implements User {
    private final Connection connection;
    private final Scanner scanner;

    public String getRollNo() {
        return rollNo;
    }

    private String rollNo;

    public Professor(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void getFunctions() {
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1.Manage Courses ");
            System.out.println("2.View Enrolled Student");
            System.out.println("3.Exit/Quit");
            System.out.print("Enter your Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    ManageCourses mc = new ManageCourses(connection, scanner,rollNo);
                    mc.manageCourses();
                    break;
                case 2:
                    GetEnrolledStudents ges=new GetEnrolledStudents(connection,scanner);
                    ges.getEnrolledStudents(rollNo);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Entered choice is invalid");
                    break;
            }
        }
    }


    public boolean login() throws InvalidEmailException {
        System.out.print("Enter Your Email: ");
        String email = scanner.next();
        //checks whether given email is valid
        boolean isValid = (email.substring(4).equals("@coed.svnit.ac.in"));


        //Extracting Roll no Via Email
        this.rollNo = email.split("@")[0];
        this.rollNo = this.rollNo.toUpperCase();
        System.out.println(this.rollNo);


        //if email is valid
        if (isValid) {
            //taking password input
            System.out.print("Enter Your Password: ");
            String pass = scanner.next();
            //if email is valid find in database
            String query = String.format("SELECT password FROM professor WHERE email='%s'", email);
            PreparedStatement ps = null;
            try {
                //query to extract passwords
                ps = connection.prepareStatement(query);  //compiling query
                ResultSet rs = ps.executeQuery();   //executing query
                if (rs.next()) {
                    String passW;
                    passW = rs.getString("password");
                    return passW.equals(pass);
                }
                // closing th instances created
                rs.close();
                ps.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                        ps = null;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //else throw InvalidEmailException
        else {
            throw new InvalidEmailException("Invalid Email");
        }
        return false;
    }
    public Set<String> viewProfessorCourses(String rollNo) {
        //query to find courses based on semester and branch
        Set<String> course_ids = new HashSet<>();
        String query = """
                       SELECT assignProfessors.course_id as course_id,courses.course_name as course_name FROM Courses
                       INNER JOIN assignProfessors ON Courses.course_id=assignProfessors.course_id
                       WHERE prof_id=?""";
        try {
            //Compiling Query
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, rollNo);
            System.out.println(rollNo);

            //executing query
            ResultSet results = ps.executeQuery();

// Print table header
            System.out.println("+----------+---------------------------+");
            System.out.println("| COURSE ID| COURSE NAME               |");
            System.out.println("+----------+---------------------------+");

// Print course details
            while (results.next()) {
                String courseId = results.getString("course_id");
                course_ids.add(courseId);
                String courseName = results.getString("course_name");
                // Format and print the course information
                System.out.printf("|%-10s|%-27s|\n", courseId, courseName);
            }
//Print table footer
            System.out.println("+----------+---------------------------+");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return course_ids;
    }


}
