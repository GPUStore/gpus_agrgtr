package ru.mephi.gpus_agrgtr.rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mephi.gpus_agrgtr.entity.Characteristic;

import java.util.Optional;

@Repository
public interface CharacteristicRepository  extends JpaRepository<Characteristic, String> {

    Optional<Characteristic> findCharacteristicByName(String name);

}
