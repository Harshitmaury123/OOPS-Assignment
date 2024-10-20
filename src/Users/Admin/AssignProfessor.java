package Users.Admin;

import Exceptions.InvalidProfessorIdException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssignProfessor {

    public static void assignProfessor(Connection connection,Scanner scanner) {
        System.out.println("Enter the professor ID:");
        String prof_id = scanner.next();
        prof_id = prof_id.toUpperCase();
        boolean isCorrect=false;


        if((prof_id.length()==4) && prof_id.charAt(0)=='P' && (prof_id.charAt(1)>='0' && prof_id.charAt(1)<='9')
           &&  (prof_id.charAt(2)>='0' && prof_id.charAt(2)<='9') && (prof_id.charAt(3)>='0' && prof_id.charAt(3)<='9') ){
            isCorrect=true;
        }
        else{
            isCorrect=false;
        }

        //check if professor id is valid
        if (isCorrect) {
            System.out.println("Enter the course ID:");
            String course_id = scanner.next();
            course_id = course_id.toUpperCase();

            try {
                // SQL query to assign a professor to a course
                String query = "INSERT INTO assignProfessors (course_id, prof_id) VALUES (?, ?)";
                PreparedStatement prepstmt = connection.prepareStatement(query);
                prepstmt.setString(1, course_id);
                prepstmt.setString(2, prof_id);
                int affectedRows = prepstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Professor assigned successfully.");
                } else {
                    System.out.println("Professor assignment failed.");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        else
        {
            throw new InvalidProfessorIdException("Invalid Professor id");
        }
    }
}
