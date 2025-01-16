package com.tpe.service;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
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


    //11- id'si verilen ogrencinin bilgilerini dto'da gelen bilgiler ile degistirelim
    public void updateStudent(Long id, UpdateStudentDTO studentDTO) {


        Student foundStudent = getStudentById(id);//1,Jack,Sparrow,jack@mail.com,98,13.01...

        //emailin unique olmasına engel var mı?
        //DTOdan gelen yeni email            tablodaki emailler
        //1-xxx@mail.com                     YOK V (existsByEmail:false)  -->update
        //2-harry@mail.com                   başka bir öğrenciye ait X (existsByEmail:true) -->ConflictException
        //3-jack@mail.com                    kendisine ait V (existsByEmail:true) -->bu bir çakışma değil

        //istek ile gönderilen email tabloda var mı?
        boolean existEmail = repository.existsByEmail(studentDTO.getEmail());//T:kendisinin veya başkasının
        boolean selfEmail = foundStudent.getEmail().equals(studentDTO.getEmail());//T:kendisine ait
        if (existEmail && !selfEmail) {
            //çakışma var
            throw new ConflictException("Email already exists!!!");
        }

        //Entity <-- DTO
        foundStudent.setName(studentDTO.getName());
        foundStudent.setLastname(studentDTO.getLastname());
        foundStudent.setEmail(studentDTO.getEmail());

        repository.save(foundStudent);//saveOrUpdate


    }

    //13-gerekli parametreleri(bilgileri) pageable ile vererek tum ogrencilerin sayfalamasini talep edilen sayfanin dondurulmesini saglayalim
    public Page<Student> getAllStudentsByPage(Pageable pageable) {

        Page<Student> studentPage = repository.findAll(pageable);
        return studentPage;


    }


    //15-
    public List<Student> getStudentsByeGrade(Integer grade) {

        //select * from Student where grade = 100
        //return repository.findAllByGrade(grade);

        return repository.filterStudentsByGrade(grade);


    }


    //18-Id'si verilen student i getirelim

    public StudentDTO getStudentByIdDto(Long id) {

        Student student = getStudentById(id);

        //Entity --> DTO
        //tablodan geln entitynin icindeki 3 datayi alip
        //DTO objesi icine yerlestirdik

        //StudentDTO studentDTO = new StudentDTO(student.getName(), student.getLastname(), student.getGrade());
        //  StudentDTO studentDTO=new StudentDTO();
        // studentDTO.setName(student.getName());....

        //yukarıdaki 2 seçenek zahmetli, bunun yerine DTO oluşturmak için
        // constructorın parametresine Entity objesi verip dönüşümü sağlayabiliriz


        StudentDTO studentDTO = new StudentDTO(student);

        return studentDTO;

    }


    //18-b:repositoryden doğrudan DTO objesi getirelim
    public StudentDTO getStudentInfoByDTO(Long id) {
        StudentDTO studentDTO = repository.findStudentDtoById(id).
                orElseThrow(() -> new ResourceNotFoundException("Student is not found by id: " + id));
        return studentDTO;
    }

















}
