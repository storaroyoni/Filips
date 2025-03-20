package com.filips.healthServer.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Table(name = "random_table")
@Entity
public class Mod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
}
