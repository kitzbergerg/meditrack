package ase.meditrack.service.algorithm;

import java.time.LocalTime;

public record ShiftTypeInfo(LocalTime startTime, LocalTime endTime, int duration) {
}
