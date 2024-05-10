package com.tritonkor.persistence.entity;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * The {@code User} class represents a user in the system.
 */
public class User extends Entity implements Comparable<User> {

    private String username;
    private String password;
    private String email;
    private byte[] avatar;
    private final LocalDate birthday;
    private final Role role;

    /**
     * Constructs a new {@code User} instance.
     *
     * @param id       The unique identifier for the user.
     * @param username The username of the user.
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @param birthday The birthday of the user.
     * @param role     The role of the user.
     */
    private User(UUID id, String username, String email, String password, byte[] avatar, LocalDate birthday,
            Role role) {
        super(id);
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.birthday = birthday;
        this.role = role;
    }

    /**
     * Returns a {@code UserBuilderId} instance to start building a {@code User}.
     *
     * @return A {@code UserBuilderId} instance.
     */
    public static UserBuilderId builder() {
        return id -> username -> email -> password -> avatar -> birthday -> role -> () -> new User(id,
                username,
                email, password, avatar, birthday, role);
    }

    /**
     * Interface for the {@code User} builder to set the ID.
     */
    public interface UserBuilderId {

        UserBuilderUsername id(UUID id);
    }

    /**
     * Interface for the {@code User} builder to set the username.
     */
    public interface UserBuilderUsername {

        UserBuilderEmail username(String username);
    }

    /**
     * Interface for the {@code User} builder to set the email.
     */
    public interface UserBuilderEmail {

        UserBuilderPassword email(String email);
    }

    /**
     * Interface for the {@code User} builder to set the password.
     */
    public interface UserBuilderPassword {

        UserBuilderAvatar password(String password);
    }

    public interface UserBuilderAvatar {

        UserBuilderBirthday avatar(byte[] avatar);
    }


    /**
     * Interface for the {@code User} builder to set the birthday.
     */
    public interface UserBuilderBirthday {

        UserBuilderRole birthday(LocalDate birthday);
    }

    /**
     * Interface for the {@code User} builder to set the user role.
     */
    public interface UserBuilderRole {

        UserBuilder role(Role role);
    }

    /**
     * Interface for the final steps of the {@code Result} builder.
     */
    public interface UserBuilder {

        User build();
    }

    /**
     * Gets the role of the user.
     *
     * @return The user's role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user's identifier.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the username of the user.
     *
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the password of the user.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the birthday of the user.
     *
     * @return The user's birthday.
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    @Override
    public int compareTo(User o) {

        return this.username.compareTo(o.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    /**
     * The {@code Role} enum represents the roles that a user can have.
     */
    public enum Role {

        TEACHER("teacher"),
        STUDENT("student"),
        ADMIN("admin");
        private String name;


        Role(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
