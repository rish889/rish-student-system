package com.rish889.sbh.repository;

import com.rish889.sbh.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

