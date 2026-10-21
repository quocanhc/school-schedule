package com.school.schedule.repository;

import com.school.schedule.model.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {
    List<ScheduleEntry> findBySchoolClassId(Long id);
    List<ScheduleEntry> findByTeacherId(Long id);
    List<ScheduleEntry> findByRoomId(Long id);
}
