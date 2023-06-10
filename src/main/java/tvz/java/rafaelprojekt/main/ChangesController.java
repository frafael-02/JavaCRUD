package tvz.java.rafaelprojekt.main;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import tvz.java.rafaelprojekt.entity.ChangedEntity;

import java.util.ArrayList;
import java.util.List;


public class ChangesController {


    @FXML
    private TextArea textArea;

    @FXML
    private ChoiceBox<String> choiceBox;

    private List<String> changes;

    public void initialize()
    {   choiceBox.getItems().add("ADMIN");
        choiceBox.getItems().add("GUEST");

        ChangedEntity.serializeChanges(MainApplication.changesList);
        changes= ChangedEntity.readChanges();

       if(changes!=null)
       {
           for (String s : changes)
            textArea.appendText(s + "\n");
       }


    }

    public void filterClicked()
    {
        if(choiceBox.getSelectionModel().getSelectedItem().equals("ADMIN"))
        {
            List<String> filteredList = new ArrayList<>(changes);
            filteredList= filteredList.stream().filter(string -> string.contains("by ADMIN")).toList();
            textArea.clear();
            for(String s:filteredList)
                textArea.appendText(s + "\n");

        }
        if(choiceBox.getSelectionModel().getSelectedItem().equals("GUEST"))
        {
            List<String> filteredList = new ArrayList<>(changes);
            filteredList= filteredList.stream().filter(string -> string.contains("by GUEST")).toList();
            textArea.clear();
            for(String s:filteredList)
                textArea.appendText(s + "\n");
        }

    }

    public void resetClicked()
    {
        choiceBox.getSelectionModel().clearSelection();
        textArea.clear();
        for(String s:changes)
            textArea.appendText(s + "\n");
    }
}
