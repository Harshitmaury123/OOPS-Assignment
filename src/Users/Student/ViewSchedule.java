package Users.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewSchedule {
    // Database connection object
    Connection con;

    // Constructor to initialize the connection
    ViewSchedule(Connection con) {
        this.con = con;
    }

    // Method to view the schedule based on semester and branch
    public void viewSchedule(int semester, String branch) {
        System.out.println("Branch: " + branch); // Display the branch name

        // SQL query to get course details along with professor info and schedule timing
        String query = "SELECT course_name, professor.name AS professor_name, day, start_time, end_time "
                + "FROM Schedule "
                + "INNER JOIN Courses ON Schedule.c_id = Courses.course_id "
                + "INNER JOIN professor ON Schedule.prof_id = professor.pid "
                + "WHERE semNo = ? AND branch = ?";

        PreparedStatement ps = null;

        try {
            // Prepare the SQL query
            ps = con.prepareStatement(query);
            ps.setInt(1, semester);   // Set the semester number
            ps.setString(2, branch);  // Set the branch name
            ResultSet resultSet = ps.executeQuery();  // Execute the query

            // Display the header of the schedule table with correct format
            System.out.println("+-----------------------------------+-------------------+---------+-------------+------------+");
            System.out.println("| Course Name                       | Professor Name    | Day     | Start Time  | End Time   |");
            System.out.println("+-----------------------------------+-------------------+---------+-------------+------------+");

            // Loop through the result set and print each course's schedule
            while (resultSet.next()) {
                String courseName = resultSet.getString("course_name");
                String professorName = resultSet.getString("professor_name");
                String day = resultSet.getString("day");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");

                // Format the output for each row according to the given format
                System.out.printf("| %-33s | %-17s | %-7s | %-11s | %-10s |\n",
                        courseName, professorName, day, startTime, endTime);
            }

            // Display the footer of the table
            System.out.println("+-----------------------------------+-------------------+---------+-------------+------------+");

        } catch (SQLException e) {
            // Handle SQL exceptions (e.g., wrong query, connection issues)
            System.out.println(e.getMessage());
        } finally {
            // Close the PreparedStatement to release database resources
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
}
