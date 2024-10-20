package Users.Admin;

import Exceptions.InvalidInputException;
import Exceptions.InvalidPasswordException;
import Exceptions.InvalidProfessorIdException;
import Exceptions.UserNotFoundException;
import Users.User;

import java.sql.*;
import java.util.Scanner;

public class Admin implements User {

    private final Connection con;
    private final Scanner sc;

    public Admin(Connection con, Scanner sc) {
        this.con = con;
        this.sc = sc;
    }



    public void getShow() {
        int choice=0;
        while (choice!=4) {
            System.out.println("****Available Operation****");
            System.out.println("1. Manage Student Records");
            System.out.println("2. Manage Course Catalogue");
            System.out.println("3. Assign Professor");
            System.out.println("4. Quit/Exit");
            System.out.println("Select the operation you want to perform ");
          choice= sc.nextInt();
            switch (choice) {
                case 1:
                    ManageStudentRecords.manageStudentRecords(con, sc);
                    break;
                case 2:
                    ManageCourseCatalog.manageCourseCatalog(con,sc);
                    break;
                case 3:
                    int count=3;
                    while (count>0) {
                        try {
                            AssignProfessor.assignProfessor(con, sc);
                            break;
                        } catch (InvalidProfessorIdException e) {
                            System.out.println(e.getMessage());
                        }
                        count--;
                    }
                    break;
                case 4:
                    System.out.println("ThankYou for Using Admin\n exiting...");
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

    @Override
    public boolean login() {
        System.out.println("Enter Admin Username: ");
        String username = sc.next();
        String query = "SELECT password FROM admin WHERE username=?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Enter Password: ");
                sc.nextLine();
                String password = sc.nextLine();
                String pass = rs.getString("password");
                if (password.equals(pass)) {
                    return true;
                } else {
                    throw new InvalidPasswordException("Password Doesn't Match");
                }
            } else {
                throw new UserNotFoundException("User Not Found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }
        return false;
    }
}
