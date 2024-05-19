package com.tritonkor.presentation.controller.tag;

import com.tritonkor.domain.dto.TagStoreDto;
import com.tritonkor.domain.dto.TagUpdateDto;
import com.tritonkor.domain.service.impl.TagService;
import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.presentation.viewmodel.TagViewModel;
import java.io.IOException;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateTagController {
    @Autowired
    private TagListController tagListController;

    @Autowired
    private TagService tagService;
    @FXML
    private TextField nameField;
    @FXML
    private Button saveButton;

    private TagViewModel tagViewModel;


    @FXML
    public void initialize() {
        tagViewModel = new TagViewModel(
                UUID.randomUUID(), "Title");

        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        nameField.textProperty().bindBidirectional(tagViewModel.nameProperty());
    }

    @FXML
    public void onSave() throws IOException {
        System.out.println("Saving Tag Data: " + tagViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tag Information");
        alert.setHeaderText("Tag Data Saved Successfully");
        alert.setContentText(tagViewModel.toString());
        alert.showAndWait();

        String name = tagViewModel.getName();

        try {
            tagService.findByName(name);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Назва вже використовується");
            return;
        } catch (EntityNotFoundException e) {
            TagStoreDto tagStoreDto = new TagStoreDto(
                    name
            );

            tagService.create(tagStoreDto);

            int currentPage = tagListController.pagination.getCurrentPageIndex();
            int totalPages = tagListController.getTotalPages();

            tagListController.pagination.setPageCount(totalPages);
            tagListController.updateTestList(currentPage);
        }
    }

    @FXML
    public void onUpdate() throws IOException {
        System.out.println("Updating Tag Data: " + tagViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tag Information");
        alert.setHeaderText("Tag Data Updating");
        alert.setContentText(tagViewModel.toString());
        alert.showAndWait();

        try {
            tagService.findById(tagViewModel.getId());

            String name = tagViewModel.getName();

            TagUpdateDto tagUpdateDto = new TagUpdateDto(
                    tagViewModel.getId(),
                    name
            );

            tagService.update(tagUpdateDto);
            saveButton.setDisable(false);

            int currentPage = tagListController.pagination.getCurrentPageIndex();
            int totalPages = tagListController.getTotalPages();

            tagListController.pagination.setPageCount(totalPages);
            tagListController.updateTestList(currentPage);
        } catch (EntityNotFoundException e) {

            showAlert(Alert.AlertType.ERROR, "Помилка", "Назва вже використовується");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void stealFields(Tag tag) {
        tagViewModel = new TagViewModel(
                tag.getId(),
                tag.getName()
        );

        bindFieldsToViewModel();
        saveButton.setDisable(true);
    }
}
