package com.student;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone console program to demonstrate:
 * - Creating multiple Student objects
 * - Storing them in an ArrayList
 * - Displaying them using the display() method
 * - Polymorphism via Person references
 */
public class StudentApp {

    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();

        // Create sample students
        Student s1 = new Student(1, "Aishwarya", "Shinde",
                "aishwarya@example.com", "9876543210",
                "2000-01-15", "Female", "Mumbai", "Science", 24);

        Student s2 = new Student(2, "Vaibhav", "Shinde",
                "vaibhav@example.com", "9123456780",
                "1992-07-15", "Male", "Thane", "Commerce", 33);

        Student s3 = new Student(3, "Kriyansh", "Shinde",
                "kriyansh@example.com", "9988776655",
                "2020-02-20", "Male", "Mumbai", "Arts", 6);

        // Add to List<Person> (Polymorphism)
        people.add(s1);
        people.add(s2);
        people.add(s3);

        // Display all students – polymorphic call to display()
        System.out.println("=== Student List from StudentApp (console) ===");
        for (Person person : people) {
            System.out.println(person.getRole() + " -> " + person.display());
        }
    }
}