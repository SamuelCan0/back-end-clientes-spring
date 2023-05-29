package com.bolsadeideas.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bolsadeideas.springboot.backend.models_entity.Region;
import com.bolsadeideas.springboot.backend.models_entity.cliente;

public interface IClienteDao extends JpaRepository<cliente, Long>{

	@Query("from Region")
	public List<Region> findAllRegiones(); 
	
}
