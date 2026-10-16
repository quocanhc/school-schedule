package com.school.schedule.service;

import com.school.schedule.model.Room;
import com.school.schedule.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Room findById(Long id) {
        return roomRepository.findById(id).orElseThrow();
    }

    public boolean nameTaken(Room room) {
        return room.getId() == null
                ? roomRepository.existsByName(room.getName())
                : roomRepository.existsByNameAndIdNot(room.getName(), room.getId());
    }

    public void save(Room room) {
        roomRepository.save(room);
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }
}
