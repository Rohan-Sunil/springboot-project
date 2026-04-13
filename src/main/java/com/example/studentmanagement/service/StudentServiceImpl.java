package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    // Constructor Injection (best practice)
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student saveStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        return studentRepository.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        validateId(id);

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        validateId(id);

        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id: " + id));

        // Full update (PUT) → all fields required
        existingStudent.setName(requireNonEmpty(student.getName(), "Name"));
        existingStudent.setEmail(requireNonEmpty(student.getEmail(), "Email"));
        existingStudent.setCourse(requireNonEmpty(student.getCourse(), "Course"));

        return studentRepository.save(existingStudent);
    }

    @Override
    public Student patchStudent(Long id, Student student) {
        validateId(id);

        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id: " + id));

        // Partial update (PATCH)
        Optional.ofNullable(student.getName())
                .filter(name -> !name.trim().isEmpty())
                .ifPresent(existingStudent::setName);

        Optional.ofNullable(student.getEmail())
                .filter(email -> !email.trim().isEmpty())
                .ifPresent(existingStudent::setEmail);

        Optional.ofNullable(student.getCourse())
                .filter(course -> !course.trim().isEmpty())
                .ifPresent(existingStudent::setCourse);

        return studentRepository.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        validateId(id);

        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }

        studentRepository.deleteById(id);
    }


    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
    }

    private String requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value;
    }
}