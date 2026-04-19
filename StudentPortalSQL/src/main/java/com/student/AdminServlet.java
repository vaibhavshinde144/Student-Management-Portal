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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AdminServlet: handles filtering, pagination, update/delete, and CSV export of students.
 * Demonstrates Inheritance (extends HttpServlet) and Polymorphism (overrides doGet/doPost).
 */
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String URL  = "jdbc:postgresql://localhost:5432/school_db";
    private static final String USER = "postgres";
    private static final String PASS = "7668";
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("export".equalsIgnoreCase(action)) {
            exportCsv(response);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int currentPage = 1;
        try {
            if (request.getParameter("page") != null && !request.getParameter("page").trim().isEmpty()) {
                currentPage = Integer.parseInt(request.getParameter("page").trim());
            }
        } catch (Exception e) {
            currentPage = 1;
        }

        int offset = (currentPage - 1) * PAGE_SIZE;

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (notBlank(request.getParameter("student_id"))) {
                whereSql.append(" AND student_id = ?");
                params.add(Integer.parseInt(request.getParameter("student_id").trim()));
            }
            if (notBlank(request.getParameter("first_name"))) {
                whereSql.append(" AND first_name ILIKE ?");
                params.add("%" + request.getParameter("first_name").trim() + "%");
            }
            if (notBlank(request.getParameter("last_name"))) {
                whereSql.append(" AND last_name ILIKE ?");
                params.add("%" + request.getParameter("last_name").trim() + "%");
            }
            if (notBlank(request.getParameter("email"))) {
                whereSql.append(" AND email ILIKE ?");
                params.add("%" + request.getParameter("email").trim() + "%");
            }
            if (notBlank(request.getParameter("phone"))) {
                whereSql.append(" AND phone = ?");
                params.add(request.getParameter("phone").trim());
            }
            if (notBlank(request.getParameter("from_dob"))) {
                whereSql.append(" AND dob >= ?");
                params.add(Date.valueOf(request.getParameter("from_dob").trim()));
            }
            if (notBlank(request.getParameter("to_dob"))) {
                whereSql.append(" AND dob <= ?");
                params.add(Date.valueOf(request.getParameter("to_dob").trim()));
            }
            if (notBlank(request.getParameter("gender"))) {
                whereSql.append(" AND gender = ?");
                params.add(request.getParameter("gender").trim());
            }
            if (notBlank(request.getParameter("course"))) {
                whereSql.append(" AND course = ?");
                params.add(request.getParameter("course").trim());
            }
            if (request.getParameter("age18") != null) {
                whereSql.append(" AND age > 18");
            }

            String countSql = "SELECT COUNT(*) FROM students " + whereSql.toString();
            PreparedStatement countPs = con.prepareStatement(countSql);
            for (int i = 0; i < params.size(); i++) {
                countPs.setObject(i + 1, params.get(i));
            }

            ResultSet countRs = countPs.executeQuery();
            int totalRecords = 0;
            if (countRs.next()) {
                totalRecords = countRs.getInt(1);
            }

            int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;

            String dataSql = "SELECT * FROM students "
                    + whereSql.toString()
                    + " ORDER BY student_id LIMIT ? OFFSET ?";

            PreparedStatement ps = con.prepareStatement(dataSql);
            int index = 1;
            for (Object param : params) {
                ps.setObject(index++, param);
            }
            ps.setInt(index++, PAGE_SIZE);
            ps.setInt(index, offset);

            ResultSet rs = ps.executeQuery();

            StringBuilder rowsHtml = new StringBuilder();
            boolean found = false;

            while (rs.next()) {
                found = true;
                int id = rs.getInt("student_id");

                rowsHtml.append("<tr class='data-row' id='row-").append(id).append("'>");
                rowsHtml.append("<td class='readonly-cell'>").append(id).append("</td>");
                rowsHtml.append("<td><input class='edit-input' type='text' name='first_name' value='")
                        .append(escapeHtml(rs.getString("first_name"))).append("'></td>");
                rowsHtml.append("<td><input class='edit-input' type='text' name='last_name' value='")
                        .append(escapeHtml(rs.getString("last_name"))).append("'></td>");
                rowsHtml.append("<td><input class='edit-input' type='text' name='email' value='")
                        .append(escapeHtml(rs.getString("email"))).append("'></td>");
                rowsHtml.append("<td><input class='edit-input' type='text' name='phone' value='")
                        .append(escapeHtml(rs.getString("phone"))).append("'></td>");
                rowsHtml.append("<td><input class='edit-input' type='date' name='dob' value='")
                        .append(rs.getDate("dob")).append("'></td>");

                rowsHtml.append("<td><select class='edit-select' name='gender'>");
                rowsHtml.append(option("Male", rs.getString("gender")));
                rowsHtml.append(option("Female", rs.getString("gender")));
                rowsHtml.append(option("Other", rs.getString("gender")));
                rowsHtml.append("</select></td>");

                rowsHtml.append("<td><input class='edit-input' type='text' name='address' value='")
                        .append(escapeHtml(rs.getString("address"))).append("'></td>");

                rowsHtml.append("<td><select class='edit-select' name='course'>");
                rowsHtml.append(option("Science", rs.getString("course")));
                rowsHtml.append(option("Commerce", rs.getString("course")));
                rowsHtml.append(option("Arts", rs.getString("course")));
                rowsHtml.append(option("Vocational Courses", rs.getString("course")));
                rowsHtml.append("</select></td>");

                rowsHtml.append("<td class='readonly-cell'>").append(rs.getInt("age")).append("</td>");
                rowsHtml.append("<td><button type='button' class='action-btn update-btn' onclick='updateStudent(")
                        .append(id).append(")'>Update</button></td>");
                rowsHtml.append("<td><button type='button' class='action-btn delete-btn' onclick='deleteStudent(")
                        .append(id).append(")'>Delete</button></td>");
                rowsHtml.append("</tr>");
            }

            if (!found) {
                rowsHtml.append("<tr><td colspan='12' style='color:red;'>No student records found.</td></tr>");
            }

            String json = "{"
                    + "\"rowsHtml\":\"" + escapeJson(rowsHtml.toString()) + "\","
                    + "\"totalRecords\":" + totalRecords + ","
                    + "\"currentPage\":" + currentPage + ","
                    + "\"totalPages\":" + totalPages
                    + "}";

            out.print(json);

        } catch (Exception e) {
            out.print("{\"rowsHtml\":\"<tr><td colspan='12' style='color:red;'>Error loading data.</td></tr>\","
                    + "\"totalRecords\":0,\"currentPage\":1,\"totalPages\":1}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            if ("update".equalsIgnoreCase(action)) {

                LocalDate dob = Date.valueOf(request.getParameter("dob").trim()).toLocalDate();
                LocalDate today = LocalDate.now();
                int age = Period.between(dob, today).getYears();

                PreparedStatement ps = con.prepareStatement(
                        "UPDATE students SET first_name=?, last_name=?, email=?, phone=?, dob=?, "
                                + "gender=?, address=?, course=?, age=? WHERE student_id=?");

                ps.setString(1, request.getParameter("first_name").trim());
                ps.setString(2, request.getParameter("last_name").trim());
                ps.setString(3, request.getParameter("email").trim());
                ps.setString(4, request.getParameter("phone").trim());
                ps.setDate(5, Date.valueOf(dob));
                ps.setString(6, request.getParameter("gender").trim());
                ps.setString(7, request.getParameter("address").trim());
                ps.setString(8, request.getParameter("course").trim());
                ps.setInt(9, age);
                ps.setInt(10, Integer.parseInt(request.getParameter("student_id").trim()));

                int updated = ps.executeUpdate();
                out.print("<span style='color:blue;'>" + updated + " record updated successfully.</span>");
            } else if ("delete".equalsIgnoreCase(action)) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE student_id=?");
                ps.setInt(1, Integer.parseInt(request.getParameter("student_id").trim()));

                int deleted = ps.executeUpdate();
                out.print("<span style='color:red;'>" + deleted + " record deleted successfully.</span>");
            } else {
                out.print("<span style='color:red;'>Invalid action.</span>");
            }

        } catch (Exception e) {
            out.print("<span style='color:red;'>Error: " + escapeHtml(e.getMessage()) + "</span>");
        }
    }

    private void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=students_export.csv");

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PrintWriter out = response.getWriter();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM students ORDER BY student_id");
             ResultSet rs = ps.executeQuery()) {

            out.println("Student ID,First Name,Last Name,Email,Phone,DOB,Gender,Address,Course,Age");

            while (rs.next()) {
                out.println(
                        rs.getInt("student_id") + ","
                                + csv(rs.getString("first_name")) + ","
                                + csv(rs.getString("last_name")) + ","
                                + csv(rs.getString("email")) + ","
                                + csv(rs.getString("phone")) + ","
                                + rs.getDate("dob") + ","
                                + csv(rs.getString("gender")) + ","
                                + csv(rs.getString("address")) + ","
                                + csv(rs.getString("course")) + ","
                                + rs.getInt("age")
                );
            }
        } catch (Exception e) {
            response.getWriter().println("Error exporting file");
        }
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String option(String value, String selectedValue) {
        if (value.equals(selectedValue)) {
            return "<option value='" + value + "' selected>" + value + "</option>";
        }
        return "<option value='" + value + "'>" + value + "</option>";
    }

    private String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\r", "")
                  .replace("\n", "");
    }

    private String csv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}