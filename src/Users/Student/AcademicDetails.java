package Users.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AcademicDetails {
    Connection con;

    public AcademicDetails(Connection con) {
        this.con = con;
    }

    private double calculateSGPA(int semester, String rollNo) {
        double sgpa = 0;
        Map<String, Integer> map = new HashMap<>(Map.of(
                "AA", 10,
                "AB", 9,
                "BB", 8,
                "BC", 7,
                "CC", 6,
                "CD", 5,
                "DD", 4,
                "FF", 0
        ));

        String creditsQuery = "SELECT SUM(courses.credits) AS total_credits " +
                "FROM enrolledStudents " +
                "INNER JOIN courses ON enrolledStudents.course_id = courses.course_id " +
                "WHERE courses.semesterNo = ? AND enrolledStudents.student_id = ?";


        String query = "SELECT grade, credits_obtained FROM results WHERE semNo = ? AND sid = ?";
        int totalCredits = 0;

        try {
            // Fetch grades and credits obtained
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, semester);
            ps.setString(2, rollNo);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                String gd = resultSet.getString("grade");
                int creditsObtained = resultSet.getInt("credits_obtained");
                sgpa += map.get(gd) * creditsObtained;
            }

            // Fetch total credits
            ps = con.prepareStatement(creditsQuery);
            ps.setInt(1, semester);
            ps.setString(2, rollNo);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                totalCredits = resultSet.getInt("total_credits");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (totalCredits > 0)
            return sgpa / totalCredits;
        return sgpa;
    }


    private double calculateCgpa(int semester, String rollNo) {
        ArrayList<Double> sgpa = new ArrayList<>();
        for (int i = 1; i <= semester; i++) {
            sgpa.add(calculateSGPA(i, rollNo));
        }
        double cgpa = 0;
        for (double sg : sgpa) {
            cgpa += sg;
        }
        if (!sgpa.isEmpty()) {
            return cgpa / sgpa.size();
        }
        return cgpa;
    }

    public void displayAcademicDetails(String rollNo, int semester) {


        String query = "SELECT cid, total_credits, credits_obtained, grade " +
                "FROM results WHERE sid = ? AND semNo = ?";
        PreparedStatement ps = null;
        try {
            for (int i = semester; i >= 1; i--) {
                System.out.println("Semester: " + i);
                System.out.println("+----------+-------+-----------------+---------------+");
                System.out.println("| COURSE ID| Grade | Credits Obtained| Total Credits|");
                System.out.println("+----------+-------+-----------------+---------------+");
                ps = con.prepareStatement(query);
                ps.setString(1, rollNo);
                ps.setInt(2, i);
                ResultSet resultSet = ps.executeQuery();

                double sgpa = calculateSGPA(i, rollNo);  // Calculate SGPA once per semester
                double cgpa = calculateCgpa(i, rollNo);  // Calculate CGPA once per semester

                while (resultSet.next()) {
                    String cid = resultSet.getString("cid");
                    String grade = resultSet.getString("grade");
                    int totalCredits = resultSet.getInt("total_credits");
                    int creditsObtained = resultSet.getInt("credits_obtained");
                    System.out.printf("|%-10s|%-7s|%-16d|%-14d|\n", cid, grade, creditsObtained, totalCredits);
                }
                System.out.println("+----------+-------+----------------+--------------+");
                System.out.println("SGPA: " + sgpa + "\nCGPA: " + cgpa);
            }
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

}
