package ase.meditrack.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TestDto (@NotBlank Integer id, @NotBlank @Size(min = 1, max = 2000) String value){
}
