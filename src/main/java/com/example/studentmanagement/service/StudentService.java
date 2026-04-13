package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import java.util.List;

public interface StudentService {

    Student saveStudent(Student student);

    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student updateStudent(Long id, Student student);

    Student patchStudent(Long id, Student student);


    void deleteStudent(Long id);
}
