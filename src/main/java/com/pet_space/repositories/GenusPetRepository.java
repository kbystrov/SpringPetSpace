package com.pet_space.repositories;

import com.pet_space.models.GenusPet;
import org.springframework.data.repository.CrudRepository;

public interface GenusPetRepository extends CrudRepository<GenusPet, String> {
}
