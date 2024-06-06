package ase.meditrack.service;

import ase.meditrack.config.KeycloakConfig;
import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.ShiftOffShiftIdList;
import ase.meditrack.repository.ShiftOffShiftIdListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockBean(KeycloakConfig.class)
@MockBean(KeycloakConfig.KeycloakPostConstruct.class)
@MockBean(RealmResource.class)
public class ShiftOffShiftIdListServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ShiftOffShiftIdListRepository repository;
    @Autowired
    private ShiftOffShiftIdListService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllReturnsAllShiftOffShiftIdLists() {
        List<ShiftOffShiftIdList> resultList = service.findAll();

        assertAll(
                () -> assertNotNull(resultList),
                () -> assertEquals(0, resultList.size())
        );
    }

    @Test
    void findByIdReturnsShiftOffShiftIdList() {
        ShiftOffShiftIdList shiftOffShiftIdList = new ShiftOffShiftIdList();
        shiftOffShiftIdList.setShiftOffShiftIdList(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        repository.save(shiftOffShiftIdList);
        ShiftOffShiftIdList savedShiftOffShiftIdList = repository.findById(shiftOffShiftIdList.getId()).get();

        ShiftOffShiftIdList result = service.findById(savedShiftOffShiftIdList.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(shiftOffShiftIdList.getId(), result.getId()),
                () -> assertEquals(shiftOffShiftIdList, result)
        );
    }

    @Test
    void findByIdThrowsNotFoundExceptionWhenNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findById(UUID.randomUUID()));

        assertEquals("shiftOffShiftIdList not found", exception.getMessage());
    }

    @Test
    void createShiftOffShiftIdList() {
        ShiftOffShiftIdList shiftOffShiftIdList = new ShiftOffShiftIdList();
        shiftOffShiftIdList.setShiftOffShiftIdList(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        ShiftOffShiftIdList result = service.create(shiftOffShiftIdList);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(shiftOffShiftIdList, result)
        );
    }

    @Test
    void updateShiftOffShiftIdList() {
        ShiftOffShiftIdList shiftOffShiftIdList = new ShiftOffShiftIdList();
        shiftOffShiftIdList.setShiftOffShiftIdList(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        repository.save(shiftOffShiftIdList);
        ShiftOffShiftIdList savedShiftOffShiftIdList = repository.findById(shiftOffShiftIdList.getId()).get();

        ShiftOffShiftIdList updatedList = new ShiftOffShiftIdList();
        updatedList.setId(savedShiftOffShiftIdList.getId());
        updatedList.setShiftOffShiftIdList(Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        ShiftOffShiftIdList result = service.update(updatedList);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedList.getShiftOffShiftIdList().size(), result.getShiftOffShiftIdList().size())
        );
    }

    @Test
    void updateShiftOffShiftIdListThrowsNotFoundExceptionWhenNotFound() {
        ShiftOffShiftIdList updatedList = new ShiftOffShiftIdList();
        updatedList.setId(UUID.randomUUID());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.update(updatedList));

        assertEquals("shiftOffShiftIdList not found", exception.getMessage());
    }

    @Test
    void deleteShiftOffShiftIdList() {
        ShiftOffShiftIdList shiftOffShiftIdList = new ShiftOffShiftIdList();
        shiftOffShiftIdList.setShiftOffShiftIdList(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        repository.save(shiftOffShiftIdList);
        ShiftOffShiftIdList savedShiftOffShiftIdList = repository.findById(shiftOffShiftIdList.getId()).get();

        List<ShiftOffShiftIdList> resultList = service.findAll();
        assertEquals(1, resultList.size());

        service.delete(savedShiftOffShiftIdList.getId());

        resultList = service.findAll();
        assertEquals(0, resultList.size());
    }
}
