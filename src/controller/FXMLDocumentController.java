/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import model.Staff;

/**
 *
 * @author smg6340
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private Button createButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button readButton;

    @FXML
    private Button updateButton;
    
    @FXML
    private Button readByIDButton;

    @FXML
    private Button readByNameCourseButton;
    
    @FXML
    void createEntry(ActionEvent event) {
        Scanner input = new Scanner(System.in);
        
        // read input from command line
        System.out.println("Enter ID:");
        int id = input.nextInt();
        
        System.out.println("Enter Course:");
        String course = input.next();
        
        System.out.println("Enter Last Name:");
        String lastname = input.next();
        
        System.out.println("Enter Assignments:");
        String assignments = input.next();
        
        // create a staff instance
        Staff staff = new Staff();
        
        // set properties
        staff.setId(id);
        staff.setCourse(course);
        staff.setLastname(lastname);
        staff.setAssignments(assignments);
        // save this staff to database by calling Create operation        
        create(staff);
    }

    @FXML
    void deleteEntry(ActionEvent event) {
        Scanner input = new Scanner(System.in);
        
         // read input from command line
        System.out.println("Enter ID:");
        int id = input.nextInt();
        
        Staff s = readById(id);
        System.out.println("we are deleting this staff member: "+ s.toString());
        delete(s);
    }

    @FXML
    void readEntry(ActionEvent event) {
        readAll();
    }
    
    @FXML
    void readById(ActionEvent event) {
        Scanner input = new Scanner(System.in);
        
        // read input from command line
        System.out.println("Enter ID:");
        int id = input.nextInt();
        
        Staff s = readById(id);
        System.out.println(s.toString());
    }

    @FXML
    void readByNameCourse(ActionEvent event) {
                // name and course
        
        Scanner input = new Scanner(System.in);
        
        // read input from command line
        
        System.out.println("Enter Course:");
        String course = input.next();
        
        System.out.println("Enter Last Name:");
        String lastname = input.next();
        
        // create a staff instance      
        List<Staff> allStaff =  readByNameAndCourse(course, lastname);
    }
    
    @FXML
    void updateEntry(ActionEvent event) {
        Scanner input = new Scanner(System.in);
        
        // read input from command line
        System.out.println("Enter ID:");
        int id = input.nextInt();
        
        System.out.println("Enter Course:");
        String course = input.next();
        
        System.out.println("Enter Last Name:");
        String lastname = input.next();
        
        System.out.println("Enter Assignments:");
        String assignments = input.next();
        
        // create a staff instance
        Staff staff = new Staff();
        
        // set properties
        staff.setId(id);
        staff.setCourse(course);
        staff.setLastname(lastname);
        staff.setAssignments(assignments);
        // save this staff to database by calling Create operation        
        update(staff);
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
                
        Query query = manager.createNamedQuery("Staff.findAll");
        List<Staff> data = query.getResultList();
        
        for (Staff s : data) {            
            System.out.println(s.getId() + " " + s.getLastname()+ " " + s.getCourse() + " " + s.getAssignments());         
        }           
    }
    /*
    Implementing CRUD operations
 */
    
    // Create operation
    public void create(Staff staff) {
        try {
            // begin transaction
            manager.getTransaction().begin();
            
            // sanity check
            if (staff.getId() != null) {
                
                // create student
                manager.persist(staff);
                
                // end transaction
                manager.getTransaction().commit();
                
                System.out.println(staff.toString() + " is created");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    // Read Operations
    public List<Staff> readAll(){
        Query query = manager.createNamedQuery("Staff.findAll");
        List<Staff> allStaff = query.getResultList();

        for (Staff s : allStaff) {
            System.out.println(s.getId() + " " + s.getLastname()+ " " + s.getCourse() + " " + s.getAssignments());
        }
        
        return allStaff;
    }
//needed to add to ensure delete button would work as intended
//also used as my first 'custom-named query'
    public Staff readById(int id){
        Query query = manager.createNamedQuery("Staff.findById");
        
        // setting query parameter
        query.setParameter("id", id);
        
        // execute query
        Staff staff = (Staff) query.getSingleResult();
        if (staff != null) {
            System.out.println(staff.getId() + " " + staff.getLastname()+ " " + staff.getCourse() + " " + staff.getAssignments());
        }
        
        return staff;
    }
  public List<Staff> readByNameAndCourse(String course, String lastname){
        Query query = manager.createNamedQuery("Staff.findByNameAndCourse");
        
        // setting query parameter
        query.setParameter("course", course);
        query.setParameter("lastname", lastname);
        
        
        // execute query
        List<Staff> allStaff =  query.getResultList();
        for (Staff staff: allStaff) {
            System.out.println(staff.getId() + " " + staff.getCourse() + " " + staff.getLastname());
        }
        
        return allStaff;
    } 
    // Update operation
    public void update(Staff model) {
        try {

            Staff existingStaff = manager.find(Staff.class, model.getId());

            if (existingStaff != null) {
                // begin transaction
                manager.getTransaction().begin();
                
                // update all atttributes
                existingStaff.setCourse(model.getCourse());
                existingStaff.setLastname(model.getLastname());
                existingStaff.setAssignments(model.getAssignments());
                // end transaction
                manager.getTransaction().commit();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Delete operation
    public void delete(Staff staff) {
        try {
            Staff existingStaff = manager.find(Staff.class, staff.getId());

            // sanity check
            if (existingStaff != null) {
                
                // begin transaction
                manager.getTransaction().begin();
                
                //remove student
                manager.remove(existingStaff);
                
                // end transaction
                manager.getTransaction().commit();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Database manager
    EntityManager manager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        // loading data from database
        //database reference: "IntroJavaFXPU"
        manager = (EntityManager) Persistence.createEntityManagerFactory("SabrinaGooptuFXMLPU").createEntityManager();
    } 
  
    
}
