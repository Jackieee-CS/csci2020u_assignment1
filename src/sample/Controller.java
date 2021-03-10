package sample;

import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Controller extends WordCounter{

    @FXML private TableView<TestFile> tableView;
    @FXML private TableColumn<TestFile, String> fileColumn;
    @FXML private TableColumn<TestFile, String> actualClassColumn;
    @FXML private TableColumn<TestFile, Double> spamProbColumn;
    @FXML private Label precision;
    @FXML private Label accuracy;

    @FXML private TextField precisionNum;
    @FXML private TextField accuracyNum;

    @FXML

    double prob = 0;

    public void initialize() {


        System.out.println("App is running...");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        Stage primaryStage = new Stage();
        File pathDir2 = directoryChooser.showDialog(primaryStage);

        String pathDir = "./train/";
        //File pathDir2 = new File("./test");
        File dataDir = new File("./train/");
        File outFile = new File("output.txt");

        WordCounter wordCounter = new WordCounter();
        System.out.println("Hello");
        try{
            wordCounter.parseDir(pathDir);
            wordCounter.parseFileProb(pathDir2);

            ArrayList<TestFile> testFile = wordCounter.returnTestFileList();
            double fileCount = wordCounter.returnFileAmount();

            /*
            for(int i = 0;i < testFile.size(); i++){

                System.out.println("The File Name is " + testFile.get(i).getFilename() + " and the probability is " +
                        testFile.get(i).getSpamProbability() + " the directory it was found in was " + testFile.get(i).getActualClass());
            }
            */
            //calculate and print the accuracy and precision
            double numTrueNegative = 0; // File was in "ham" and prob < threshold
            double numFalsePos = 0; // File was in "ham" and prob > threshold
            double numTruePos = 0; // File was in "spam" and prob > threshold
            double threshold = 0.5; // EDIT THIS BY % OF SPAM THRESHOLD (BASE IS 50% PROB = 50% SPAM)
            for (TestFile entry: testFile) {
                String actualClass = entry.getActualClass();
                prob = entry.getSpamProbability();
                if(actualClass.equals("ham") && prob < threshold){
                    numTrueNegative++;
                } if (actualClass.equals("spam") && prob > threshold){
                    numTruePos++;
                } if(actualClass.equals("ham") && prob > threshold){
                    numFalsePos++;
                }
            }

            double accuracy = (numTruePos+numTrueNegative) / fileCount;
            double precision = numTruePos/(numFalsePos + numTruePos);

            System.out.println("Accuracy was " + accuracy);
            System.out.println("Precision was " + precision);

            accuracyNum.setText(String.valueOf(accuracy));
            precisionNum.setText(String.valueOf(precision));

            //ObservableList<TestFile> Files = FXCollections.observableArrayList(testFile);
            fileColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
            fileColumn.setMinWidth(400);
            spamProbColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));
            spamProbColumn.setMinWidth(400);
            actualClassColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));
            actualClassColumn.setMinWidth(400);
            tableView.getItems().setAll(testFile);

        }catch(FileNotFoundException e){
            System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }



    }
}