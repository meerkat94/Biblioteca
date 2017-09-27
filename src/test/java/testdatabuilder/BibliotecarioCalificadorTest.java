package testdatabuilder;

import static org.junit.Assert.fail;
import java.text.ParseException;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bibliotecario.bibliotecario.BibliotecarioApplication;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.excepcion.PrestamoException;
import dominio.reglas.ReglaDiaDomingo;
import dominio.reglas.ReglasPrestamo;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=BibliotecarioApplication.class)
@DataJpaTest
public class BibliotecarioCalificadorTest {

	private static final String CRONICA_DE_UNA_MUERTA_ANUNCIADA = "Cronica de una muerta anunciada";
	
	@Autowired
	Bibliotecario bibliotecario;
	@Autowired
	private RepositorioLibro repositorioLibros;
	@Autowired
	private RepositorioPrestamo repositorioPrestamo;
	@Autowired
	public ReglasPrestamo reglas;
	


	@Test
	public void prestarLibroTest() throws ParseException {

		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).conIsbn("1234").build();
		repositorioLibros.agregar(libro);
		bibliotecario.prestar(libro.getIsbn(), "Juan");
        Assert.assertTrue(bibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));

	}
	

	@Test
	public void prestarLibroNoDisponibleTest() throws ParseException {
	
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);
		
		bibliotecario.prestar(libro.getIsbn(), "Juan");
		try {
			bibliotecario.prestar(libro.getIsbn(), "Juan");
			fail();

		} catch (PrestamoException e) {		
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE, e.getMessage());
		}
	}

	@Test
	public void prestarLibroPalindromoTest() throws ParseException {

		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).conIsbn("12421").build();
		repositorioLibros.agregar(libro);

		try {	
			bibliotecario.prestar(libro.getIsbn(), "Juan");
			fail();

		} catch (PrestamoException e) {	
			Assert.assertEquals("los libros palíndromos solo se pueden utilizar en la biblioteca", e.getMessage());
			Assert.assertNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn("1221"));
		}
	}

	@Test
	public void prestarLibroConUsuarioTest() throws ParseException {
	
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);
		bibliotecario.prestar(libro.getIsbn(), "Juan");
		
		Assert.assertTrue(bibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtener(libro.getIsbn()).getNombreUsuario());
		Assert.assertEquals("Juan", repositorioPrestamo.obtener(libro.getIsbn()).getNombreUsuario());

	}

	@Test
	public void prestarLibroConFechaEntregaTest() throws ParseException {

		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).conIsbn("58749841647").build();
		repositorioLibros.agregar(libro);		
		bibliotecario.prestar(libro.getIsbn(), "Juan");

		Assert.assertTrue(bibliotecario.esPrestado(libro.getIsbn()));
		Calendar fechaPrestamo = Calendar.getInstance();
		asignarTiempoCero(fechaPrestamo);
		Calendar fechaObtenida = repositorioPrestamo.obtener(libro.getIsbn()).getFechaEntregaMaxima();		
		asignarTiempoCero(fechaObtenida);
		
		Assert.assertEquals(CalificadorUtil.sumarDiasSinContarDomingos(fechaPrestamo, 15).getTime(),fechaObtenida.getTime());

	}

	@Test
	public void esDomingoTest() throws ParseException {

		ReglaDiaDomingo reglaDiaDomingo = new ReglaDiaDomingo();
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);

		reglaDiaDomingo.validar(libro.getIsbn());
		try {
			reglaDiaDomingo.validar(libro.getIsbn());

		} catch (PrestamoException e) {			
			Assert.assertEquals("los domingos no abre la biblioteca ", e.getMessage());
		}
	}

	private void asignarTiempoCero(Calendar fecha) {
		fecha.set(Calendar.HOUR, 0);
		fecha.set(Calendar.MILLISECOND, 0);
		fecha.set(Calendar.SECOND, 0);
		fecha.set(Calendar.MINUTE, 0);
	}

}
