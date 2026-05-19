package com.skillsphere.skillsphere.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skillsphere.skillsphere.user.entity.Instructor;
import com.skillsphere.skillsphere.user.entity.User;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByUser(User user);
    Optional<Instructor> findByIdAndIsDeletedFalse(Long id);
}
