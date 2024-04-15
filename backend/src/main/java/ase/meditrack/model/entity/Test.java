package ase.meditrack.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "test_table")
@Data
public class Test {
    @Id
    Integer id;

    String value;
}
