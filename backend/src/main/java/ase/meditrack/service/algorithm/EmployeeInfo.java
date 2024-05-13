package ase.meditrack.service.algorithm;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EmployeeInfo(UUID uuid, List<Integer> worksShifts, int workingHours) {
}
