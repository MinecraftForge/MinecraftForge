package ibxm;

import java.io.*;

public class ProTracker {
	public static boolean is_mod( byte[] header_1084_bytes ) {
		boolean is_mod;
		is_mod = false;
		if( calculate_num_channels( header_1084_bytes ) > 0 ) {
			is_mod = true;
		}
		return is_mod;
	}

	public static Module load_mod( byte[] header_1084_bytes, DataInput data_input ) throws IOException {
		int num_channels, channel_idx, panning;
		int sequence_length, restart_idx, sequence_idx;
		int num_patterns, pattern_idx, instrument_idx;
		Module module;
		num_channels = calculate_num_channels( header_1084_bytes );
		if( num_channels < 1 ) {
			throw new IllegalArgumentException( "ProTracker: Unrecognised module format!" );
		}
		module = new Module();
		module.song_title = ascii_text( header_1084_bytes, 0, 20 );
		module.pal = ( num_channels == 4 );
		module.global_volume = 64;
		module.channel_gain = IBXM.FP_ONE * 3 / 8;
		module.default_speed = 6;
		module.default_tempo = 125;
		module.set_num_channels( num_channels );
		for( channel_idx = 0; channel_idx < num_channels; channel_idx++ ) {
			panning = 64;
			if( ( channel_idx & 0x03 ) == 0x01 || ( channel_idx & 0x03 ) == 0x02 ) {
				panning = 192;
			}
			module.set_initial_panning( channel_idx, panning );
		}
		sequence_length = header_1084_bytes[ 950 ] & 0x7F;
		restart_idx = header_1084_bytes[ 951 ] & 0x7F;
		if( restart_idx >= sequence_length ) {
			restart_idx = 0;
		}
		module.restart_sequence_index = restart_idx;
		module.set_sequence_length( sequence_length );
		for( sequence_idx = 0; sequence_idx < sequence_length; sequence_idx++ ) {
			module.set_sequence( sequence_idx, header_1084_bytes[ 952 + sequence_idx ] & 0x7F );
		}
		num_patterns = calculate_num_patterns( header_1084_bytes );
		module.set_num_patterns( num_patterns );
		for( pattern_idx = 0; pattern_idx < num_patterns; pattern_idx++ ) {
			module.set_pattern( pattern_idx, read_mod_pattern( data_input, num_channels ) );
		}
		module.set_num_instruments( 31 );
		for( instrument_idx = 1; instrument_idx <= 31; instrument_idx++ ) {
			module.set_instrument( instrument_idx, read_mod_instrument( header_1084_bytes, instrument_idx, data_input ) );
		}
		return module;
	}

	private static int calculate_num_patterns( byte[] module_header ) {
		int num_patterns, order_entry, pattern_idx;
		num_patterns = 0;
		for( pattern_idx = 0; pattern_idx < 128; pattern_idx++ ) {
			order_entry = module_header[ 952 + pattern_idx ] & 0x7F;
			if( order_entry >= num_patterns ) {
				num_patterns = order_entry + 1;
			}
		}
		return num_patterns;
	}
	
	private static int calculate_num_channels( byte[] module_header ) {
		int num_channels;
		switch( ( module_header[ 1082 ] << 8 ) | module_header[ 1083 ] ) {
			case 0x4b2e: /* M.K. */
			case 0x4b21: /* M!K! */
			case 0x542e: /* N.T. */
			case 0x5434: /* FLT4 */
				num_channels = 4;
				break;
			case 0x484e: /* xCHN */
				num_channels = module_header[ 1080 ] - 48;
				break;
			case 0x4348: /* xxCH */
				num_channels = ( ( module_header[ 1080 ] - 48 ) * 10 ) + ( module_header[ 1081 ] - 48 );
				break;
			default:
				/* Not recognised. */
				num_channels = 0;
				break;
		}
		return num_channels;
	}

	private static Pattern read_mod_pattern( DataInput data_input, int num_channels ) throws IOException {
		int input_idx, output_idx;
		int period, instrument, effect, effect_param;
		Pattern pattern;
		byte[] input_pattern_data, output_pattern_data;
		pattern = new Pattern();
		pattern.num_rows = 64;
		input_pattern_data = new byte[ 64 * num_channels * 4 ];
		output_pattern_data = new byte[ 64 * num_channels * 5 ];
		data_input.readFully( input_pattern_data );
		input_idx = 0;
		output_idx = 0;
		while( input_idx < input_pattern_data.length ) {
			period = ( input_pattern_data[ input_idx ] & 0x0F ) << 8;
			period = period | ( input_pattern_data[ input_idx + 1 ] & 0xFF );
			output_pattern_data[ output_idx ] = to_key( period );
			instrument = input_pattern_data[ input_idx ] & 0x10;
			instrument = instrument | ( ( input_pattern_data[ input_idx + 2 ] & 0xF0 ) >> 4 );
			output_pattern_data[ output_idx + 1 ] = ( byte ) instrument;
			effect = input_pattern_data[ input_idx + 2 ] & 0x0F;
			effect_param = input_pattern_data[ input_idx + 3 ] & 0xFF;
			if( effect == 0x01 && effect_param == 0 ) {
				/* Portamento up of zero has no effect. */
				effect = 0;
			}
			if( effect == 0x02 && effect_param == 0 ) {
				/* Portamento down of zero has no effect. */
				effect = 0;
			}
			if( effect == 0x08 && num_channels == 4 ) {
				/* Some Amiga mods use effect 0x08 for reasons other than panning.*/
				effect = 0;
				effect_param = 0;
			}
			if( effect == 0x0A && effect_param == 0 ) {
				/* Volume slide of zero has no effect.*/
				effect = 0;
			}
			if( effect == 0x05 && effect_param == 0 ) {
				/* Porta + Volume slide of zero has no effect.*/
				effect = 0x03;
			}
			if( effect == 0x06 && effect_param == 0 ) {
				/* Vibrato + Volume slide of zero has no effect.*/
				effect = 0x04;
			}
			output_pattern_data[ output_idx + 3 ] = ( byte ) effect;
			output_pattern_data[ output_idx + 4 ] = ( byte ) effect_param;
			input_idx += 4;
			output_idx += 5;
		}
		pattern.set_pattern_data( output_pattern_data );
		return pattern;
	}

	private static Instrument read_mod_instrument( byte[] mod_header, int idx, DataInput data_input ) throws IOException  {
		int header_offset, sample_data_length;
		int loop_start, loop_length, sample_idx, fine_tune;
		Instrument instrument;
		Sample sample;
		byte[] raw_sample_data;
		short[] sample_data;
		header_offset = ( idx - 1 ) * 30 + 20;
		instrument = new Instrument();
		instrument.name = ascii_text( mod_header, header_offset, 22 );
		sample = new Sample();
		sample_data_length = unsigned_short_be( mod_header, header_offset + 22 ) << 1;
		fine_tune = mod_header[ header_offset + 24 ] & 0x0F;
		if( fine_tune > 7 ) {
			fine_tune -= 16;
		}
		sample.transpose = ( fine_tune << IBXM.FP_SHIFT ) / 96;
		sample.volume = mod_header[ header_offset + 25 ] & 0x7F;
		loop_start = unsigned_short_be( mod_header, header_offset + 26 ) << 1;
		loop_length = unsigned_short_be( mod_header, header_offset + 28 ) << 1;
		if( loop_length < 4 ) {
			loop_length = 0;
		}
		raw_sample_data = new byte[ sample_data_length ];
		sample_data = new short[ sample_data_length ];
		try {
			data_input.readFully( raw_sample_data );
		} catch( EOFException e ) {
			System.out.println( "ProTracker: Instrument " + idx + " has samples missing." );
		}
		for( sample_idx = 0; sample_idx < raw_sample_data.length; sample_idx++ ) {
			sample_data[ sample_idx ] = ( short ) ( raw_sample_data[ sample_idx ] << 8 );
		}
		sample.set_sample_data( sample_data, loop_start, loop_length, false );
		instrument.set_num_samples( 1 );
		instrument.set_sample( 0, sample );
		return instrument;
	}
	
	private static byte to_key( int period ) {
		int oct, key;
		if( period < 32 ) {
			key = 0;
		} else {
			oct = LogTable.log_2( 7256 ) - LogTable.log_2( period );
			if( oct < 0 ) {
				key = 0;
			} else {
				key = oct * 12;
				key = key >> ( IBXM.FP_SHIFT - 1 );
				key = ( key >> 1 ) + ( key & 1 );
			}
		}
		return ( byte ) key;
	}

	private static int unsigned_short_be( byte[] buf, int offset ) {
		int value;
		value = ( buf[ offset ] & 0xFF ) << 8;
		value = value | ( buf[ offset + 1 ] & 0xFF );
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

