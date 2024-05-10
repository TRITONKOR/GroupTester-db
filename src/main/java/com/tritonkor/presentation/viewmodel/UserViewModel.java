package com.tritonkor.presentation.viewmodel;

import com.tritonkor.persistence.entity.User.Role;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class UserViewModel {

    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final ObjectProperty<Image> avatar = new SimpleObjectProperty<>();
    private Path avatarPath;
    private final ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();
    private final ObjectProperty<Role> role = new SimpleObjectProperty<>();

    public UserViewModel(UUID id, String username, String email, String password, Image avatar,
        Path avatarPath, LocalDate birthday, Role role) {
        this.id.set(id);
        this.username.set(username);
        this.email.set(email);
        this.password.set(password);
        this.avatar.set(avatar);
        this.avatarPath = avatarPath;
        this.birthday.set(birthday);
        this.role.set(role);
    }

    public UUID getId() {
        return id.get();
    }

    public ObjectProperty<UUID> idProperty() {
        return id;
    }

    public void setId(UUID id) {
        this.id.set(id);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public Image getAvatar() {
        return avatar.get();
    }

    public ObjectProperty<Image> avatarProperty() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar.set(avatar);
    }

    public Path getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(Path avatarPath) {
        this.avatarPath = avatarPath;
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public ObjectProperty<LocalDate> birthdayProperty() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
    }

    public Role getRole() {
        return role.get();
    }

    public ObjectProperty<Role> roleProperty() {
        return role;
    }

    public void setRole(Role role) {
        this.role.set(role);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserViewModel.class.getSimpleName() + "[", "]")
            .add("id=" + id.get())
            .add("username=" + username.get())
            .add("email=" + email.get())
            .add("password=" + password.get())
            .add("photo=" + avatar.get())
            .add("birthday=" + birthday.get())
            .add("role=" + role.get())
            .toString();
    }
}
