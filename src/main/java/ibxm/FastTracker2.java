
package ibxm;

import java.io.*;

public class FastTracker2 {
	public static boolean is_xm( byte[] header_60_bytes ) {
		String xm_identifier;
		xm_identifier = ascii_text( header_60_bytes, 0, 17 );
		return xm_identifier.equals( "Extended Module: " );
	}

	public static Module load_xm( byte[] header_60_bytes, DataInput data_input ) throws IOException {
		int xm_version, song_header_length, sequence_length;
		int num_channels, num_patterns, num_instruments, xm_flags, idx;
		byte[] structure_header, song_header;
		boolean delta_env;
		String tracker_name;
		Instrument instrument;
		Module module;
		if( !is_xm( header_60_bytes ) ) {
			throw new IllegalArgumentException( "Not an XM file!" );
		}
		xm_version = unsigned_short_le( header_60_bytes, 58 );
		if( xm_version != 0x0104 ) {
			throw new IllegalArgumentException( "Sorry, XM version " + xm_version + " is not supported!" );
		}
		module = new Module();
		module.song_title = ascii_text( header_60_bytes, 17, 20 );
		tracker_name = ascii_text( header_60_bytes, 38, 20 );
		delta_env = tracker_name.startsWith( "DigiBooster Pro" );
		structure_header = new byte[ 4 ];
		data_input.readFully( structure_header );
		song_header_length = int_le( structure_header, 0 );
		song_header = new byte[ song_header_length ];
		data_input.readFully( song_header, 4, song_header_length - 4 );
		sequence_length = unsigned_short_le( song_header, 4 );
		module.restart_sequence_index = unsigned_short_le( song_header, 6 );
		num_channels = unsigned_short_le( song_header, 8 );
		num_patterns = unsigned_short_le( song_header, 10 );
		num_instruments = unsigned_short_le( song_header, 12 );
		xm_flags = unsigned_short_le( song_header, 14 );
		module.linear_periods = ( xm_flags & 0x1 ) == 0x1;
		module.global_volume = 64;
		module.channel_gain = IBXM.FP_ONE * 3 / 8;
		module.default_speed = unsigned_short_le( song_header, 16 );
		module.default_tempo = unsigned_short_le( song_header, 18 );
		module.set_num_channels( num_channels );
		for( idx = 0; idx < num_channels; idx++ ) {
			module.set_initial_panning( idx, 128 );
		}
		module.set_sequence_length( sequence_length );		
		for( idx = 0; idx < sequence_length; idx++ ) {
			module.set_sequence( idx, song_header[ 20 + idx ] & 0xFF );
		}
		module.set_num_patterns( num_patterns );
		for( idx = 0; idx < num_patterns; idx++ ) {
			module.set_pattern( idx, read_xm_pattern( data_input, num_channels ) );
		}
		module.set_num_instruments( num_instruments );
		for( idx = 1; idx <= num_instruments; idx++ ) {
			try {
				instrument = read_xm_instrument( data_input, delta_env );
				module.set_instrument( idx, instrument );
			} catch( EOFException e ) {
				System.out.println( "Instrument " + idx + " is missing!" );
			}
		}
		return module;
	}

    private static Pattern read_xm_pattern( DataInput data_input, int num_channels ) throws IOException {
		@SuppressWarnings("unused") //Forge
		int pattern_header_length, packing_type, num_rows, pattern_data_length;
		byte[] structure_header, pattern_header, pattern_data;
		Pattern pattern;
		structure_header = new byte[ 4 ];
		data_input.readFully( structure_header );
		pattern_header_length = int_le( structure_header, 0 );
		pattern_header = new byte[ pattern_header_length ];
		data_input.readFully( pattern_header, 4, pattern_header_length - 4 );
		packing_type = pattern_header[ 4 ];
		if( packing_type != 0 ) {
			throw new IllegalArgumentException( "Pattern packing type " + packing_type + " is not supported!" );
		}
		pattern = new Pattern();
		pattern.num_rows = unsigned_short_le( pattern_header, 5 );
		pattern_data_length = unsigned_short_le( pattern_header, 7 );
		pattern_data = new byte[ pattern_data_length ];
		data_input.readFully( pattern_data );		
		pattern.set_pattern_data( pattern_data );
		return pattern;
	}
	
	private static Instrument read_xm_instrument( DataInput data_input, boolean delta_env ) throws IOException {
		int instrument_header_length, num_samples, idx;
		int env_tick, env_ampl, env_num_points, flags;
		byte[] structure_header, instrument_header, sample_headers;
		Instrument instrument;
		Envelope envelope;
		structure_header = new byte[ 4 ];
		data_input.readFully( structure_header );
		instrument_header_length = int_le( structure_header, 0 );
		instrument_header = new byte[ instrument_header_length ];
		data_input.readFully( instrument_header, 4, instrument_header_length - 4 );
		instrument = new Instrument();
		instrument.name = ascii_text( instrument_header, 4, 22 );
		num_samples = unsigned_short_le( instrument_header, 27 );
		if( num_samples > 0 ) {
			instrument.set_num_samples( num_samples );
			for( idx = 0; idx < 96; idx++ ) {
				instrument.set_key_to_sample( idx + 1, instrument_header[ 33 + idx ] & 0xFF );
			}
			envelope = new Envelope();
			env_num_points = instrument_header[ 225 ] & 0xFF;
			envelope.set_num_points( env_num_points );
			for( idx = 0; idx < env_num_points; idx++ ) {
				env_tick = unsigned_short_le( instrument_header, 129 + idx * 4 );
				env_ampl = unsigned_short_le( instrument_header, 131 + idx * 4 );
				envelope.set_point( idx, env_tick, env_ampl, delta_env );
			}
			envelope.set_sustain_point( instrument_header[ 227 ] & 0xFF );
			envelope.set_loop_points( instrument_header[ 228 ] & 0xFF, instrument_header[ 229 ] & 0xFF );
			flags = instrument_header[ 233 ] & 0xFF;
			instrument.volume_envelope_active = ( flags & 0x1 ) == 0x1;
			envelope.sustain = ( flags & 0x2 ) == 0x2;
			envelope.looped = ( flags & 0x4 ) == 0x4;
			instrument.set_volume_envelope( envelope );
			envelope = new Envelope();
			env_num_points = instrument_header[ 226 ] & 0xFF;
			envelope.set_num_points( env_num_points );
			for( idx = 0; idx < env_num_points; idx++ ) {
				env_tick = unsigned_short_le( instrument_header, 177 + idx * 4 );
				env_ampl = unsigned_short_le( instrument_header, 179 + idx * 4 );
				envelope.set_point( idx, env_tick, env_ampl, delta_env );
			}
			envelope.set_sustain_point( instrument_header[ 230 ] & 0xFF );
			envelope.set_loop_points( instrument_header[ 231 ] & 0xFF, instrument_header[ 232 ] & 0xFF );
			flags = instrument_header[ 234 ] & 0xFF;
			instrument.panning_envelope_active = ( flags & 0x1 ) == 0x1;
			envelope.sustain = ( flags & 0x2 ) == 0x2;
			envelope.looped = ( flags & 0x4 ) == 0x4;
			instrument.set_panning_envelope( envelope );
			instrument.vibrato_type = instrument_header[ 235 ] & 0xFF;
			instrument.vibrato_sweep = instrument_header[ 236 ] & 0xFF;
			instrument.vibrato_depth = instrument_header[ 237 ] & 0xFF;
			instrument.vibrato_rate = instrument_header[ 238 ] & 0xFF;
			instrument.volume_fade_out = unsigned_short_le( instrument_header, 239 );
			sample_headers = new byte[ num_samples * 40 ];
			data_input.readFully( sample_headers );
			for( idx = 0; idx < num_samples; idx++ ) {
				instrument.set_sample( idx, read_xm_sample( sample_headers, idx, data_input ) );
			}
		}
		return instrument;
	}

	private static Sample read_xm_sample( byte[] sample_headers, int sample_idx, DataInput data_input ) throws IOException {
		int header_offset, sample_length, loop_start, loop_length;
		int flags, in_idx, out_idx, sam, last_sam;
		int fine_tune, relative_note;
		boolean sixteen_bit, ping_pong;
		byte[] raw_sample_data;
		short[] decoded_sample_data;
		Sample sample;
		header_offset = sample_idx * 40;
		sample = new Sample();
		sample_length = int_le( sample_headers, header_offset );		
		loop_start = int_le( sample_headers, header_offset + 4 );
		loop_length = int_le( sample_headers, header_offset + 8 );
		sample.volume = sample_headers[ header_offset + 12 ] & 0xFF;
		fine_tune = sample_headers[ header_offset + 13 ];
		fine_tune = ( fine_tune << IBXM.FP_SHIFT ) / 1536;
		sample.set_panning = true;
		flags = sample_headers[ header_offset + 14 ] & 0xFF;
		if( ( flags & 0x03 ) == 0 ) {
			loop_length = 0;
		}
		ping_pong = ( flags & 0x02 ) == 0x02;
		sixteen_bit = ( flags & 0x10 ) == 0x10;
		sample.panning = sample_headers[ header_offset + 15 ] & 0xFF;
		relative_note = sample_headers[ header_offset + 16 ];
		relative_note = ( relative_note << IBXM.FP_SHIFT ) / 12;
		sample.transpose = relative_note + fine_tune;
		sample.name = ascii_text( sample_headers, header_offset + 18, 22 );
		raw_sample_data = new byte[ sample_length ];
		try {
			data_input.readFully( raw_sample_data );
		} catch( EOFException e ) {
			System.out.println( "Sample has been truncated!" );
		}
		in_idx = 0;
		out_idx = 0;
		sam = 0;
		last_sam = 0;
		if( sixteen_bit ) {
			decoded_sample_data = new short[ sample_length >> 1 ];
			while( in_idx < raw_sample_data.length ) {
				sam = raw_sample_data[ in_idx ] & 0xFF;
				sam = sam | ( ( raw_sample_data[ in_idx + 1 ] & 0xFF ) << 8 );
				last_sam = last_sam + sam;
				decoded_sample_data[ out_idx ] = ( short ) last_sam;
				in_idx += 2;
				out_idx += 1;
			}
			sample.set_sample_data( decoded_sample_data, loop_start >> 1, loop_length >> 1, ping_pong );
		} else {
			decoded_sample_data = new short[ sample_length ];
			while( in_idx < raw_sample_data.length ) {
				sam = raw_sample_data[ in_idx ] & 0xFF;
				last_sam = last_sam + sam;
				decoded_sample_data[ out_idx ] = ( short ) ( last_sam << 8 );
				in_idx += 1;
				out_idx += 1;
			}
			sample.set_sample_data( decoded_sample_data, loop_start, loop_length, ping_pong );
		}
		return sample;
	}

	private static int unsigned_short_le( byte[] buffer, int offset ) {
		int value;
		value = buffer[ offset ] & 0xFF;
		value = value | ( ( buffer[ offset + 1 ] & 0xFF ) << 8 );
		return value;
	}

	private static int int_le( byte[] buffer, int offset ) {
		int value;
		value = buffer[ offset ] & 0xFF;
		value = value | ( ( buffer[ offset + 1 ] & 0xFF ) << 8 );
		value = value | ( ( buffer[ offset + 2 ] & 0xFF ) << 16 );
		value = value | ( ( buffer[ offset + 3 ] & 0x7F ) << 24 );
		return value;
	}
	
	private static String ascii_text( byte[] buffer, int offset, int length ) {
		int idx, chr;
		byte[] string_buffer;
		String string;
		string_buffer = new byte[ length ];
		for( idx = 0; idx < length; idx++ ) {
			chr = buffer[ offset + idx ];
			if( chr < 32 ) {
				chr = 32;
			}
			string_buffer[ idx ] = ( byte ) chr;
		}
		try {
			string = new String( string_buffer, 0, length, "ISO-8859-1" );
		} catch( UnsupportedEncodingException e ) {
			string = "";
		}
		return string;
	}
}

