package com.student;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewStudentsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String studentIdParam = request.getParameter("student_id");

        if (studentIdParam == null || studentIdParam.trim().isEmpty()) {
            out.println("<p style='color:red;'>Student ID is required.</p>");
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/school_db", "postgres", "7668")) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM students WHERE student_id = ?");
            ps.setInt(1, Integer.parseInt(studentIdParam));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("<h2>Student Details</h2>");
                out.println("<table border='1'>");
                out.println("<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Email</th><th>Phone</th><th>DOB</th><th>Gender</th><th>Address</th><th>Course</th><th>Age</th></tr>");
                out.println("<tr>");
                out.println("<td>" + rs.getInt("student_id") + "</td>");
                out.println("<td>" + rs.getString("first_name") + "</td>");
                out.println("<td>" + rs.getString("last_name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td>" + rs.getString("phone") + "</td>");
                out.println("<td>" + rs.getDate("dob") + "</td>");
                out.println("<td>" + rs.getString("gender") + "</td>");
                out.println("<td>" + rs.getString("address") + "</td>");
                out.println("<td>" + rs.getString("course") + "</td>");
                out.println("<td>" + rs.getInt("age") + "</td>");
                out.println("</tr>");
                out.println("</table>");
            } else {
                out.println("<p style='color:red;'>No student found with ID " + studentIdParam + ".</p>");
            }

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        out.println("<p><a href='student.html'>Back to Student Portal</a></p>" + "<p><a href='Index.html'>Home</a></p>");
    }
}
