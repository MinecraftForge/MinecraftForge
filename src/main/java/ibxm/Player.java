
package ibxm;

import java.io.*;
import javax.sound.sampled.*;

public class Player {
	private Thread play_thread;
	private IBXM ibxm;
	private Module module;
	private int song_duration, play_position;
	private boolean running, loop;
	private byte[] output_buffer;
	private SourceDataLine output_line;

	/**
		Simple command-line test player.
	*/
	public static void main( String[] args ) throws Exception {
		if( args.length < 1 ) {
			System.err.println( "Usage: java ibxm.Player <module file>" );
			System.exit( 0 );
		}
		FileInputStream file_input_stream = new FileInputStream( args[ 0 ] );
		Player player = new Player();
		player.set_module( Player.load_module( file_input_stream ) );
		file_input_stream.close();
		player.play();
	}
	
	/**
		Decode the data in the specified InputStream into a Module instance.
		@param input an InputStream containing the module file to be decoded.
		@throws IllegalArgumentException if the data is not recognised as a module file.
	*/
	public static Module load_module( InputStream input ) throws IllegalArgumentException, IOException {
		DataInputStream data_input_stream = new DataInputStream( input );
		/* Check if data is in XM format.*/
		byte[] xm_header = new byte[ 60 ];
		data_input_stream.readFully( xm_header );
		if( FastTracker2.is_xm( xm_header ) )
			return FastTracker2.load_xm( xm_header, data_input_stream );
		/* Check if data is in ScreamTracker 3 format.*/	
		byte[] s3m_header = new byte[ 96 ];
		System.arraycopy( xm_header, 0, s3m_header, 0, 60 );
		data_input_stream.readFully( s3m_header, 60, 36 );
		if( ScreamTracker3.is_s3m( s3m_header ) )
			return ScreamTracker3.load_s3m( s3m_header, data_input_stream );
		/* Check if data is in ProTracker format.*/
		byte[] mod_header = new byte[ 1084 ];
		System.arraycopy( s3m_header, 0, mod_header, 0, 96 );
		data_input_stream.readFully( mod_header, 96, 988 );
			return ProTracker.load_mod( mod_header, data_input_stream );
	}

	/**
		Instantiate a new Player.
	*/
	public Player() throws LineUnavailableException {
		ibxm = new IBXM( 48000 );
		set_loop( true );
		output_line = AudioSystem.getSourceDataLine( new AudioFormat( 48000, 16, 2, true, true ) );
		output_buffer = new byte[ 1024 * 4 ];
	}

	/**
		Set the Module instance to be played.
	*/
	public void set_module( Module m ) {
		if( m != null ) module = m;
		stop();
		ibxm.set_module( module );
		song_duration = ibxm.calculate_song_duration();
	}
	
	/**
		If loop is true, playback will continue indefinitely,
		otherwise the module will play through once and stop.
	*/
	public void set_loop( boolean loop ) {
		this.loop = loop;
	}
	
	/**
		Open the audio device and begin playback.
		If a module is already playing it will be restarted.
	*/
	public void play() {
		stop();
		play_thread = new Thread( new Driver() );
		play_thread.start();
	}
	
	/**
		Stop playback and close the audio device.
	*/
	public void stop() {
		running = false;
		if( play_thread != null ) {
			try {
				play_thread.join();
			} catch( InterruptedException ie ) {}
		}
	}
	
	private class Driver implements Runnable {
		@Override
        public void run() {
			if( running ) return;
			try {
				output_line.open();
				output_line.start();
				play_position = 0;
				running = true;
				while( running ) {
					int frames = song_duration - play_position;
					if( frames > 1024 ) frames = 1024;		
					ibxm.get_audio( output_buffer, frames );
					output_line.write( output_buffer, 0, frames * 4 );
					play_position += frames;
					if( play_position >= song_duration ) {
						play_position = 0;
						if( !loop ) running = false;
					}
				}
				output_line.drain();
				output_line.close();
			} catch( LineUnavailableException lue ) {
				lue.printStackTrace();
			}
		}
	}
}
