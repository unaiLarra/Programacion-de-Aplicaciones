package ud.prog3.pr01;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

/** Ventana principal de reproductor de vídeo
 * Utiliza la librería VLCj que debe estar instalada y configurada
 *     (http://www.capricasoftware.co.uk/projects/vlcj/index.html)
 * @author Andoni Eguíluz Morán
 * Facultad de Ingeniería - Universidad de Deusto
 */
public class VideoPlayer extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// Varible de ventana principal de la clase
	private static VideoPlayer miVentana;

	// Atributo de VLCj
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	// Atributos manipulables de swing
	private JList<String> lCanciones = null;  // Lista vertical de vídeos del player
	private JProgressBar pbVideo = null;      // Barra de progreso del vídeo en curso
	private JCheckBox cbAleatorio = null;     // Checkbox de reproducción aleatoria
	private JLabel lMensaje = null;           // Label para mensaje de reproducción
	// Datos asociados a la ventana
	private ListaDeReproduccion listaRepVideos;  // Modelo para la lista de vídeos
	

	public VideoPlayer() {
		// Creación de datos asociados a la ventana (lista de reproducción)
		listaRepVideos = new ListaDeReproduccion();
		
		// Creación de componentes/contenedores de swing
		lCanciones = new JList<String>( listaRepVideos );
		pbVideo = new JProgressBar( 0, 10000 );
		cbAleatorio = new JCheckBox("Rep. aleatoria");
		lMensaje = new JLabel( "" );
		JPanel pBotonera = new JPanel();
		JButton bAnyadir = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Add.png")) );
		JButton bAtras = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Rewind.png")) );
		JButton bpausado = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Play Pause.png")) );
		JButton bAdelante = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Fast Forward.png")) );
		JButton bMaximizar = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Maximize.png")) );
		
		// Componente de VCLj
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

		// Configuración de componentes/contenedores
		setTitle("Video Player - Deusto Ingeniería");
		setLocationRelativeTo( null );  // Centra la ventana en la pantalla
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 800, 600 );
		lCanciones.setPreferredSize( new Dimension( 200,  500 ) );
		pBotonera.setLayout( new FlowLayout( FlowLayout.LEFT ));
		
		// Enlace de componentes y contenedores
		pBotonera.add( bAnyadir );
		pBotonera.add( bAtras );
		pBotonera.add( bpausado );
		pBotonera.add( bAdelante );
		pBotonera.add( bMaximizar );
		pBotonera.add( cbAleatorio );
		pBotonera.add( lMensaje );
		getContentPane().add( mediaPlayerComponent, BorderLayout.CENTER );
		getContentPane().add( pBotonera, BorderLayout.NORTH );
		getContentPane().add( pbVideo, BorderLayout.SOUTH );
		getContentPane().add( new JScrollPane( lCanciones ), BorderLayout.WEST );
		
		// Escuchadores
		
		bAnyadir.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fPath = pedirCarpeta();
				if (fPath==null) return;
				path = fPath.getAbsolutePath();
				// TODO: pedir ficheros por ventana de entrada (JOptionPane)
				// ficheros = ...
				
				JFileChooser fileChooser = new JFileChooser();
				int respuesta = fileChooser.showOpenDialog(null); 

				if (respuesta == JFileChooser.APPROVE_OPTION) {
					ficheros = fileChooser.getSelectedFile().getAbsolutePath();
				}
				
				listaRepVideos.add( path, ficheros );
				lCanciones.repaint();
			}
		});
		bAtras.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paraVideo();
				if (cbAleatorio.isSelected()) {
					listaRepVideos.irARandom();
				} else {
					listaRepVideos.irAAnterior();
				}
				lanzaVideo();
			}
		});
		bAdelante.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paraVideo();
				if (cbAleatorio.isSelected()) {
					listaRepVideos.irARandom();
				} else {
					listaRepVideos.irASiguiente();
				}
				lanzaVideo();
			}
		});
		bpausado.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponent.mediaPlayer().status().isPlayable()) {
					if (mediaPlayerComponent.mediaPlayer().status().isPlaying()) {
		                mediaPlayerComponent.mediaPlayer().controls().setPause(true);
					} else {
		                mediaPlayerComponent.mediaPlayer().controls().setPause(false);
					}
				} else {
					lanzaVideo();
				}
			}
		});
		bMaximizar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponent.mediaPlayer().fullScreen().isFullScreen())
			        mediaPlayerComponent.mediaPlayer().fullScreen().set(false);
				else
					mediaPlayerComponent.mediaPlayer().fullScreen().set(true);
			}
		});
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mediaPlayerComponent.mediaPlayer().controls().stop();
				mediaPlayerComponent.mediaPlayer().release();
			}
		});
		mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener( 
			new MediaPlayerEventAdapter() {
				@Override
				public void finished(MediaPlayer mediaPlayer) {
					listaRepVideos.irASiguiente();
					lanzaVideo();
				}
				@Override
				public void error(MediaPlayer mediaPlayer) {
					listaRepVideos.irASiguiente();
					lanzaVideo();
					lCanciones.repaint();
				}
			    @Override
			    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
					pbVideo.setValue( (int) ( 10000.0 * 
							mediaPlayerComponent.mediaPlayer().status().time() /
							mediaPlayerComponent.mediaPlayer().status().length() ) );
					pbVideo.repaint();
			    }
		});
	}

	//
	// Métodos sobre el player de vídeo
	//
	
	// Para la reproducción del vídeo en curso
	private void paraVideo() {
		if (mediaPlayerComponent.mediaPlayer()!=null)
			mediaPlayerComponent.mediaPlayer().controls().stop();
	}

	// Empieza a reproducir el vídeo en curso de la lista de reproducción
	private void lanzaVideo() {
		if (mediaPlayerComponent.mediaPlayer()!=null &&
			listaRepVideos.getFicSeleccionado()!=-1) {
			
			File ficVideo = listaRepVideos.getFic(listaRepVideos.getFicSeleccionado());
			mediaPlayerComponent.mediaPlayer().media().play(  
				ficVideo.getAbsolutePath() );
			lCanciones.setSelectedIndex( listaRepVideos.getFicSeleccionado() );
			
			Date fecha = new Date(ficVideo.lastModified());
			SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/Y HH:mm");
			String fechaString = dateForm.format(fecha);
			lMensaje.setText("Ultima modificación del archivo: " + fechaString);
			
		} else {
			lCanciones.setSelectedIndices( new int[] {} );
		}
	}
	
	// Pide interactivamente una carpeta para coger vídeos
	// (null si no se selecciona)
	private static File pedirCarpeta() {
		File carpeta = new File("");
		
		JFileChooser fileChooser = new JFileChooser();
		int respuesta = fileChooser.showOpenDialog(null); 

		if (respuesta == JFileChooser.APPROVE_OPTION) {
			carpeta = new File(fileChooser.getSelectedFile().getAbsolutePath());
			return carpeta;
		}
		
		return null;
	}

		private static String ficheros;
		private static String path;
	/** Ejecuta una ventana de VideoPlayer.
	 * El path de VLC debe estar en la variable de entorno "vlc".
	 * Comprobar que la versión de 32/64 bits de Java y de VLC es compatible.
	 * @param args	Un array de dos strings. El primero es el nombre (con comodines) de los ficheros,
	 * 				el segundo el path donde encontrarlos.  Si no se suministran, se piden de forma interactiva. 
	 */
	public static void main(String[] args) {
		// Para probar carga interactiva descomentar o comentar la línea siguiente:
		args = new String[] { "*Pentatonix*.mp4", "test/res/" };
		if (args.length < 2) {
			// No hay argumentos: selección manual
			File fPath = pedirCarpeta();
			if (fPath==null) return;
			path = fPath.getAbsolutePath();
			// TODO : Petición manual de ficheros con comodines (showInputDialog)
			// ficheros = ???
		} else {
			ficheros = args[0];
			path = args[1];
		}
		
		// Inicializar VLC.
		// Probar con el buscador nativo...
		boolean found = new NativeDiscovery().discover();
    	// System.out.println( LibVlc.INSTANCE.libvlc_get_version() );  // Visualiza versión de VLC encontrada
    	// Si no se encuentra probar otras opciones:
    	if (!found) {
			// Buscar vlc como variable de entorno
			String vlcPath = System.getenv().get( "vlc" );
			if (vlcPath==null) {  // Poner VLC a mano
	        	System.setProperty("C:\\Users\\ularr\\OneDrive - Universidad de Deusto\\Deusto\\AÑO 2\\SEMESTRE 1\\PROGRAMACION DE APLICACIONES\\Librerias\\vlcj\\vlcj-4.7.1.jar", "C:\\Program Files\\VideoLAN");
			} else {  // Poner VLC desde la variable de entorno
				System.setProperty( "C:\\Users\\ularr\\OneDrive - Universidad de Deusto\\Deusto\\AÑO 2\\SEMESTRE 1\\PROGRAMACION DE APLICACIONES\\Librerias\\vlcj\\vlcj-4.7.1.jar", vlcPath );
			}
		}
    	
    	// Lanzar ventana
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				miVentana = new VideoPlayer();
				// Descomentar esta línea y poner una ruta válida para ver un vídeo de ejemplo
				miVentana.listaRepVideos.add( new File("test/res/Official Video Daft Punk - Pentatonix.mp4") );
				miVentana.setVisible( true );
				miVentana.listaRepVideos.add( path, ficheros );
				miVentana.listaRepVideos.irAUltimo();
				miVentana.lanzaVideo();
			}
		});
	}
	
}
