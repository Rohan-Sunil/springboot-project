package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.exception.ResourceNotFoundException;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {

        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()) {
            return optionalStudent.get();
        } else {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
    }

    @Override
    public Student updateStudent(Long id, Student student) {

        Student existingStudent = getStudentById(id);

        existingStudent.setName(student.getName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setCourse(student.getCourse());

        return studentRepository.save(existingStudent);
    }

    @Override
    public Student patchStudent(Long id, Student student) {

        Student existingStudent = getStudentById(id);

        if (student.getName() != null) {
            existingStudent.setName(student.getName());
        }

        if (student.getEmail() != null) {
            existingStudent.setEmail(student.getEmail());
        }

        if (student.getCourse() != null) {
            existingStudent.setCourse(student.getCourse());
        }

        return studentRepository.save(existingStudent);
    }


    @Override
    public void deleteStudent(Long id) {

        Student student = getStudentById(id);
        studentRepository.delete(student);
    }
}
