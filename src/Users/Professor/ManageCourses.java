package Users.Professor;

import Exceptions.CourseNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

public class ManageCourses {
    private final Connection connection;
    private final Scanner scanner;
    private final String rollNo;

    ManageCourses(Connection connection, Scanner sc, String rollNo) {
        this.connection = connection;
        this.scanner = sc;
        this.rollNo = rollNo;
    }

    public void manageCourses() {
        while (true) {
            System.out.println("Option Available");
            System.out.println("1.Update Syllabus");
            System.out.println("2.Update Timing ");
            System.out.println("3.Update/Set Enrollment Limit");
            System.out.println("4.Update Credits");
            System.out.println("5.Exit/Quit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            boolean isValid=false;
            while (!isValid) {
                try {
                    switch (choice) {
                        case 1:
                            updateSyllabus(rollNo);
                            break;
                        case 2:
                            updateTimings(rollNo);
                            break;
                        case 3:
                            updateEnrollmentLimits(rollNo);
                            break;
                        case 4:
                            updateCredits(rollNo);
                            break;
                        case 5:
                            return;
                        default:

                            System.out.println("Choice is invalid");
                            break;
                    }
                    isValid=true;
                } catch (CourseNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void updateEnrollmentLimits(String rollNo) {
        Professor prof = new Professor(connection, scanner);
        Set<String> course_ids;
        course_ids = prof.viewProfessorCourses(rollNo);
        System.out.println("Enter the course_id:");
        String course_id = scanner.nextLine();
        course_id = course_id.toUpperCase();
        if (course_ids.contains(course_id)) {
            System.out.println("Enter the new limit:");
            int new_limit = scanner.nextInt();
            try {
                String query = "UPDATE assignProfessors SET enrollmentLimit=? WHERE course_id=?";
                PreparedStatement prepstmt = connection.prepareStatement(query);
                prepstmt.setInt(1, new_limit);
                prepstmt.setString(2, course_id);

                int affectedRows = prepstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Enrollment limit updated successfully");
                } else {
                    System.out.println("Enrollment limit update failed");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            throw new CourseNotFoundException("OOPS!! Unable to update .You are not assigned that course ID");
        }

    }


    private void updateCredits(String rollNo) {
        Professor prof = new Professor(connection, scanner);
        Set<String> course_ids;
        course_ids = prof.viewProfessorCourses(rollNo);
        System.out.println("Enter the course_id:");
        String course_id = scanner.nextLine();
        course_id = course_id.toUpperCase();
        if (course_ids.contains(course_id)) {
            System.out.println("Enter the new credits:");
            int new_credits = scanner.nextInt();
            scanner.nextLine(); // Consume the leftover newline
            try {
                String query = "UPDATE courses SET credits=? WHERE course_id=?";
                PreparedStatement prepstmt = connection.prepareStatement(query);
                prepstmt.setInt(1, new_credits);
                prepstmt.setString(2, course_id);

                int affectedRows = prepstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Credits updated successfully");
                } else {
                    System.out.println("Credits update failed");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            throw new CourseNotFoundException("OOPS!! Unable to update .You are not assigned that course ID");
        }
    }


    private void updateTimings(String rollNo) {
        Professor prof = new Professor(connection, scanner);
        Set<String> course_ids;
        course_ids = prof.viewProfessorCourses(rollNo);
        System.out.println("Enter the course_id:");
        String course_id = scanner.nextLine();
        course_id = course_id.toUpperCase();
        String day,branch;
        int sem;
        if (course_ids.contains(course_id)) {
            System.out.println("Enter Branch of Course_id You want to add:");
            branch=scanner.next();
            System.out.println("Enter Semester of Course:");
            sem=scanner.nextInt();
            System.out.println("Enter Day of Course(First Letter Capital):");
            day=scanner.next();
            System.out.println("Enter the start time in format HH:MM");
            String new_st = scanner.next();
            System.out.println("Enter the new end time in format HH:MM");
            String new_et = scanner.next();
            String retrieveQuery="SELECT * FROM schedule WHERE c_id=?";
            String query = "UPDATE schedule SET start_time = ?, end_time = ? WHERE c_id = ?";

            try {
                PreparedStatement ps= connection.prepareStatement(retrieveQuery);
                ps.setString(1,course_id);
                ResultSet rs=ps.executeQuery();
                if(!rs.next())
                    query="INSERT into schedule(c_id,semNo,branch,prof_id,day,start_time,end_time)VALUES(?,?,?,?,?,?,?)";
                PreparedStatement prepstmt = connection.prepareStatement(query);
                if(rs.next()) {
                    prepstmt.setString(1, new_st);
                    prepstmt.setString(2, new_et);
                    prepstmt.setString(3, course_id);
                }
                else
                {
                    prepstmt.setString(1,course_id);
                    prepstmt.setInt(2,sem);
                    prepstmt.setString(3,branch);
                    prepstmt.setString(4,rollNo);
                    prepstmt.setString(5,day);
                    prepstmt.setString(6,new_st);
                    prepstmt.setString(7,new_et);
                }

                int affectedRows = prepstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Timings updated successfully");
                } else {
                    System.out.println("Timings update failed");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            throw new CourseNotFoundException("OOPS!! Unable to update .You are not assigned that course ID");
        }

    }

    private void updateSyllabus(String rollNo) {
        Professor prof = new Professor(connection, scanner);
        Set<String> course_ids;
        course_ids = prof.viewProfessorCourses(rollNo);
        System.out.println("Enter the course_id:");
        String course_id = scanner.nextLine();
        course_id = course_id.toUpperCase();
        if (course_ids.contains(course_id)) {
            System.out.println("Enter the new syllabus:");
            String new_syllabus = scanner.nextLine();

            try {
                String query = "UPDATE syllabus SET syllabus=? WHERE c_id=?";
                PreparedStatement prepstmt = connection.prepareStatement(query);
                prepstmt.setString(1, new_syllabus);
                prepstmt.setString(2, course_id);

                int affectedRows = prepstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Syllabus updated successfully");
                } else {
                    System.out.println("Syllabus update failed");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            throw new CourseNotFoundException("Invalid course");

        }

    }
}
