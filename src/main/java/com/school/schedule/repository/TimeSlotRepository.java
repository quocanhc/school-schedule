package com.school.schedule.repository;

import com.school.schedule.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    boolean existsByDayOfWeekAndPeriod(DayOfWeek dayOfWeek, Integer period);
    boolean existsByDayOfWeekAndPeriodAndIdNot(DayOfWeek dayOfWeek, Integer period, Long id);
}