package com.school.schedule.service;

import com.school.schedule.model.TimeSlot;
import com.school.schedule.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    public static final List<DayOfWeek> SCHOOL_DAYS = List.of(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

    private final TimeSlotRepository repository;

    public List<TimeSlot> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(TimeSlot::getDayOfWeek)
                        .thenComparing(TimeSlot::getPeriod))
                .toList();
    }

    public TimeSlot findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public boolean exists(TimeSlot t) {
        return t.getId() == null
                ? repository.existsByDayOfWeekAndPeriod(t.getDayOfWeek(), t.getPeriod())
                : repository.existsByDayOfWeekAndPeriodAndIdNot(t.getDayOfWeek(), t.getPeriod(), t.getId());
    }

    public void save(TimeSlot t) {
        repository.save(t);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /** Creates Monday-Saturday x periods 1-10, skipping ones that already exist. */
    public void generateDefaults() {
        for (DayOfWeek d : SCHOOL_DAYS) {
            for (int p = 1; p <= 10; p++) {
                if (!repository.existsByDayOfWeekAndPeriod(d, p)) {
                    TimeSlot t = new TimeSlot();
                    t.setDayOfWeek(d);
                    t.setPeriod(p);
                    repository.save(t);
                }
            }
        }
    }
}