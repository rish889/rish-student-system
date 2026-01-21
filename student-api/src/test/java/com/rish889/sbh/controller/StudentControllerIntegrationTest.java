package com.rish889.sbh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rish889.sbh.entity.Student;
import com.rish889.sbh.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateStudent() throws Exception {
        Student student = new Student("John Doe", "john.doe@example.com");

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllStudents() throws Exception {
        studentRepository.save(new Student("Alice Smith", "alice@example.com"));
        studentRepository.save(new Student("Bob Johnson", "bob@example.com"));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice Smith")))
                .andExpect(jsonPath("$[1].name", is("Bob Johnson")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetStudentById() throws Exception {
        Student saved = studentRepository.save(new Student("Jane Doe", "jane@example.com"));

        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is("jane@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateStudent() throws Exception {
        Student saved = studentRepository.save(new Student("Old Name", "old@example.com"));
        Student updated = new Student("New Name", "new@example.com");

        mockMvc.perform(put("/api/students/{id}", saved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.email", is("new@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteStudent() throws Exception {
        Student saved = studentRepository.save(new Student("To Delete", "delete@example.com"));

        mockMvc.perform(delete("/api/students/{id}", saved.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isForbidden());
    }
}