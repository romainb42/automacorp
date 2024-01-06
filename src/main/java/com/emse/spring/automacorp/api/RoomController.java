package com.emse.spring.automacorp.api;

import com.emse.spring.automacorp.dao.HeaterDao;
import com.emse.spring.automacorp.dao.RoomDao;
import com.emse.spring.automacorp.dao.WindowDao;
import com.emse.spring.automacorp.dto.Room;
import com.emse.spring.automacorp.dto.RoomMapper;
import com.emse.spring.automacorp.model.BuildingEntity;
import com.emse.spring.automacorp.model.RoomEntity;
import com.emse.spring.automacorp.model.SensorEntity;
import com.emse.spring.automacorp.model.SensorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static Logger logger = LogManager.getLogger(RoomController.class);

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
        SensorEntity currentTemperature = new SensorEntity(SensorType.TEMPERATURE,room.currentTemperature().getName());
        currentTemperature.setId(room.currentTemperature().getId());
        currentTemperature.setValue(room.currentTemperature().getValue());
        SensorEntity outsideTemperature = new SensorEntity(SensorType.TEMPERATURE, room.building().getName());
        outsideTemperature.setId(room.building().getId());
        outsideTemperature.setValue(room.building().getOutsideTemperature().getValue());
        BuildingEntity building = new BuildingEntity(room.building().getName(), outsideTemperature);
        building.setId(room.building().getId());
        building.setRooms(room.building().getRooms());
        RoomEntity entity = new RoomEntity(room.floor(), room.name(), currentTemperature,room.targetTemperature() , building);
        RoomEntity saved = roomDao.save(entity);
        return ResponseEntity.ok(RoomMapper.of(saved));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Room> update(@PathVariable Long id, @RequestBody RoomCommand room) {
        if (id==null && room==null){
            logger.fatal("There are no id nor room...");
            return null;
        }
        else if (id==null || room==null) {
            logger.error("There is a missing argument between id and room.");
            return null;
        }
        else {
            RoomEntity entity = roomDao.findById(id).orElse(null);
            if (entity == null) {
                logger.warn("The given room id (" + id +")doesn't exist.");
                return ResponseEntity.badRequest().build();
            }
            logger.debug("Beginning of room " + id + "'s update");
            entity.setFloor(room.floor());
            entity.setName(room.name());
            entity.setCurrentTemperature(room.currentTemperature());
            entity.setTargetTemperature(room.targetTemperature());
            entity.setWindows(room.windows());
            logger.info("The room " + id + " has successfully been modified.");
            return ResponseEntity.ok(RoomMapper.of(entity));
        }
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
