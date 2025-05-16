package gr.ichrist.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dummy")
public class DummyEntity {
    @Id
    private Long id;
}
