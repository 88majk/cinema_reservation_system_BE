package com.example.cinemaressys.repositories;

import com.example.cinemaressys.entities.Cinema;
import com.example.cinemaressys.entities.Role;
import com.example.cinemaressys.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.beans.Transient;
import java.util.List;

public interface    UserRepositories extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    @Query("SELECT u.password FROM User u WHERE u.email = :email")
    String findPasswordByEmail(@Param("email") String email);
    boolean findIsAdminByEmail(String email);
    User findUserByEmail(String email);
    List<User> findByRoleRoleId(int roleId);
    User findByUserId(int userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.userId = :userId")
    void deleteByUserId(@Param("userId") int userId);
}
