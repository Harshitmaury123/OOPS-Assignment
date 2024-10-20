package Users.Student;

import Exceptions.CourseNotFoundException;
import Exceptions.InvalidEmailException;
import Exceptions.InvalidSemesterException;
import Users.User;

import java.sql.*;
import java.util.Scanner;
import java.util.Set;

public class Student implements User {
    private final Connection con;
    private final Scanner sc;
    private String rollNo;
    private String branch;
    private int semester;

    public Student(Connection con, Scanner sc) {
        this.con = con;
        this.sc = sc;
    }

    //function to view availableCourses
    public void viewCourses() throws InvalidSemesterException {
        ViewCourses vc = new ViewCourses(con);
        System.out.println("""
                What Would you Like to View
                1. Registered Courses
                2. Available Courses""");
        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                //view Registered Courses
                vc.viewRegisteredCourses(rollNo);
                break;
            case 2:
                boolean semsterFound=false;
                while(!semsterFound) {
                    try {
                        System.out.println("Enter The Semester you want view courses of(1-8):");
                        semester = sc.nextInt();
                        //taking branch input
                        if (semester >= 1 && semester <= 8) {
                            semsterFound = true;
                        } else {
                            throw new InvalidSemesterException("No such Semester Exists");
                        }
                        //semester successfully Found
                    } catch (InvalidSemesterException e) {
                        System.out.println(e.getMessage());
                    }
                }
                    System.out.println("Enter the Branch you want to see courses of: ");
                    sc.nextLine();
                    branch = sc.nextLine();
                    vc.viewAvailableCourses(semester, branch);


        }
    }

    //Function to Submit Complaints
    public void submitComplaints() {
        RegisterComplaint complaint = new RegisterComplaint(con);
        System.out.println("Enter The complaint you would like to register: ");
        sc.nextLine();
        String msg = sc.nextLine();
        complaint.giveComplaints(rollNo, msg);
    }

    public boolean login() throws InvalidEmailException {
        System.out.print("Enter Your Email: ");
        String email = sc.next();
        //checks whether given email is valid
        boolean isValid = (email.substring(8).equals("@coed.svnit.ac.in"));


        //Extracting Roll no Via Email
        this.rollNo = email.split("@")[0];
        this.rollNo = this.rollNo.toUpperCase();

        //Extracting branch via Email
        this.branch = email.substring(3, 5);
        this.branch=this.branch.toUpperCase();

        //if email is valid
        if (isValid) {
            //taking password input
            System.out.print("Enter Your Password: ");
            String pass = sc.next();
            //if email is valid find in database
            String query = String.format("SELECT password FROM student WHERE email='%s'", email);
            PreparedStatement ps=null;
            try {
                //query to extract passwords

                 ps = con.prepareStatement(query);  //compiling query
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
        //else throw InvalidEmailException
        else {
            throw new InvalidEmailException("Invalid Email");
        }
        return false;
    }

    //viewSchedule
    public void viewSchedule() {
        ViewSchedule vs = new ViewSchedule(con);
        System.out.println("Enter Semester:");
        semester = sc.nextInt();
        vs.viewSchedule(semester, branch);
    }

    public void dropCourse() {
        ViewCourses viewCourses=new ViewCourses(con);
        viewCourses.viewRegisteredCourses(rollNo);
        System.out.println("Enter Course_id you want to delete from above courses:");
        String course_id=sc.next();
        DropCourse dc=new DropCourse(con);
        dc.dropCourse(rollNo,course_id);
    }
    public void displayAcademicDetails()
    {
        System.out.println("Enter Semester: ");
        semester=sc.nextInt();
        AcademicDetails a=new AcademicDetails(con);
        a.displayAcademicDetails(rollNo,semester);
    }
    public void registerCourse()
    {
        Set<String>course_ids;
        System.out.println("Enter Semster:");
        semester=sc.nextInt();
        if(semester>=1 && semester<=8)
        {
            ViewCourses vc=new ViewCourses(con);
            course_ids=vc.viewAvailableCourses(semester,branch);
            RegisterCourse rc=new RegisterCourse(con);
            System.out.println("Enter Course_ID you want to register:");
            String course_id=sc.next();
            course_id=course_id.toUpperCase();
            if(course_ids.contains(course_id)) {
                rc.registerCourse(rollNo, course_id);
            }
            else
                throw new CourseNotFoundException("Course Not Found");
        }
        else
            throw new InvalidSemesterException("Semester Doesn't Exist");

    }
}
