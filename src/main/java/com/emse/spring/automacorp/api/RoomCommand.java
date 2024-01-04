package com.emse.spring.automacorp.api;

import com.emse.spring.automacorp.model.BuildingEntity;
import com.emse.spring.automacorp.model.HeaterEntity;
import com.emse.spring.automacorp.model.SensorEntity;
import com.emse.spring.automacorp.model.WindowEntity;

import java.util.Set;

public record RoomCommand(String name, Integer floor, SensorEntity currentTemperature, Double targetTemperature, Set<WindowEntity> windows, Set<HeaterEntity> heaters, BuildingEntity building) {
}
