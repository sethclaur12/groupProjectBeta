/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vet_patient_manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author sethc
 */
public class FXMLDocumentController implements Initializable {
    @FXML private TableView <Patient> tableView;
    @FXML private Label patientID;
    @FXML private Label patientName;
    @FXML private Label ownerID;
    @FXML private TextField searchTxt;
    private ObservableList<Patient> data = FXCollections.observableArrayList(); 
    private List <Patient> list = new ArrayList<>();
    
    Label pIDLabel = new Label("Patient ID: ");
    Label pNameLabel = new Label("Patient Name: ");
    Label owIDLabel = new Label("Owner Id: ");
    Label pID = new Label("Patient ID: ");
    Label pName = new Label("Patient Name: ");
    Label owID = new Label("Owner Id: ");
    
    //initializing the Create New PopUp
    Stage createStg = new Stage();
    VBox createV = new VBox();
    HBox buttonsBox;
    Label msgCreate = new Label("Input the required information to create an entry for a new patient");
    GridPane createGrid = new GridPane();
    TextField patientIDNew = new TextField();
    TextField patientNameNew = new TextField();
    TextField ownerIDNew = new TextField();
    Button createSubmitBtn = new Button("Create");
    Button closeCreateBtn = new Button("Cancel");
    Scene scCreate= new Scene(createV);
    
    //initializing the Edit PopUp
    Stage editStg = new Stage();
    VBox editV = new VBox();
    HBox editButtonsBox;
    Label msgEdit = new Label("Edit the Information of an existing patient");
    GridPane editGrid = new GridPane();
    TextField patientIDEdit = new TextField();
    TextField patientNameEdit = new TextField();
    TextField ownerIDEdit = new TextField();
    Button editSubmitBtn = new Button("Save");
    Button closeEditBtn = new Button("Cancel");
    Scene scEdit= new Scene(editV);
    
    //name too to long pop up
    Stage nameStg = new Stage();
    VBox nameV = new VBox();
    Label nameTooLong = new Label("Patient Name can not exceed 30 charactrers. Owner and patient ID should be greater than 0");
    Button closeName = new Button("Ok");
    Scene scName = new Scene(nameV);
    
    

    public FXMLDocumentController() {
        this.buttonsBox = new HBox(createSubmitBtn,closeCreateBtn);
        this.editButtonsBox = new HBox(editSubmitBtn, closeEditBtn);
    }
    
    @FXML
    private void search(){
        int search = Integer.parseInt(searchTxt.getText());
        List <Patient> results = new ArrayList<>();
        ObservableList<Patient> resultsObs = FXCollections.observableArrayList(); 
        
        for (Patient p : list) {
            if (search == p.getPatientID()) {
                results.add(p);
            }
        }
        for (Patient x : results) {
            resultsObs.add(new Patient(x));
        }
        
        searchTxt.setText("Enter Patient ID to search");
        tableView.getItems().clear();
        tableView.setItems(resultsObs);
    }
    
    @FXML
    private void refresh(){
        try {
            readFromFile();
            tableView.getItems().clear();
            populate(list);//arraylist list populating the observable list data
            tableView.setItems(data);    
        } 
        catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
        }
    }   
    
    //command to open the createStg to create a new entry
    @FXML
    private void createNew() {
        createStg.show();
    }
    
    //delete a row in the table view
    @FXML
    private void deleteRow() throws IOException {
        Patient selectedItem = tableView.getSelectionModel().getSelectedItem();
        tableView.getItems().remove(selectedItem); 
        //save changes to file
        save();
    }
    
    @FXML
    private void editRow(){
        Patient selectedItem = tableView.getSelectionModel().getSelectedItem();
        patientIDEdit.setText(Integer.toString(selectedItem.getPatientID()));
        patientNameEdit.setText(selectedItem.getPatientName());
        ownerIDEdit.setText(Integer.toString(selectedItem.getOwnerID()));
        
        editStg.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            readFromFile();
        } 
        catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
        }
        populate(list);//arraylist list populating the observable list data
        tableView.setItems(data);
        
        //vbox specifications
        createV.setPadding(new Insets(10,10,10,10));
        createV.setSpacing(10);
        editV.setPadding(new Insets(10,10,10,10));
        editV.setSpacing(10);
        
        //elements of the Create new Pop UP
        createV.getChildren().addAll(msgCreate, createGrid, buttonsBox);
        createStg.setScene(scCreate);
        //elements of the Create new Pop UP
        editV.getChildren().addAll(msgEdit, editGrid, editButtonsBox);
        editStg.setScene(scEdit);
        
        //elements of the name too to long pop up
        nameV.getChildren().addAll(nameTooLong, closeName);
        nameStg.setScene(scName);
        
        //gridPane of the create new pop Up
        createGrid.getChildren().addAll(pID, pName, owID, patientIDNew, patientNameNew, ownerIDNew);
        createGrid.setConstraints(pID, 0, 0); // column=0 row=0
        createGrid.setConstraints(pName, 0, 1); // column=0 row=1
        createGrid.setConstraints(owID, 0, 2); // column=0 row=2
       
        createGrid.setConstraints(patientIDNew, 1, 0); // column=1 row=0
        createGrid.setConstraints(patientNameNew, 1, 1); // column=1 row=1
        createGrid.setConstraints(ownerIDNew, 1, 2); // column=1 row=2
        
        //gridPane of the edit pop Up
        editGrid.getChildren().addAll(pIDLabel, pNameLabel, owIDLabel, patientIDEdit, patientNameEdit, ownerIDEdit);
        editGrid.setConstraints(pIDLabel, 0, 0); // column=0 row=0
        editGrid.setConstraints(pNameLabel, 0, 1); // column=0 row=1
        editGrid.setConstraints(owIDLabel, 0, 2); // column=0 row=2
       
        editGrid.setConstraints(patientIDEdit, 1, 0); // column=1 row=0
        editGrid.setConstraints(patientNameEdit, 1, 1); // column=1 row=1
        editGrid.setConstraints(ownerIDEdit, 1, 2); // column=1 row=2
        
        //closing the Create New PopUp
        closeCreateBtn.setOnAction((ActionEvent event) ->{
            patientIDNew.setText("");
            patientNameNew.setText("");
            ownerIDNew.setText("");
            createStg.close();
        });
        
        //closing the Edit PopUp
        closeEditBtn.setOnAction((ActionEvent event) ->{
            patientIDEdit.setText("");
            patientNameEdit.setText("");
            ownerIDEdit.setText("");
            editStg.close();
        });
        
        //closing the name too to long pop up
        closeName.setOnAction((ActionEvent event) ->{
            nameStg.close();
        });

        createSubmitBtn.setOnAction((ActionEvent event) -> {
            try {
                int pId = Integer.parseInt(patientIDNew.getText());
                String name = patientNameNew.getText();
                int oId = Integer.parseInt(ownerIDNew.getText());

                if (name.length() > Patient.getNAME_SIZE()) {
                    throw new InvalidPatientInfoException();
                }
                else if (pId <= 0){
                    throw new InvalidPatientInfoException();
                }
                else if (oId <= 0) {
                    throw new InvalidPatientInfoException();
                }
                else {
                    tableView.getItems().add(new Patient(pId, name, oId));

                    patientIDNew.setText("");
                    patientNameNew.setText("");
                    ownerIDNew.setText("");

                    //refresh table
                    tableView.refresh();
                    //save changes to file
                    save();
                    createStg.close();    
                }    
            }
            catch (InvalidPatientInfoException ex) {
                System.out.println(ex);
                nameStg.show();   
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } 
        });
        
        editSubmitBtn.setOnAction((ActionEvent event) -> {
            try {
                int pId = Integer.parseInt(patientIDEdit.getText());
                String name = patientNameEdit.getText();
                int oId = Integer.parseInt(ownerIDEdit.getText());

                if (name.length() > Patient.getNAME_SIZE()) {
                    throw new InvalidPatientInfoException();
                }
                else if (pId <= 0){
                    throw new InvalidPatientInfoException();
                }
                else if (oId <= 0) {
                    throw new InvalidPatientInfoException();
                }
                else {
                    Patient selectedItem = tableView.getSelectionModel().getSelectedItem();
                    selectedItem.setOwnerID(oId);
                    selectedItem.setPatientID(pId);
                    selectedItem.setPatientName(name);

                    //refresh table
                    tableView.refresh();
                    //table to file
                    save();
                    editStg.close();    
                }    
            }
            catch (InvalidPatientInfoException ex) {
                System.out.println(ex);
                nameStg.show();   
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } 
        });
        
        //displaying info to the right with a mouse left click
        tableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                int index = tableView.getSelectionModel().getSelectedIndex();
                Patient selected = tableView.getItems().get(index);
                
                patientID.setText(Integer.toString(selected.getPatientID()));
                patientName.setText(selected.getPatientName());
                ownerID.setText(Integer.toString(selected.getOwnerID()));
            }
        });
        
        //add a key listeners
        tableView.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) {
                int index = tableView.getSelectionModel().getSelectedIndex();
                Patient selected = tableView.getItems().get(index);
                
                patientID.setText(Integer.toString(selected.getPatientID()));
                patientName.setText(selected.getPatientName());
                ownerID.setText(Integer.toString(selected.getOwnerID()));
            }
        });
    }    
    
    private void populate(final List<Patient> patient) {
        for (Patient x: patient) {
            data.add(new Patient(x));
        }
    }
    
    private void readFromFile() throws FileNotFoundException {
        list.clear();
        File file = new File("text.txt");
        
        try (Scanner input = new Scanner(file)) {
            input.useDelimiter(",");
            
            int patientID;
            String patientName;
            int ownerID;
            
            
            //loop through all entries, one entry is a row
            while (input.hasNext()) {
                patientID = input.nextInt();
                patientName = input.next();
                ownerID = input.nextInt();
                input.nextLine();
                
                list.add(new Patient(patientID, patientName, ownerID));
            }
        }
    }
    
    public void save() throws IOException{
        list = tableView.getItems();
        new FileWriter("text.txt", false).close();
        FileWriter f = new FileWriter("text.txt", true);
        PrintWriter pw = new PrintWriter(f); 
        
        for (Patient p: list) {
            pw.print(p.getPatientID() + ",");
            pw.print(p.getPatientName() + ",");
            pw.println(p.getOwnerID() + ",");
        }   
        
        pw.close();
        
    }
    
    
}
