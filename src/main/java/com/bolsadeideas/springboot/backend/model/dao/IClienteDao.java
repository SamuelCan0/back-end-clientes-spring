package com.bolsadeideas.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolsadeideas.springboot.backend.models_entity.cliente;

public interface IClienteDao extends JpaRepository<cliente, Long>{

	List<cliente> findAll();
	
}
