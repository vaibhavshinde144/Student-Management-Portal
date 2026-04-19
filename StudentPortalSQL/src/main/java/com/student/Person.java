package com.student;

/**
 * Abstract base class to demonstrate Abstraction and Inheritance.
 * Other domain classes like Student extend this class.
 */
public abstract class Person {

    // Encapsulated common fields
    private int personId;
    private String firstName;
    private String lastName;

    public Person(int personId, String firstName, String lastName) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and setters (Encapsulation)
    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Abstract behavior — subclasses must implement (Abstraction)
    public abstract String getRole();

    /**
     * Abstract display method.
     * Implemented differently by each subclass (Polymorphism).
     */
    public abstract String display();
}