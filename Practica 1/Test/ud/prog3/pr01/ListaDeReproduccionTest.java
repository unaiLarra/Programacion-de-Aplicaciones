package ud.prog3.pr01;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ListaDeReproduccionTest {
	private ListaDeReproduccion lr1;
	private ListaDeReproduccion lr2;
	private ListaDeReproduccion lr3;
	private final File FIC_TEST1 = new File( "test/res/No del grupo.mp4" );
	
	@Before
	public void setUp() throws Exception {
	lr1 = new ListaDeReproduccion();
	lr2 = new ListaDeReproduccion();
	lr3 = new ListaDeReproduccion();
	lr2.add( FIC_TEST1 );
	}
	
	@After
	 public void tearDown() {
	lr2.clear();
	lr3.clear();
	}
	
	
	@Test public void addCarpeta() {
	String carpetaTest = "test/res/";
	String filtroTest = "*Pentatonix*.mp4";
	ListaDeReproduccion lr = new ListaDeReproduccion();
	lr.add( carpetaTest, filtroTest );
	fail( "Método sin acabar" );
	}
	
	// Verificamos que la funcion de añadir ficheros a la lista fitra y añade bien
	@Test public void leerDirectorio() {
	assertEquals(3, lr3.add("\\Users\\ularr\\eclipse-workspace\\Practica 1\\test\\res", "*Pentatonix*.mp4"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGet_Exc1() {
	lr1.getFic(0); // Debe dar error porque aún no existe la posición 0
	} 
	
	// Chequeo de error por get(índice) por debajo de 0
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGet_Exc2() {
	lr2.getFic(-1); // Debe dar error porque aún no existe la posición -1
	}
	// Chequeo de funcionamiento correcto de get(índice)
	@Test public void testGet() {
	assertEquals( FIC_TEST1, lr2.getFic(0) ); // El único dato es el fic-test1
	}
}
