package com.student;

/**
 * Student domain class.
 * Demonstrates:
 * - Inheritance (extends Person)
 * - Encapsulation (private fields + getters/setters)
 * - Polymorphism/Abstraction (implements abstract methods from Person)
 */
public class Student extends Person {

    private int studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String address;
    private String course;
    private int age;

    // Parameterized constructor
    public Student(int studentId,
                   String firstName,
                   String lastName,
                   String email,
                   String phone,
                   String dob,
                   String gender,
                   String address,
                   String course,
                   int age) {

        // Call abstract superclass constructor (Inheritance)
        super(studentId, firstName, lastName);

        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.course = course;
        this.age = age;
    }

    // Getters and setters (Encapsulation)

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
        setPersonId(studentId); // keep Person state in sync
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        super.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
        super.setLastName(lastName);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Abstraction + Polymorphism: implementation of abstract methods

    @Override
    public String getRole() {
        return "Student";
    }

    /**
     * display() method required by your assignment.
     * Returns a formatted String with all important data.
     */
    @Override
    public String display() {
        return "Student{"
                + "id=" + studentId
                + ", name='" + firstName + " " + lastName + '\''
                + ", email='" + email + '\''
                + ", phone='" + phone + '\''
                + ", dob='" + dob + '\''
                + ", gender='" + gender + '\''
                + ", address='" + address + '\''
                + ", course='" + course + '\''
                + ", age=" + age
                + '}';
    }
}