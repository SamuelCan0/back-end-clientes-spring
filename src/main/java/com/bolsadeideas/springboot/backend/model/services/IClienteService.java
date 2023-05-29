package com.bolsadeideas.springboot.backend.model.services;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.backend.models_entity.Region;
import com.bolsadeideas.springboot.backend.models_entity.cliente;

public interface IClienteService {

	public List<cliente> findAll();
	
	public Page<cliente> findAll(Pageable pageable);
	
	public cliente findById(Long id);
	
	public cliente save(cliente client);
	
	public void delete(Long id);
	
	public List<Region> findAllRegiones();  
}
