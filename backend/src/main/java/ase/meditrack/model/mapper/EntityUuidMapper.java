package ase.meditrack.model.mapper;

import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public interface EntityUuidMapper {
    default UUID userToId(User entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default User idToUser(UUID id) {
        if (id == null) return null;
        User entity = new User();
        entity.setId(id);
        return entity;
    }


    default UUID shiftToId(Shift entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default Shift idToShift(UUID id) {
        if (id == null) return null;
        Shift entity = new Shift();
        entity.setId(id);
        return entity;
    }


    default UUID shiftSwapToId(ShiftSwap entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default ShiftSwap idToShiftSwap(UUID id) {
        if (id == null) return null;
        ShiftSwap entity = new ShiftSwap();
        entity.setId(id);
        return entity;
    }


    default UUID monthlyPlanToId(MonthlyPlan entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default MonthlyPlan idToMonthlyPlan(UUID id) {
        if (id == null) return null;
        MonthlyPlan entity = new MonthlyPlan();
        entity.setId(id);
        return entity;
    }


    default UUID shiftTypesToId(ShiftType entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default ShiftType idToShiftTypes(UUID id) {
        if (id == null) return null;
        ShiftType entity = new ShiftType();
        entity.setId(id);
        return entity;
    }


    default UUID holidayToId(Holiday entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default Holiday idToHoliday(UUID id) {
        if (id == null) return null;
        Holiday entity = new Holiday();
        entity.setId(id);
        return entity;
    }


    default UUID teamToId(Team entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default Team idToTeam(UUID id) {
        if (id == null) return null;
        Team entity = new Team();
        entity.setId(id);
        return entity;
    }


    default UUID hardConstraintsToId(HardConstraints entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default HardConstraints idToHardConstraints(UUID id) {
        if (id == null) return null;
        HardConstraints entity = new HardConstraints();
        entity.setId(id);
        return entity;
    }


    default UUID roleToId(Role entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default Role idToRole(UUID id) {
        if (id == null) return null;
        Role entity = new Role();
        entity.setId(id);
        return entity;
    }


    default UUID preferencesToId(Preferences entity) {
        if (entity == null) return null;
        return entity.getId();
    }

    default Preferences idToPreferences(UUID id) {
        if (id == null) return null;
        Preferences entity = new Preferences();
        entity.setId(id);
        return entity;
    }
}
