package com.tritonkor.presentation.controller.tag;

import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.entity.filter.TagFilterDto;
import com.tritonkor.persistence.repository.contract.TagRepository;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagListController {
    @Autowired
    private CreateTagController createTagController;

    @FXML
    private TextField searchField;
    @FXML
    private VBox tagListContainer;
    @FXML
    public Pagination pagination;
    private static final int PAGE_SIZE = 5;
    private String sortColumn = "name";
    private boolean ascending = true;

    private final TagRepository tagRepository;

    private TagFilterDto currentFilters = new TagFilterDto("");

    public TagListController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @FXML
    public void initialize() {
        pagination.setPageCount(getTotalPages());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            handlePageChange(newIndex.intValue());
        });
        updateTestList(0);
    }


    public int getTotalPages() {
        long totalTests = tagRepository.count();
        return (int) Math.ceil((double) totalTests / PAGE_SIZE);
    }

    public void updateTestList(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;

        Set<Tag> tags = tagRepository.findAll(
                offset, PAGE_SIZE, sortColumn, ascending, currentFilters
        );

        tagListContainer.getChildren().clear();
        tagListContainer.getChildren().addAll(tags.stream().map(this::createTagCard).toList());
    }

    private VBox createTagCard(Tag tag) {
        Label nameLabel = new Label(tag.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Button deleteButton = new Button("Видалити");
        Button updateButton = new Button("Оновити");

        HBox hBox = new HBox(deleteButton, updateButton);

        deleteButton.setOnAction(event -> handleDeleteTag(tag));
        updateButton.setOnAction(event -> handleUpdateTag(tag));

        VBox tagCard = new VBox(nameLabel, hBox);
        tagCard.setSpacing(5);
        tagCard.setStyle(
                "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 5;");

        return tagCard;
    }

    @FXML
    private void handlePageChange(int pageIndex) {
        updateTestList(pageIndex);
    }

    @FXML
    public void handleSortAscending(ActionEvent actionEvent) {
        ascending = true;
        updateTestList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleSortDescending(ActionEvent actionEvent) {
        ascending = false;
        updateTestList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleApplyFilters(ActionEvent actionEvent) {
        String name = searchField.getText().trim();

        // не дороблено, щоб фільтрувати по діапазону дат (приклад в коментарях є)
        currentFilters = new TagFilterDto(name);

        pagination.setPageCount(getTotalPages());
        updateTestList(0);
    }

    @FXML
    public void handleDeleteTag(Tag tag) {
        tagRepository.delete(tag.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tag Information");
        alert.setHeaderText("Tag Deletion");
        alert.setContentText(tag.getName() + " Has Been Deleted Successfully");
        alert.showAndWait();

        pagination.setPageCount(getTotalPages());
        updateTestList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleUpdateTag(Tag tag) {
        createTagController.stealFields(tag);
    }
}
