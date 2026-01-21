package com.rish889.sbh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rish889.sbh.entity.Student;
import com.rish889.sbh.security.SecurityConfig;
import com.rish889.sbh.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(SecurityConfig.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private StudentService studentService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturnCreatedStudent() throws Exception {
        // Given
        Student inputStudent = new Student("John Doe", "john@example.com");
        Student savedStudent = new Student("John Doe", "john@example.com");
        // Simulate ID assignment by repository
        savedStudent.setId(1L);

        when(studentService.create(any(Student.class))).thenReturn(savedStudent);

        // When & Then
        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(studentService, times(1)).create(any(Student.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnListOfStudents() throws Exception {
        // Given
        Student student1 = new Student("John Doe", "john@example.com");
        student1.setId(1L);
        Student student2 = new Student("Jane Smith", "jane@example.com");
        student2.setId(2L);
        List<Student> students = Arrays.asList(student1, student2);

        when(studentService.getAll()).thenReturn(students);

        // When & Then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")));

        verify(studentService, times(1)).getAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ShouldReturnEmptyListWhenNoStudents() throws Exception {
        // Given
        when(studentService.getAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(studentService, times(1)).getAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ShouldReturnStudent() throws Exception {
        // Given
        Student student = new Student("John Doe", "john@example.com");
        student.setId(1L);

        when(studentService.getById(1L)).thenReturn(student);

        // When & Then
        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(studentService, times(1)).getById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdatedStudent() throws Exception {
        // Given
        Student updatedStudent = new Student("John Updated", "john.updated@example.com");
        updatedStudent.setId(1L);

        when(studentService.update(eq(1L), any(Student.class))).thenReturn(updatedStudent);

        // When & Then
        mockMvc.perform(put("/api/students/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")));

        verify(studentService, times(1)).update(eq(1L), any(Student.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldCallServiceDelete() throws Exception {
        // Given
        doNothing().when(studentService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/students/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(studentService, times(1)).delete(1L);
    }

    @Test
    void create_ShouldReturn401WhenNotAuthenticated() throws Exception {
        // Given
        Student student = new Student("John Doe", "john@example.com");

        // When & Then
        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isUnauthorized());

        verify(studentService, never()).create(any(Student.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_ShouldReturn403WhenNotAdmin() throws Exception {
        // Given
        Student student = new Student("John Doe", "john@example.com");

        // When & Then
        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isForbidden());

        verify(studentService, never()).create(any(Student.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_ShouldReturn403WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isForbidden());

        verify(studentService, never()).getAll();
    }
}