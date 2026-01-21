package com.rish889.sbh.repository;

import com.rish889.sbh.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    void shouldSaveStudent() {
        // given
        Student student = new Student("John Doe", "john.doe@example.com");

        // when
        Student savedStudent = studentRepository.save(student);

        // then
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo("John Doe");
        assertThat(savedStudent.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldFindStudentById() {
        // given
        Student student = new Student("Jane Smith", "jane.smith@example.com");
        Student savedStudent = studentRepository.save(student);

        // when
        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getId());

        // then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("Jane Smith");
        assertThat(foundStudent.get().getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void shouldReturnEmptyWhenStudentNotFound() {
        // when
        Optional<Student> foundStudent = studentRepository.findById(999L);

        // then
        assertThat(foundStudent).isEmpty();
    }

    @Test
    void shouldFindAllStudents() {
        // given
        Student student1 = new Student("Alice Brown", "alice.brown@example.com");
        Student student2 = new Student("Bob Wilson", "bob.wilson@example.com");
        studentRepository.save(student1);
        studentRepository.save(student2);

        // when
        List<Student> students = studentRepository.findAll();

        // then
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getName)
                .containsExactlyInAnyOrder("Alice Brown", "Bob Wilson");
    }

    @Test
    void shouldUpdateStudent() {
        // given
        Student student = new Student("Charlie Davis", "charlie.davis@example.com");
        Student savedStudent = studentRepository.save(student);

        // when
        savedStudent.setName("Charlie Updated");
        savedStudent.setEmail("charlie.updated@example.com");
        Student updatedStudent = studentRepository.save(savedStudent);

        // then
        assertThat(updatedStudent.getId()).isEqualTo(savedStudent.getId());
        assertThat(updatedStudent.getName()).isEqualTo("Charlie Updated");
        assertThat(updatedStudent.getEmail()).isEqualTo("charlie.updated@example.com");
    }

    @Test
    void shouldDeleteStudent() {
        // given
        Student student = new Student("David Miller", "david.miller@example.com");
        Student savedStudent = studentRepository.save(student);

        // when
        studentRepository.deleteById(savedStudent.getId());

        // then
        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getId());
        assertThat(foundStudent).isEmpty();
    }

    @Test
    void shouldDeleteAllStudents() {
        // given
        studentRepository.save(new Student("Student 1", "student1@example.com"));
        studentRepository.save(new Student("Student 2", "student2@example.com"));

        // when
        studentRepository.deleteAll();

        // then
        List<Student> students = studentRepository.findAll();
        assertThat(students).isEmpty();
    }

    @Test
    void shouldCountStudents() {
        // given
        studentRepository.save(new Student("Student 1", "student1@example.com"));
        studentRepository.save(new Student("Student 2", "student2@example.com"));
        studentRepository.save(new Student("Student 3", "student3@example.com"));

        // when
        long count = studentRepository.count();

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void shouldCheckIfStudentExists() {
        // given
        Student student = new Student("Emma Johnson", "emma.johnson@example.com");
        Student savedStudent = studentRepository.save(student);

        // when
        boolean exists = studentRepository.existsById(savedStudent.getId());
        boolean notExists = studentRepository.existsById(999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
