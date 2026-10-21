   package com.school.schedule.repository;

   import com.school.schedule.model.Room;
   import org.springframework.data.jpa.repository.JpaRepository;

   public interface RoomRepository extends JpaRepository<Room, Long> {
       boolean existsByName(String name);
       boolean existsByNameAndIdNot(String name, Long id);
   }