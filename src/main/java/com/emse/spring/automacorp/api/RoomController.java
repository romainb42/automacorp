package com.emse.spring.automacorp.api;

import com.emse.spring.automacorp.dao.HeaterDao;
import com.emse.spring.automacorp.dao.RoomDao;
import com.emse.spring.automacorp.dao.WindowDao;
import com.emse.spring.automacorp.dto.Heater;
import com.emse.spring.automacorp.dto.Room;
import com.emse.spring.automacorp.dto.RoomMapper;
import com.emse.spring.automacorp.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/rooms")
@Transactional
public class RoomController {

    private final RoomDao roomDao;
    private final WindowDao windowDao;
    private final HeaterDao heaterDao;

    public RoomController(RoomDao roomDao, WindowDao windowDao, HeaterDao heaterDao) {
        this.roomDao = roomDao;
        this.windowDao = windowDao;
        this.heaterDao = heaterDao;
    }

    @GetMapping
    public List<Room> findAll() {
        return roomDao.findAll()
                .stream()
                .map(RoomMapper::of)
                .sorted(Comparator.comparing(Room::name))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public Room findById(@PathVariable Long id) {
        return roomDao.findById(id).map(RoomMapper::of).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody RoomCommand room) {
        SensorEntity currentTemperature = new SensorEntity(SensorType.TEMPERATURE,"temperatureSensor");
        currentTemperature.setId(1L);
        currentTemperature.setValue(15.0);
        SensorEntity outsideTemperature = new SensorEntity(SensorType.TEMPERATURE, "Outside temperature");
        outsideTemperature.setId(2L);
        outsideTemperature.setValue(19.0);
        BuildingEntity building = new BuildingEntity(room.building().getName(), outsideTemperature);
        RoomEntity entity = new RoomEntity(1, room.name(), currentTemperature,20.0 , building);
        RoomEntity saved = roomDao.save(entity);
        return ResponseEntity.ok(RoomMapper.of(saved));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Room> update(@PathVariable Long id, @RequestBody RoomCommand room) {
        RoomEntity entity = roomDao.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.badRequest().build();
        }
        entity.setFloor(room.floor());
        entity.setName(room.name());
        entity.setCurrentTemperature(room.currentTemperature());
        entity.setTargetTemperature(room.targetTemperature());
        entity.setWindows(room.windows());
        return ResponseEntity.ok(RoomMapper.of(entity));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        windowDao.deleteWindowsByRoomId(id);
        heaterDao.deleteHeatersByRoomId(id);
        roomDao.deleteById(id);
    }

    @PutMapping(path = "/{id}/openWindows")
    public ResponseEntity<Room> openWindows(@PathVariable Long id) {
        RoomEntity entity = roomDao.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.badRequest().build();
        }
        windowDao.updateWindowStatusByRoomId(id, 1.0);
        entity.setWindows(roomDao.findById(id).get().getWindows());
        return ResponseEntity.ok(RoomMapper.of(entity));
    }

    @PutMapping(path = "/{id}/closeWindows")
    public ResponseEntity<Room> closeWindows(@PathVariable Long id) {
        RoomEntity entity = roomDao.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.badRequest().build();
        }
        windowDao.updateWindowStatusByRoomId(id, 0.0);
        entity.setWindows(roomDao.findById(id).get().getWindows());
        return ResponseEntity.ok(RoomMapper.of(entity));
    }

}
