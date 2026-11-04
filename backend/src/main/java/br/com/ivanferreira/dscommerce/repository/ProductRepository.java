package br.com.ivanferreira.dscommerce.repository;

import br.com.ivanferreira.dscommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
