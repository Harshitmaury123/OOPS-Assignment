package Users.Admin;

import Exceptions.CourseNotFoundException;
import Exceptions.InvalidInputException;
import Exceptions.MarksNegativeException;
import Exceptions.UserNotFoundException;
import Users.Student.AcademicDetails;
import Users.Student.ViewCourses;

import java.sql.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ManageStudentRecords {
    private static String rollNo;

    static void manageStudentRecords(Connection con, Scanner sc) {
        int choice = 0;
        while (choice != 5) {
            System.out.println("****Available Operation****");
            System.out.println("1. Get Personal Details"); //done
            System.out.println("2. Change Details"); //done
            System.out.println("3. Assign Grades");
            System.out.println("4. Handle Complaints"); //done
            System.out.println("5. Quit/Exit");  //done
            System.out.println("Select the operation you want to perform ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    getDetails(con, sc);
                    break;
                case 2:
                    changeDetails(con, sc);
                    break;
                case 3:
                    assignGrades(con, sc);
                    break;
                case 4:
                    handleComplaints(con, sc);
                    break;
                case 5:
                    System.out.println("Exiting from StudentRecords\nexiting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new InvalidInputException("Invalid Choice");
            }
        }
    }

    //function to retrieve details of student
    private static void getDetails(Connection con, Scanner sc) {
        sc.nextLine();
        System.out.println("Enter Roll Number of Student: ");
        String rollNum = sc.nextLine();

        String query = "SELECT * FROM student WHERE sid=?";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, rollNum);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String sid = rs.getString("sid");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phoneNo = rs.getString("phone_number");
                String degreeType = rs.getString("degreeType");
                String dob = rs.getString("dob");
                String branch = rs.getString("branch");
                int sem = rs.getInt("semester");
                String date = rs.getString("enrollmentDate");

                System.out.println("+----------+--------+---------------------------+-------------+------------+--------+----------+---------------------+------------+");
                System.out.println("|  SID     | NAME   |  EMAIL                    |PHONE NUMBER | DEGREE TYPE | BRANCH | SEMESTER | ENROLLMENTDATE      |  DOB       |");
                System.out.println("+----------+--------+---------------------------+-------------+------------+--------+----------+---------------------+------------+");
                System.out.printf("|%-10s|%-8s|%-27s|%-13s|%-12s|%-8s|%-10s|%-21s|%-12s", sid, name, email, phoneNo, degreeType, branch, sem, date, dob);
                System.out.println();
                System.out.println("+----------+--------+---------------------------+-------------+------------+--------+----------+---------------------+------------+");


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //function to change details of student
    private static void changeDetails(Connection con, Scanner sc) {
        // Taking roll number as input
        System.out.println("Enter Roll Number ");
        rollNo = sc.next();
        rollNo = rollNo.toUpperCase();
        // Selecting details
        System.out.println("Select Detail you want to change ");
        System.out.println("1. DOB\n2. Password\n3. Phone Number");
        int choice = sc.nextInt();
        String attribute;
        Date newDob;
        String oldP;
        String newNumber; // Initialize to avoid potential null references

        attribute = switch (choice) {
            case 1 -> "dob";
            case 2 -> "password";
            case 3 -> "phone_number"; // Change to match your database field

            default -> throw new InvalidInputException("Invalid Choice");
        };

        // Checking various cases of choice
        try {
            String query = "UPDATE student SET " + attribute + " = ? WHERE sid = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(2, rollNo);

            switch (choice) {
                case 1:
                    System.out.println("Enter new DOB (yyyy-mm-dd)");
                    sc.nextLine(); // Consume leftover newline
                    newDob = Date.valueOf(sc.nextLine());
                    ps.setDate(1, newDob);
                    break;
                case 2:
                    System.out.println("Enter your old password:");
                    sc.nextLine(); // Consume leftover newline
                    oldP = sc.nextLine();

                    String pwQuery = "SELECT password FROM student WHERE sid = ?";
                    try  {
                        PreparedStatement ps1 = con.prepareStatement(pwQuery);
                        ps1.setString(1, rollNo);
                        ResultSet rs = ps1.executeQuery();

                        if (rs.next()) {
                            if (rs.getString("password").equals(oldP)) {
                                System.out.println("Enter new password:");
                                String newp = sc.nextLine();
                                ps.setString(1, newp);
                            } else {
                                System.out.println("Wrong Password");
                                return; // Exit if the password is wrong
                            }
                        } else {
                            System.out.println("Data not Found");
                            return; // Exit if no data found
                        }
                    }catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Enter new Phone Number: ");
                    newNumber = sc.next();
                    ps.setString(1, newNumber);
                    break;
                default:
                    throw new InvalidInputException("Invalid choice");
            }

            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Updated Successful" : "Can't be Updated");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void assignGrades(Connection con, Scanner sc) {
        System.out.println("Enter Roll No: ");
        rollNo = sc.next();
        int semester = 0;
        String query = "SELECT semester FROM student WHERE sid=?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                semester = rs.getInt("semester");
            if (semester == 0)
                throw new UserNotFoundException("User Not Found");
            else {
                Set<String> registered_courses = new HashSet<>();

                //calculating total credits
                int total_credits=0;
                int credits_obtained=0;
                //display registered courses
                ViewCourses vc = new ViewCourses(con);
                registered_courses = vc.viewRegisteredCourses(rollNo);

                //display academic details
                AcademicDetails ad = new AcademicDetails(con);
                ad.displayAcademicDetails(rollNo, semester);

                System.out.println("Enter Course ID: ");
                String course_id = sc.next();
                course_id=course_id.toUpperCase();
                if (registered_courses.contains(course_id)) {
                    System.out.println("Enter Your Marks For given courseID:");
                    int marks = sc.nextInt();
                    String grade = "";
                    if (marks > 90 && marks <= 100) {
                        grade = "AA";
                    } else if (marks > 80 && marks <= 90) {
                        grade = "AB";
                    } else if (marks > 70 && marks <= 80) {
                        grade = "BB";
                    } else if (marks > 60 && marks <= 70) {
                        grade = "BC";
                    } else if (marks > 50 && marks <= 60) {
                        grade = "CC";
                    } else if (marks > 40 && marks <= 50) {
                        grade = "CD";
                    } else if (marks > 30 && marks <= 40) {
                        grade = "DD";
                    }else if(marks<=30 && marks>=0 )  {
                        grade = "FF";
                    }
                    else
                    {
                        throw new MarksNegativeException("Marks Must be Between 0-100");
                    }
                    String gradeQuery="SELECT credits FROM courses WHERE course_id=?";
                    PreparedStatement gradePs=con.prepareStatement(gradeQuery);
                    gradePs.setString(1,course_id);
                    ResultSet resultSet=gradePs.executeQuery();

                    if(resultSet.next())
                    {
                        total_credits=resultSet.getInt("credits");
                    }
                    if(!grade.equals("FF"))
                        credits_obtained=total_credits;

                    String updateQuery="""
                            INSERT INTO results(semNo,sid,cid,credits_obtained,total_credits,grade)
                            VALUES(?,?,?,?,?,?)""";
                    PreparedStatement us=con.prepareStatement(updateQuery);
                    us.setInt(1,semester);
                    us.setString(2,rollNo);
                    us.setString(3,course_id);
                    us.setInt(4,credits_obtained);
                    us.setInt(5,total_credits);
                    us.setString(6,grade);
                    int rowsAffected =us.executeUpdate();
                    if(rowsAffected>0)
                    {
                        System.out.println("Grade Assigned Successfully");
                    }
                    else
                    {
                        System.out.println("Some Error Occurred Try Again Later");
                    }
                } else {
                    throw new CourseNotFoundException("Course Not found");
                }


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleComplaints(Connection con, Scanner sc) {
        System.out.println("Enter Roll No: ");
        rollNo = sc.next();
        rollNo = rollNo.toUpperCase(); // Convert roll number to uppercase for consistency

        // Query to get complaints for the specific student
        String query = "SELECT description, status FROM complaint WHERE sid=?";

        try {
            // Prepare the statement to fetch complaints
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();

            // Check if the student has any complaints
            if (!rs.isBeforeFirst()) {  // If there are no results
                System.out.println("No complaints found for this Roll Number.");
            } else {
                // Iterate through the result set and process each complaint
                while (rs.next()) {
                    String description = rs.getString("description");
                    String status = rs.getString("status");

                    // Display complaint details
                    System.out.println("Complaint Description: " + description);
                    System.out.println("Status: " + status);

                    // If the complaint is pending, prompt to resolve it
                    if (status.equalsIgnoreCase("pending")) {
                        System.out.println("Do you want to resolve this complaint? (yes/no)");
                        String resolve = sc.next();

                        if (resolve.equalsIgnoreCase("yes")) {
                            // Update the status to 'Resolved'
                            String updateQuery = "UPDATE complaint SET status = 'Resolved' WHERE sid = ? AND description = ?";
                            PreparedStatement psUpdate = con.prepareStatement(updateQuery);
                            psUpdate.setString(1, rollNo);
                            psUpdate.setString(2, description);

                            int rowsAffected = psUpdate.executeUpdate(); // Execute the update
                            if (rowsAffected > 0) {
                                System.out.println("Complaint resolved successfully.");
                            } else {
                                System.out.println("Error resolving complaint. Please try again.");
                            }
                            psUpdate.close();  // Close the update statement
                        }
                    } else {
                        // Inform the user if the complaint is already resolved
                        System.out.println("This complaint is already resolved.");
                    }
                }
            }
            ps.close();  // Close the prepared statement
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.out.println("Error handling complaints: " + e.getMessage());
        }

    }

}


