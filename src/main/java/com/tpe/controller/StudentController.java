package com.tpe.controller;

import com.tpe.domain.Student;

import com.tpe.dto.StudentDTO;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/*
clienttan 3 şekilde veri alınır:
1-requestin BODY(JSON)
2-requestin URL query param
3-requestin URL path param
 */

@RestController//requestler bu classtaki metodlarla eşleştirilecek ve responselar hazırlanacak
//@ResponseBody :metodun dönüş değerini JSON formatında cevap olarak hazırlar
//@RequestBody  :requestin içindeki(body) JSON formatında olan datayı metodların parametresinde kullanabilmemizi sağlar
// obje<->JSON dönüşümü: Jackson kütüphanesi
@RequestMapping("/students")//https://localhost:8080/students...
@RequiredArgsConstructor//sadece final olan fieldları set eder
//    public StudentController(StudentService service) {
//        this.service = service;
//    }
public class StudentController {


    Logger logger = LoggerFactory.getLogger(StudentController.class);


    //@Autowired
    private final StudentService service;


    //SpringBOOT'u selamlama:)
    //http://localhost:8080/students/greet + GET
    //@ResponseBody
    @GetMapping("/greet")
    public String greet() {
        return "Hello Spring Boot:)";
    }

    //1-tüm öğrencileri listeleyelim : READ
    //Request : http://localhost:8080/students + GET
    //Response: tüm öğrencilerin listesini + 200 : OK(HttpStatus Code)

    @PreAuthorize("hasRole('STUDENT')") //metodu kullanmadan önce yetki kontrolü yapmayı sağlar
    @GetMapping
    //@ResponseBody:@Restcontroller içinde var, burada gerek kalmadı.
    public ResponseEntity<List<Student>> getAllStudents() {
        //tablodan öğrencileri getirelim
        List<Student> allStudents = service.findAllStudents();
        return new ResponseEntity<>(allStudents, HttpStatus.OK);//200
    }


    //ResponseEntity : cevabın body + status kodu

    //3-öğrenci ekleme : CREATE
    //Request : http://localhost:8080/students + POST + body(JSON)
    /*
    {
    "name":"Jack",
    "lastname":"Sparrow",
    "email":"jack@mail.com",
    "grade":98
    }
     */
    //Response: öğrenci tabloya eklenir , başarılı mesaj+ 201(Created)
    @PostMapping
    public ResponseEntity<String> createStudent(@Valid @RequestBody Student student){

        try {

            service.saveStudent(student);
            logger.info("yeni öğrenci eklendi : "+student.getName());

            return new ResponseEntity<>("Student is created successfully...",HttpStatus.CREATED);//201
        }catch (Exception e){
            logger.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);//400
        }

    }





    //6-query param ile id si verilen öğrenciyi getirme
    //request : http://localhost:8080/students/query?id=1 + GET
    //response:student + 200
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/query")
    public ResponseEntity<Student> getStudent(@RequestParam("id") Long id){
        Student foundstudent=service.getStudentById(id);
        return new ResponseEntity<>(foundstudent,HttpStatus.OK);
    }


    //ÖDEV:(Alternatif)6-path param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/1 + GET
    //response : student + 200
    @GetMapping("/{id}")
    public ResponseEntity<Student> findStudent(@PathVariable("id") Long id) {
        Student foundstudent = service.getStudentById(id);
        return new ResponseEntity<>(foundstudent, HttpStatus.OK);
    }

    //8-path param ile id si verilen öğrenciyi silme
    //request: http://localhost:8080/students/1 + DELETE
    //response: tablodan kayıt silinir,başarılı mesajı + 200
    @DeleteMapping("/{deletedId}")
    public ResponseEntity<String> deleteStudent(@PathVariable("deletedId") Long id) {

        service.deleteStudentById(id);

        //return new ResponseEntity<>("Student is deleted successfully...", HttpStatus.OK);
        return ResponseEntity.ok("Student is deleted successfully...");
    }


    //10- idsi verilen öğrencinin name,lastname ve emailini değiştirme(guncelleme)
    //request : http://localhost:8080/students/1 + PUT(yerine koyma)/PATCH(kismi) + BODY(JSON)
    //response : güncelleme, başarılı mesaj + 201
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable("id") Long id,
                                                @Valid @RequestBody UpdateStudentDTO studentDTO) {

        service.updateStudent(id, studentDTO);


        return new ResponseEntity<>("Student is updated successfully...", HttpStatus.CREATED); //201
    }


    //12-tüm öğrencileri listeleme : READ
    //pagination(sayfalandırma) : hız/performans
    //tüm kayıtları page page(sayfa sayfa) gösterelim

    //request :
    //http://localhost:8080/students/page?
    //                               page=3&
    //                               size=20&
    //                               sort=name&
    //                               direction=DESC(ASC) + GET
    @GetMapping("/page")
    public ResponseEntity<Page<Student>> getAllStudents(@RequestParam("page") int pageNo, //kacinci sayfa
                                                        @RequestParam("size") int size,   //her sayfada kac tane kayit
                                                        @RequestParam("sort") String property, //hangi ozellige gore siralama
                                                        @RequestParam("direction") Sort.Direction direction) { //siralamanin yonu icin sabit degisken

        //findAll metodunun sayfa getirmesi için gerekli olan bilgileri
        //pageable tipinde verebiliriz.
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(direction, property));

        Page<Student> studentPage = service.getAllStudentsByPage(pageable);

        return new ResponseEntity<>(studentPage, HttpStatus.OK);//200

    }

    //14- grade ile öğrencileri filtreleyelim
    //request : http://localhost:8080/students/grade/100 + GET
    //response : grade = 100 olan ogrencileri listeleyelim + 200
    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<Student>> getAllStudentsByGrade(@PathVariable("grade") Integer grade) {

        List<Student> studentList = service.getStudentsByeGrade(grade);
        return ResponseEntity.ok(studentList);
    }


    //ÖDEVVV:
    //JPA in metodlarını türetme
    //JPQL/SQL ile custom sorgu
    //16-lastname ile öğrencileri filtreleyelim
    // request:http://localhost:8080/students/lastname?lastname=Potter + GET
    //response : lastname e sahip olan öğrenci listesi + 200
    @GetMapping("/lastname")
    public ResponseEntity<List<Student>> getStudentsByLastName(@RequestParam String lastname) {

        List<Student> studentList = service.getAllStudentByLastname(lastname);

        return ResponseEntity.ok(studentList);//200
    }


    //Meraklısına ÖDEVVV:)isim veya soyisme göre filtreleme
    //request:http://localhost:8080/students/search?word=harry + GET

    @GetMapping("/search")
    public ResponseEntity<List<Student>> getAllStudentByNameOrLastName(@RequestParam("word") String word) {
        List<Student> studentList = service.getAllStudentByNameOrLastname(word);
        return ResponseEntity.ok(studentList);

    }

    //-----------------------------------------------------------------------------------------------

    //Meraklısına ÖDEVVV:)name içinde ".." hecesi geçen öğrencileri filtreleme
    //request:http://localhost:8080/students/filter?word=al + GET ex:halil, lale

    @GetMapping("/filter")
    public ResponseEntity<List<Student>> getStudentInfo(@RequestParam String word){

        List<Student> students=service.getStudentsSearching(word);

        return ResponseEntity.ok(students);//200

    }



    //-----------------------------------------------------------------------------------------------





    //17- id'si verilen öğrencinin name,lastname ve grade getirme
    //request:http://localhost:8080/students/info/2 + GET
    //response:id'si verilen öğrencinin sadece 3 datasını(field) DTO ile getirelim +200
    @GetMapping("/info/{id}")
    public ResponseEntity<StudentDTO> getStudentInfo(@PathVariable Long id) {

        //StudentDTO studentDTO = service.getStudentByIdDto(id); //18-
        StudentDTO studentDTO = service.getStudentInfoByDTO(id); //18-b

        return ResponseEntity.ok(studentDTO);
    }


    /*
    Loglama, bir yazılım veya sistemin çalışırken yaptığı önemli olayları,
     işlemleri ve hataları kaydetmesi anlamına gelir. Loglar,

     bilgisayar programlarının bir çeşit "günlüğü" veya "karne defteri" gibi düşünülebilir.
     Sistem, yaptığı işleri ve karşılaştığı problemleri
     buraya yazar ve bu bilgiler, geliştiriciler veya sistem yöneticileri için çok değerlidir.
    */




    //19-http://localhost:8080/students/welcome + GET
    @GetMapping("/welcome")
    public String welcome(HttpServletRequest request) {
        logger.info("Welcome isteği geldi!");
        logger.warn("welcome isteğinin pathi : " + request.getServletPath());
        logger.warn("welcome isteğinin metodu : " + request.getMethod());
        return "Welcome Spring Boot:)";
    }



    //Not:http://localhost:8080/students/update?name=Ali&lastname=Can&email=ali@mail.com


    /*
    Spring Boot Actuator, bir uygulamanın sağlık durumunu ve çalışma metriklerini izlemek
    için kullanılan bir Spring Boot kütüphanesidir. Actuator, bir uygulamanın
    arka planda nasıl çalıştığını görmenizi sağlar ve uygulamanın izlenebilirliğini artırır.
    Uygulama Sağlık Durumu:

    Uygulamanın çalışır durumda olup olmadığını kontrol eder.
    Örneğin: "Veritabanına bağlanabiliyor mu? Sunucu çalışıyor mu?"
    Metrik Takibi:

    Uygulamanın performansı hakkında bilgiler sağlar.
    Örneğin: "Kaç kullanıcı sisteme bağlandı? Bellek kullanımı ne durumda?"
    Günlük İşleyişin İzlenmesi:

    Loglama, yapılandırmalar, güvenlik bilgileri gibi iç detayları görmenizi sağlar.
    Sorun Giderme:

    Hata durumunda, sistemin hangi noktada sorun yaşadığını anlamanıza yardımcı olur
    /actuator/health    Uygulamanın sağlık durumunu gösterir.
    /actuator/metrics   Uygulamanın performansıyla ilgili metrikleri listeler.
    /actuator/env       Uygulamanın çevre değişkenlerini listeler.
    /actuator/loggers   Log seviyelerini ve log yapılandırmalarını kontrol eder.
    /actuator/info      Uygulama hakkında bilgi verir (ör. sürüm bilgisi).
    */






}