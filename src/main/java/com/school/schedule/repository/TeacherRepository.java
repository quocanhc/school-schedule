   package com.school.schedule.repository;

   import com.school.schedule.model.Teacher;
   import org.springframework.data.jpa.repository.JpaRepository;

   public interface TeacherRepository extends JpaRepository<Teacher, Long> {
   }