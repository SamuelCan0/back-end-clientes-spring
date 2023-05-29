package com.bolsadeideas.springboot.backend.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.backend.model.dao.IClienteDao;
import com.bolsadeideas.springboot.backend.models_entity.cliente;

import jakarta.transaction.Transactional;

@Service
public class ClienteServiceImpl implements IClienteService{
	@Autowired
	private IClienteDao clienteDao;
	

	@Override
	@Transactional
	public List<cliente> findAll(){
		return (List<cliente>) clienteDao.findAll();
	}
	
	@Override
	@Transactional
	public Page<cliente> findAll(Pageable pageable){
		return clienteDao.findAll(pageable);
	}
	

	@Override
	@Transactional
	public cliente findById(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public cliente save(cliente client) {
		return clienteDao.save(client);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.deleteById(id);
		
	}




	

	
	

}
