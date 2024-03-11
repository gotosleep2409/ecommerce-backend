package org.example.apitest.repository;

import org.example.apitest.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    Size getSizeByName (String name);
}
