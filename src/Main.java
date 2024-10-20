import DatabasePackage.DbConnection;
import Exceptions.*;

import Users.Admin.Admin;
import Users.Professor.Professor;
import Users.Student.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    //Declaring Scanner and Connection
    private static Connection con;
    private static Scanner sc;

    public static void main(String[] args) {
        //Connecting With Database
        try {
            con = DbConnection.connectdb();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        sc = new Scanner(System.in);
        int choice;
        //Welcome Statement
        System.out.println("*************Welcome*************");
        do {
            //Select Who you want to log in as
            System.out.println("Login as:\n1.Student\n2.Professor\n3.Admin\n4.Exit");

            choice = sc.nextInt();
            switch (choice) {
                //if Student is Selected (Completed)
                case 1:
                    Student s1 = new Student(con, sc);
                    //number of tries of login
                    int count = 3;
                    while (count > 0) {
                        try {
                            boolean chk = s1.login();
                            if (chk) {
                                System.out.println("Logged in as Student");
                                break;
                            } else {
                                throw new InvalidPasswordException("Wrong Password or email! you have  " + (count - 1) + " tries left");
                            }
                        } catch (InvalidPasswordException | InvalidEmailException e) {
                            System.out.println(e.getMessage());
                        }
                        count--;
                    }
                    //if Password Was never Correct
                    if (count == 0) {
                        throw new InvalidPasswordException("Your Password or UserName is Incorrect,Try again later after Some time");
                    }
                    while (choice != 7) {
                        System.out.println("""
                                Which Operation will you like to perform:
                                1. ViewAvailableCourses
                                2. Register Courses
                                3. ViewSchedule
                                4. ViewAcademicDetails
                                5. Drop course
                                6. Submit Complaint
                                7. Exit""");

                        //updating choice
                        choice = sc.nextInt();
                        switch (choice) {
                            //view Courses
                            case 1:
                                s1.viewCourses();
                                break;
                            //Register Courses
                            case 2:
                                boolean isCorrect = false;
                                while (!isCorrect) {
                                    try {
                                        s1.registerCourse();
                                        isCorrect = true;
                                    } catch (CourseNotFoundException | InvalidSemesterException e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                                break;
                            //View Schedule
                            case 3:
                                s1.viewSchedule();
                                break;

                            //Academic Record
                            case 4:
                                s1.displayAcademicDetails();
                                break;

                            //Drop Courses
                            case 5:
                                s1.dropCourse();
                                break;

                            //Complaints
                            case 6:
                                s1.submitComplaints();
                                break;
                            case 7:
                                System.out.println("ThankYou For Using The System");
                                try {
                                    System.out.println("Exiting...");
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    System.out.println(e.getMessage());
                                }
                                break;
                        }
                    }
                    break;
                //if professor is Selected
                case 2:
                    Professor pr=new Professor(con,sc);
                    //number of tries of login
                    int count1 = 3;
                    while (count1 > 0) {
                        try {
                            boolean chk = pr.login();
                            if (chk) {
                                System.out.println("Logged in as Professor");
                                break;
                            } else {
                                throw new InvalidPasswordException("Wrong Password or email! you have  " + (count1 - 1) + " tries left");
                            }
                        } catch (InvalidPasswordException | InvalidEmailException e) {
                            System.out.println(e.getMessage());
                        }
                        count1--;
                    }
                    //if Password Was never Correct
                    if (count1 == 0) {
                        throw new InvalidPasswordException("Your Password or UserName is Incorrect,Try again later after Some time");
                    }
                    pr.getFunctions();
                    break;

                //if Admin is Selected
                case 3:
                    Admin admin = new Admin(con, sc);
                    int adminCount = 3;
                    boolean isLogged = false;
                    while (adminCount > 0) {
                        try {
                            isLogged = admin.login();
                            break;
                        } catch (InvalidPasswordException e) {
                            System.out.println(e.getMessage());
                        }
                        adminCount--;
                    }
                    if (isLogged)
                        admin.getShow();
                    else
                        throw new InvalidPasswordException("You have tried you password multiple times,try again later");
                    break;
                //if Exited
                case 4:
                    try {
                        System.out.println("ThankYou for using the System.\nExiting...");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                default:
                    throw new InvalidInputException("Invalid Choice");
            }
        } while (choice != 4);
        try {
            con.close();
            sc.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}