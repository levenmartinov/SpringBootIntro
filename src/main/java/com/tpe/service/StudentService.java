package com.tpe.service;

import com.tpe.domain.Student;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    //2- tablodan tum kayitlari getirme
    public List<Student> findAllStudents() {
        return repository.findAll();
    }

    //4-
    public void saveStudent(Student student) {

        //student aynı email ile daha önce tabloya eklenmiş mi
        //select * from t_student where email=student.getEmail....>0 --->t/f
        boolean existsStudent = repository.existsByEmail(student.getEmail());
        if (existsStudent) {
            //bu email daha once kullanilmis --> hata firlatalim
            throw new ConflictException("Email already exists!");
        }
        repository.save(student); //insert into ..  direkt sorgu yazmadan yaziyor


    }


    //7-
    public Student getStudentById(Long id) {

        Student student = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student is not found by id: " + id));

        return student;

    }

    //9-
    public void deleteStudentById(Long id) {

        //id'si verilen student yoksa ozel bir mesaj ile custom exception firiltmak istiyoruz

        // getStudentById(id);
        // repository.deleteById(id);

        //bu id ile ogrenci var mi?
        Student student = getStudentById(id);
        repository.delete(student);

    }





















}
