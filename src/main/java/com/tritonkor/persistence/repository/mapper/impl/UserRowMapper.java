package com.tritonkor.persistence.repository.mapper.impl;

import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));

        return User.builder()
                .id(id)
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .avatar(rs.getBytes("avatar"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .role(User.Role.valueOf(rs.getString("role")))
                .build();
    }
}
