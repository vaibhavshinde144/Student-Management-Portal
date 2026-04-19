package com.student;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/school_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "7668";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        String fname   = request.getParameter("fname");
        String lname   = request.getParameter("lname");
        String email   = request.getParameter("email");
        String phone   = request.getParameter("phone");
        String dobStr  = request.getParameter("dob");
        String gender  = request.getParameter("gender");
        String address = request.getParameter("address");
        String course  = request.getParameter("course");

        fname   = (fname   != null) ? fname.trim()   : null;
        lname   = (lname   != null) ? lname.trim()   : null;
        email   = (email   != null) ? email.trim()   : null;
        phone   = (phone   != null) ? phone.trim()   : null;
        dobStr  = (dobStr  != null) ? dobStr.trim()  : null;
        gender  = (gender  != null) ? gender.trim()  : null;
        address = (address != null) ? address.trim() : null;
        course  = (course  != null) ? course.trim()  : null;

        System.out.println("fname = [" + fname   + "]");
        System.out.println("lname = [" + lname   + "]");
        System.out.println("email = [" + email   + "]");
        System.out.println("phone = [" + phone   + "]");
        System.out.println("dob   = [" + dobStr  + "]");
        System.out.println("gender= [" + gender  + "]");
        System.out.println("address=[" + address + "]");
        System.out.println("course= [" + course  + "]");

        try {

            if (fname == null || fname.isEmpty()) {
                out.println("<p style='color:red;'>First Name is required.</p>");
                return;
            }
            if (!fname.matches("^[A-Za-z]{1,20}$")) {
                out.println("<p style='color:red;'>Invalid First Name: only alphabets, max 20 chars.</p>");
                return;
            }

            if (lname == null || lname.isEmpty()) {
                out.println("<p style='color:red;'>Last Name is required.</p>");
                return;
            }
            if (!lname.matches("^[A-Za-z]{1,30}$")) {
                out.println("<p style='color:red;'>Invalid Last Name: only alphabets, max 30 chars.</p>");
                return;
            }

            if (email == null || email.isEmpty()) {
                out.println("<p style='color:red;'>Email is required.</p>");
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                out.println("<p style='color:red;'>Invalid Email format.</p>");
                return;
            }

            if (phone == null || phone.isEmpty()) {
                out.println("<p style='color:red;'>Phone Number is required.</p>");
                return;
            }
            if (!phone.matches("^[0-9]{10}$")) {
                out.println("<p style='color:red;'>Invalid Phone: must be 10 digits numeric.</p>");
                return;
            }

            if (dobStr == null || dobStr.isEmpty()) {
                out.println("<p style='color:red;'>DOB is required.</p>");
                return;
            }

            LocalDate dob;
            try {
                dob = LocalDate.parse(dobStr);
            } catch (DateTimeParseException e) {
                out.println("<p style='color:red;'>Invalid DOB format.</p>");
                return;
            }

            LocalDate today = LocalDate.now();
            if (!dob.isBefore(today)) {
                out.println("<p style='color:red;'>Invalid DOB: must be less than today's date.</p>");
                return;
            }

            int age = Period.between(dob, today).getYears();

            if (gender == null || gender.isEmpty()) {
                out.println("<p style='color:red;'>Gender is required.</p>");
                return;
            }

            if (address == null || address.isEmpty()) {
                out.println("<p style='color:red;'>Address is required.</p>");
                return;
            }

            if (course == null || course.isEmpty()) {
                out.println("<p style='color:red;'>Course selection is required.</p>");
                return;
            }

            Class.forName("org.postgresql.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                // Duplicate email check
                try (PreparedStatement checkEmail = conn.prepareStatement(
                        "SELECT student_id FROM students WHERE email = ?")) {
                    checkEmail.setString(1, email);
                    try (ResultSet rsEmail = checkEmail.executeQuery()) {
                        if (rsEmail.next()) {
                            out.println("<p style='color:red;'>Email Id is already registered.</p>");
                            return;
                        }
                    }
                }

                // Duplicate phone check
                try (PreparedStatement checkPhone = conn.prepareStatement(
                        "SELECT student_id FROM students WHERE phone = ?")) {
                    checkPhone.setString(1, phone);
                    try (ResultSet rsPhone = checkPhone.executeQuery()) {
                        if (rsPhone.next()) {
                            out.println("<p style='color:red;'>Mobile number is already registered.</p>");
                            return;
                        }
                    }
                }

                // ==== Use Student object (Encapsulation + OO integration) ====
                Student student = new Student(
                        0,                // studentId (0 -> DB DEFAULT)
                        fname,
                        lname,
                        email,
                        phone,
                        dob.toString(),
                        gender,
                        address,
                        course,
                        age
                );

                // Optional: log display() output for debugging
                System.out.println(student.display());

                String sql = "INSERT INTO students "
                        + "(student_id, first_name, last_name, email, phone, dob, gender, address, course, age) "
                        + "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "RETURNING student_id";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, student.getFirstName());
                    ps.setString(2, student.getLastName());
                    ps.setString(3, student.getEmail());
                    ps.setString(4, student.getPhone());
                    ps.setDate(5, Date.valueOf(dob));
                    ps.setString(6, student.getGender());
                    ps.setString(7, student.getAddress());
                    ps.setString(8, student.getCourse());
                    ps.setInt(9, student.getAge());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int studentId = rs.getInt("student_id");
                            out.println("<h3 style='color:green;'>Student registered successfully!</h3>");
                            out.println("<p>Student ID: <b>" + studentId + "</b></p>");
                            out.println("<p><a href='register.html'>Back to Registration</a></p>"
                                    + "<p><a href='Index.html'>Home</a></p>"
                                    + "<p><a href='login.html'>Admin/Teachers Portal</a></p>");
                        } else {
                            out.println("<p style='color:red;'>Registration failed.</p>");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        }
    }
}