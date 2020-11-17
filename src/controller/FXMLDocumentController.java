package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import model.Staff;

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
    private Label searchLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;
    
    @FXML
    private Button searchAdvancedButton;
    
    @FXML
    private Button showDetailsButton;
    
    @FXML
    private TableView<Staff> staffTable;
    @FXML
    private TableColumn<Staff, Integer> staffId;
    @FXML
    private TableColumn<Staff, String> staffLastname;
    @FXML
    private TableColumn<Staff, String> staffCourse;
    @FXML
    private TableColumn<Staff, String> staffAssignments;

    // the observable list of students that is used to insert data into the table
    private ObservableList<Staff> staffData;
    
    // add the proper data to the observable list to be rendered in the table
    public void setTableData(List<Staff> staffList) {

        // initialize the staffData variable
        staffData = FXCollections.observableArrayList();

        // add the staff objects to an observable list object for use with the GUI table
        staffList.forEach(s -> {
            staffData.add(s);
        });

        // set the the table items to the data in staffData; refresh the table
        staffTable.setItems(staffData);
        staffTable.refresh();
    }
     
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
    void search(ActionEvent event) {
        System.out.println("clicked");

        // getting the name from input box        
        String lastname = searchTextField.getText();

        // calling a db read operation, readByName
        List<Staff> allStaff = readByName(lastname);

        if (allStaff == null || allStaff.isEmpty()) {

            // show an alert to inform user 
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog Box");// line 2
            alert.setHeaderText("ERROR");// line 3
            alert.setContentText("No staff member matches that name");// line 4
            alert.showAndWait(); // line 5
        } else {

            // setting table data
            setTableData(allStaff);
        }
    }
    
    @FXML
    void searchAdvanced(ActionEvent event) {
        System.out.println("clicked");

        // getting the name from input box        
        String lastname = searchTextField.getText();

        // calling a db read operation, readByNameAdvanced
        List<Staff> allStaff = readByNameAdvanced(lastname);

        // setting table data
        //setTableData(allStaff);
        // add an alert
        if (allStaff == null || allStaff.isEmpty()) {

            // show an alert to inform user 
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog Box");// line 2
            alert.setHeaderText("ERROR");// line 3
            alert.setContentText("No staff member matches that query");// line 4
            alert.showAndWait(); // line 5
        } else {
            // setting table data
            setTableData(allStaff);
        }
    }
    
    @FXML
    void showDetails(ActionEvent event) throws IOException {
        System.out.println("clicked");

        
        // pass currently selected model
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        
        // fxml loader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetailedModelView.fxml"));

        // load the ui elements
        Parent detailedModelView = loader.load();

        // load the scene
        Scene tableViewScene = new Scene(detailedModelView);

        //access the detailedControlled and call a method
        DetailedModelViewController detailedControlled = loader.getController();


        detailedControlled.initData(selectedStaff);

        // create a new state
        Stage stage = new Stage();
        stage.setScene(tableViewScene);
        stage.show();
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
//needed to add for search feature to work
    public List<Staff> readByName(String lastname) {
        Query query = manager.createNamedQuery("Staff.findByLastname");

        // setting query parameter
        query.setParameter("lastname", lastname);

        // execute query
        List<Staff> allStaff = query.getResultList();
        for (Staff staff : allStaff) {
            System.out.println(staff.getId() + " " + staff.getLastname()+ " " + staff.getCourse() + " " + staff.getAssignments());
        }

        return allStaff;
    }
    
    public List<Staff> readByNameAdvanced(String lastname) {
        Query query = manager.createNamedQuery("Staff.findByNameAdvanced");

        // setting query parameter
        query.setParameter("lastname", lastname);

        // execute query
        List<Staff> allStaff = query.getResultList();
        for (Staff staff : allStaff) {
            System.out.println(staff.getId() + " " + staff.getLastname()+ " " + staff.getCourse() + " " + staff.getAssignments());
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
        
        staffId.setCellValueFactory(new PropertyValueFactory<>("ID"));
        staffLastname.setCellValueFactory(new PropertyValueFactory<>("Name"));
        staffCourse.setCellValueFactory(new PropertyValueFactory<>("Course"));
        staffAssignments.setCellValueFactory(new PropertyValueFactory<>("Assignments"));
        //enable row selection
        // (SelectionMode.MULTIPLE);
        staffTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    } 
  
    
}
