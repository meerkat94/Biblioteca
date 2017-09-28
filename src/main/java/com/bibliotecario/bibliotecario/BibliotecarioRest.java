package com.bibliotecario.bibliotecario;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.Prestamo;
import dominio.SolicitudPrestamo;
import dominio.reglas.ReglasPrestamo;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

@EnableAutoConfiguration
@Transactional
@RestController
public class BibliotecarioRest {

	@Autowired
	Bibliotecario bibliotecario;
	@Autowired
	ReglasPrestamo reglas;	
	@Autowired
	RepositorioPrestamo repositorioPrestamo;
	@Autowired
	RepositorioLibro repositorioLibros;
	
	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "biblioteca";
	}

	
	@RequestMapping(value = "/prestar", method = RequestMethod.POST)
	@ResponseBody
	public Prestamo servicioPrestar(@RequestBody SolicitudPrestamo solicitud) {
		bibliotecario.prestar(solicitud.isbn,solicitud.getNombreUsuario());
		return(repositorioPrestamo.obtener(solicitud.getIsbn()));
		
		
	}

	@RequestMapping(value = "/ListarLibros", method = RequestMethod.GET)
	@ResponseBody
	public List<Libro> servicioListarLibros() {
		return repositorioLibros.obtenerListaLibros();

	}

	@RequestMapping(value = "/ListarPrestamos", method = RequestMethod.GET)
	@ResponseBody
	public List<Prestamo> servicioListarPrestamos() {
		return repositorioPrestamo.obtenerListaPrestamos();

	}
	
	@RequestMapping(value = "/crearlibro", method = RequestMethod.POST)
	@ResponseBody
	public Libro crearLibroServicio(@RequestBody Libro libro) {		
		repositorioLibros.agregar(libro);		
		return libro;
	}

}
