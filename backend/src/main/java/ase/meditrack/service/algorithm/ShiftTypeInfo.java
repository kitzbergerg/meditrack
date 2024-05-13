package ase.meditrack.service.algorithm;

import java.time.LocalTime;
import java.util.UUID;

public record ShiftTypeInfo(UUID uuid, LocalTime startTime, LocalTime endTime, int duration) {
}
