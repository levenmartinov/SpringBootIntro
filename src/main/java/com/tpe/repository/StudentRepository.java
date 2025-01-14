package com.tpe.repository;


import com.tpe.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository//opsiyonel
public interface StudentRepository extends JpaRepository<Student, Long> {

    //JpaRepositorydeki metodlar Spring tarafından otomatik olarak implemente edilir


    //5-
    boolean existsByEmail(String email);



}
