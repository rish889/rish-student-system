package com.rish889.sbh.service;

import com.rish889.sbh.entity.Student;
import com.rish889.sbh.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new Student("John Doe", "john.doe@example.com");
    }

    @Test
    void shouldCreateStudent() {
        // given
        when(repository.save(any(Student.class))).thenReturn(testStudent);

        // when
        Student result = studentService.create(testStudent);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(repository, times(1)).save(testStudent);
    }

    @Test
    void shouldGetAllStudents() {
        // given
        Student student1 = new Student("Alice Brown", "alice.brown@example.com");
        Student student2 = new Student("Bob Wilson", "bob.wilson@example.com");
        List<Student> students = Arrays.asList(student1, student2);
        when(repository.findAll()).thenReturn(students);

        // when
        List<Student> result = studentService.getAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(student1, student2);
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldGetEmptyListWhenNoStudents() {
        // given
        when(repository.findAll()).thenReturn(List.of());

        // when
        List<Student> result = studentService.getAll();

        // then
        assertThat(result).isEmpty();
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldGetStudentById() {
        // given
        Long studentId = 1L;
        when(repository.findById(studentId)).thenReturn(Optional.of(testStudent));

        // when
        Student result = studentService.getById(studentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(repository, times(1)).findById(studentId);
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFoundById() {
        // given
        Long studentId = 999L;
        when(repository.findById(studentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studentService.getById(studentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");
        verify(repository, times(1)).findById(studentId);
    }

    @Test
    void shouldUpdateStudent() {
        // given
        Long studentId = 1L;
        Student existingStudent = new Student("John Doe", "john.doe@example.com");
        Student updatedData = new Student("John Updated", "john.updated@example.com");

        when(repository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(repository.save(any(Student.class))).thenReturn(updatedData);

        // when
        Student result = studentService.update(studentId, updatedData);

        // then
        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        verify(repository, times(1)).findById(studentId);
        verify(repository, times(1)).save(updatedData);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentStudent() {
        // given
        Long studentId = 999L;
        Student updatedData = new Student("John Updated", "john.updated@example.com");
        when(repository.findById(studentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studentService.update(studentId, updatedData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");
        verify(repository, times(1)).findById(studentId);
        verify(repository, never()).save(any(Student.class));
    }

    @Test
    void shouldDeleteStudent() {
        // given
        Long studentId = 1L;
        doNothing().when(repository).deleteById(studentId);

        // when
        studentService.delete(studentId);

        // then
        verify(repository, times(1)).deleteById(studentId);
    }

    @Test
    void shouldNotThrowExceptionWhenDeletingStudent() {
        // given
        Long studentId = 1L;
        doNothing().when(repository).deleteById(studentId);

        // when & then
        studentService.delete(studentId);
        verify(repository, times(1)).deleteById(studentId);
    }
}