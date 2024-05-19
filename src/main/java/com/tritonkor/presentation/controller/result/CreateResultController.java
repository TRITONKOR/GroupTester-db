package com.tritonkor.presentation.controller.result;

import com.tritonkor.domain.dto.ResultStoreDto;
import com.tritonkor.domain.dto.ResultUpdateDto;
import com.tritonkor.domain.service.impl.ResultService;
import com.tritonkor.persistence.entity.Mark;
import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.presentation.viewmodel.ResultViewModel;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateResultController {
    @Autowired
    private ResultListController resultListController;

    @Autowired
    private ResultService resultService;
    @FXML
    private Label resultLabel;
    @FXML
    private ComboBox<String> testComboBox;
    @FXML
    private ComboBox<String> ownerComboBox;
    @FXML
    private Spinner<Integer> markSpinner;
    @FXML
    private Button saveButton;

    private ResultViewModel resultViewModel;

    private final UserRepository userRepository;
    private final TestRepository testRepository;


    public CreateResultController(UserRepository userRepository, TestRepository testRepository) {
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }

    @FXML
    public void initialize() {
        resultViewModel = new ResultViewModel(
                UUID.randomUUID(), "", "", null);

        Set<User> teachers = userRepository.findAllWhere(STR."role = 'STUDENT'");
        Set<String> usernames = new HashSet<>();
        for (User user : teachers) {
            usernames.add(user.getUsername());
        }
        ownerComboBox.getItems().addAll(usernames);

        Set<Test> tests = testRepository.findAll();
        Set<String> testTitles = new HashSet<>();
        for (Test test : tests) {
            testTitles.add(test.getTitle());
        }
        testComboBox.getItems().addAll(testTitles);

        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        testComboBox.valueProperty().bindBidirectional(resultViewModel.testTitleProperty());
        ownerComboBox.valueProperty().bindBidirectional(resultViewModel.ownerUsernameProperty());

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100); // Мінімальне та максимальне значення

        markSpinner.setValueFactory(valueFactory);

        // Налаштування конвертера для відображення значень Spinner
        markSpinner.setEditable(true); // Дозволяє редагування значень у введеному полі
        markSpinner.getValueFactory().setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                return value != null ? value.toString() : "";
            }

            @Override
            public Integer fromString(String text) {
                return text.isEmpty() ? 0 : Integer.valueOf(text);
            }
        });

        // Прив'язка значення Spinner до відповідної властивості в resultViewModel
        markSpinner.getValueFactory().valueProperty().bindBidirectional(resultViewModel.markProperty());
    }

    @FXML
    public void onSave() throws IOException {
        System.out.println("Saving Result Data: " + resultViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Result Information");
        alert.setHeaderText("Result Data Saving");
        alert.setContentText(resultViewModel.toString());
        alert.showAndWait();

        if (resultViewModel.getTestTitle().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Тест не обрано");
            return;
        }
        if (resultViewModel.getOwnerUsername().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Користувача не обрано");
            return;
        }

        Test test = testRepository.findByTitle(resultViewModel.getTestTitle()).orElseThrow();
        User owner = userRepository.findByUsername(resultViewModel.getOwnerUsername()).orElseThrow();
        int mark = resultViewModel.getMark();


        ResultStoreDto resultStoreDto = new ResultStoreDto(
                test.getId(),
                owner.getId(),
                resultListController.getCurrentReport().getId(),
                new Mark(mark)
        );

        resultService.create(resultStoreDto);

        int currentPage = resultListController.pagination.getCurrentPageIndex();
        int totalPages = resultListController.getTotalPages();

        resultListController.pagination.setPageCount(totalPages);
        resultListController.updateResultList(currentPage);
    }

    @FXML
    public void onUpdate() throws IOException {
        System.out.println("Updating Result Data: " + resultViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Result Information");
        alert.setHeaderText("Result Data Updating");
        alert.setContentText(resultViewModel.toString());
        alert.showAndWait();

        try {
            resultService.findById(resultViewModel.getId());

        Test test = testRepository.findByTitle(resultViewModel.getTestTitle()).orElseThrow();
        User owner = userRepository.findByUsername(resultViewModel.getOwnerUsername()).orElseThrow();
        int mark = resultViewModel.getMark();


        ResultUpdateDto resultUpdateDto = new ResultUpdateDto(
                resultViewModel.getId(),
                test.getId(),
                owner.getId(),
                resultListController.getCurrentReport().getId(),
                new Mark(mark)
        );

        resultService.update(resultUpdateDto);

        int currentPage = resultListController.pagination.getCurrentPageIndex();
        int totalPages = resultListController.getTotalPages();

        resultListController.pagination.setPageCount(totalPages);
        resultListController.updateResultList(currentPage);
        } catch (EntityNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Результат з таким айді не існує");
        }
    }

    public void stealFields(Result result) {
        Test test = testRepository.findById(result.getTestId()).orElseThrow();
        User owner = userRepository.findById(result.getOwnerId()).orElseThrow();

        resultViewModel = new ResultViewModel(
                result.getId(),
                test.getTitle(),
                owner.getUsername(),
                result.getMark().getGrade()
        );

        bindFieldsToViewModel();
        saveButton.setDisable(true);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
