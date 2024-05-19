package com.tritonkor.presentation.controller.test;

import com.tritonkor.domain.service.impl.TagService;
import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.repository.contract.TagRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.presentation.viewmodel.TagViewModel;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddTestTagsController {

    @Autowired
    private TestTagsListController testTagsListController;

    @Autowired
    private TagService tagService;
    @FXML
    private ComboBox<String> tagComboBox;

    private TagViewModel tagViewModel;

    private final TestRepository testRepository;
    private final TagRepository tagRepository;

    public AddTestTagsController(TestRepository testRepository, TagRepository tagRepository) {
        this.testRepository = testRepository;
        this.tagRepository = tagRepository;
    }

    @FXML
    public void initialize() {
        tagViewModel = new TagViewModel(
                UUID.randomUUID(), "Title");

        Set<Tag> tags = tagService.findAll();
        Set<Tag> testTags = testRepository.findAllTags(
                testTagsListController.getCurrentTest().getId());

        tags.removeAll(testTags);

        Set<String> names = new HashSet<>();
        for (Tag tag : tags) {
            names.add(tag.getName());
        }
        tagComboBox.getItems().addAll(names);

        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        tagComboBox.valueProperty().bindBidirectional(tagViewModel.nameProperty());
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

        Tag tag = tagService.findByName(name);

        tagRepository.attach(testTagsListController.getCurrentTest().getId(), tag.getId());

        int currentPage = testTagsListController.pagination.getCurrentPageIndex();
        int totalPages = testTagsListController.getTotalPages();

        testTagsListController.pagination.setPageCount(totalPages);
        testTagsListController.updateTestList(currentPage);

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
