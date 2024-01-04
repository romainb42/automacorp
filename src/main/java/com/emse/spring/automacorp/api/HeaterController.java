package com.emse.spring.automacorp.api;

import com.emse.spring.automacorp.dao.HeaterDao;
import com.emse.spring.automacorp.dto.Heater;
import com.emse.spring.automacorp.dto.HeaterMapper;
import com.emse.spring.automacorp.model.HeaterEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/heaters")
@Transactional
public class HeaterController {
    private final HeaterDao heaterDao;

    public HeaterController(HeaterDao heaterDao) {
        this.heaterDao = heaterDao;
    }

    @GetMapping
    public List<Heater> findAll() {
        return heaterDao.findAll()
                .stream()
                .map(HeaterMapper::of)
                .sorted(Comparator.comparing(Heater::name))
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public Heater findById(@PathVariable Long id) {
        return heaterDao.findById(id).map(HeaterMapper::of).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Heater> create(@RequestBody HeaterCommand heater) {
        HeaterEntity entity = new HeaterEntity(heater.name(), heater.heaterStatus(), heater.room());
        HeaterEntity saved = heaterDao.save(entity);
        return ResponseEntity.ok(HeaterMapper.of(saved));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Heater> update(@PathVariable Long id, @RequestBody HeaterCommand heater) {
        HeaterEntity entity = heaterDao.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.badRequest().build();
        }
        entity.setName(heater.name());
        entity.setHeaterStatus(heater.heaterStatus());
        entity.setRoom(heater.room());
        return ResponseEntity.ok(HeaterMapper.of(entity));
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        heaterDao.deleteById(id);
    }
}
