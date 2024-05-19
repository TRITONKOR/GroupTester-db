package com.tritonkor.persistence.entity.filter;

import com.tritonkor.persistence.entity.User;
import java.time.LocalDate;

public record UserFilterDto(
        String username,
        String email,
        LocalDate birthdayFrom,
        LocalDate birthdayTo,
        User.Role role) {

}
