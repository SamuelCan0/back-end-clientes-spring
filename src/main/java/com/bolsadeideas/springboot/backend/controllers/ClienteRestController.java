package com.bolsadeideas.springboot.backend.controllers;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.backend.model.services.IClienteService;
import com.bolsadeideas.springboot.backend.model.services.IUploadFileService;
import com.bolsadeideas.springboot.backend.models_entity.Region;
import com.bolsadeideas.springboot.backend.models_entity.cliente;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadService;
	
	@GetMapping("/clientes")
	public List<cliente> index() {
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/page/{page}")
    public Page<cliente> index(@PathVariable Integer page) {
        return clienteService.findAll(PageRequest.of(page, 4));
    }
	
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		cliente client=null;
		Map<String, Object>response=new HashMap<>();
		
		try {
			client=clienteService.findById(id);
		}
		catch (DataAccessException e) {
			response.put("mensaje","Error al realizar la consulta en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		
		if (client==null) {
			response.put("mensaje", "el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<cliente>(client,HttpStatus.OK); 
	}
	
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Validated @RequestBody cliente client, BindingResult result) {
		
		
		cliente clienteNew=null;
		Map<String, Object>response=new HashMap<>();
		
		if(result.hasErrors()) {
			/*List<String> errors=new ArrayList<>();
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campo '"+err.getField()+"' "+err.getDefaultMessage());
			}*/
			List<String> errors=result.getFieldErrors()
					.stream()
					.map(err->"El campo '"+err.getField()+"' "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("Errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			
			
		}
		
		try {
			clienteNew=clienteService.save(client);
		} catch (DataAccessException e) {
			response.put("mensaje","Error al insertar en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Registro de usuario exitoso");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
		
	}
	
	
	@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Validated @RequestBody cliente client, BindingResult result, @PathVariable Long id) {
		
		cliente clienteActual=clienteService.findById(id);
		cliente clienteUpdate=null;
		
		Map<String, Object>response=new HashMap<>();
		
		if(result.hasErrors()) {
			List<String> errors=result.getFieldErrors()
					.stream()
					.map(err->"El campo '"+err.getField()+"' "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("Errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			
			
		}
		
		if (clienteActual==null) {
			response.put("mensaje", "Error, no se pudo editar, el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
		clienteActual.setApellido(client.getApellido());
		clienteActual.setEmail(client.getEmail());
		clienteActual.setNombre(client.getNombre());
		clienteActual.setCreateAt(client.getCreateAt());
		clienteActual.setRegion(client.getRegion());
		
		clienteUpdate=clienteService.save(clienteActual);
		
		} catch (DataAccessException e) {
			response.put("mensaje","Error al Actualizar en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Actualizacion de usuario exitoso");
		response.put("cliente", clienteUpdate);
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object>response=new HashMap<>();
		
		cliente clienteActual=clienteService.findById(id);
		if (clienteActual==null) {
			response.put("mensaje", "Error, no se pudo Eliminar, el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			String nombreFotoAnterior=clienteActual.getFoto();
			
			uploadService.eliminar(nombreFotoAnterior);
			
			clienteService.delete(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje","Error al Eliminar en la base de datos");
			response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente eliminado con exito!");
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
	}
	
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo,@RequestParam Long id){
		Map<String, Object>response=new HashMap<>();
		
		cliente client=clienteService.findById(id);
		
		if (!archivo.isEmpty()) {
			String nombreArchivo=null;
			try {
				nombreArchivo=uploadService.copiar(archivo);
			} catch (IOException e) {
				response.put("mensaje","Error al subir la imagen '"+nombreArchivo+"'");
				response.put("error",e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreFotoAnterior=client.getFoto();
			
			uploadService.eliminar(nombreFotoAnterior);
			
						
			client.setFoto(nombreArchivo);
			
			clienteService.save(client);
			
			response.put("Cliente", client);
			response.put("mensaje", "Has Subido Correctamente la imagen: "+nombreArchivo);
		}
		
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		
		Resource recurso=null;
		
		try {
			recurso=uploadService.cargar(nombreFoto);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		HttpHeaders cabecera=new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+recurso.getFilename()+"\"");
		
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
	}
	
	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones(){
		return clienteService.findAllRegiones();
	}

}
