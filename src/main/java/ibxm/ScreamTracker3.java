
package ibxm;

import java.io.*;

public class ScreamTracker3 {
	private static final int[] effect_map = new int[] {
		0xFF,
		0x25, /* A: Set Speed.*/
		0x0B, /* B: Pattern Jump.*/
		0x0D, /* C: Pattern Break.*/
		0x0A, /* D: Volume Slide.*/
		0x02, /* E: Portamento Down.*/
		0x01, /* F: Portamento Up.*/
		0x03, /* G: Tone Portamento.*/
		0x04, /* H: Vibrato.*/
		0x1D, /* I: Tremor.*/
		0x00, /* J: Arpeggio.*/
		0x06, /* K: Vibrato + Volume Slide.*/
		0x05, /* L: Tone Portamento + Volume Slide.*/
		0xFF, /* M: */
		0xFF, /* N: */
		0x09, /* O: Sample Offset.*/
		0xFF, /* P: */
		0x1B, /* Q: Retrig + Volume Slide.*/
		0x07, /* R: Tremolo.*/
		0x0E, /* S: Extended Effects.*/
		0x0F, /* T: Set Tempo.*/
		0x24, /* U: Fine Vibrato.*/
		0x10, /* V: Set Global Volume. */
		0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
		0xFF, 0xFF, 0xFF, 0xFF
	};

	private static final int[] effect_s_map = new int[] {
		0x00, /* 0: Set Filter.*/
		0x03, /* 1: Glissando.*/
		0x05, /* 2: Set Fine Tune.*/
		0x04, /* 3: Set Vibrato Waveform.*/
		0x07, /* 4: Set Tremolo Waveform.*/
		0xFF, /* 5: */
		0xFF, /* 6: */
		0xFF, /* 7: */
		0x08, /* 8: Set Panning.*/
		0xFF, /* 9: */
		0x09, /* A: Stereo Control.*/
		0x06, /* B: Pattern Loop.*/
		0x0C, /* C: Note Cut.*/
		0x0D, /* D: Note Delay.*/
		0x0E, /* E: Pattern Delay.*/
		0x0F  /* F: Invert Loop.*/
	};

	public static boolean is_s3m( byte[] header_96_bytes ) {
		String s3m_identifier;
		s3m_identifier = ascii_text( header_96_bytes, 44, 4 );
		return s3m_identifier.equals( "SCRM" );
	}

	public static Module load_s3m( byte[] header_96_bytes, DataInput data_input ) throws IOException {
		int num_pattern_orders, num_instruments, num_patterns, num_channels;
		int flags, tracker_version, master_volume, panning, channel_config, sequence_length;
		int instrument_idx, pattern_idx, channel_idx, order_idx, panning_offset;
		boolean signed_samples, stereo_mode, default_panning;
		int[] channel_map, sequence;
		byte[] s3m_file;
		Module module;
		Instrument instrument;
		s3m_file = read_s3m_file( header_96_bytes, data_input );
		module = new Module();
		module.song_title = ascii_text( s3m_file, 0, 28 );
		num_pattern_orders = get_num_pattern_orders( s3m_file );
		num_instruments = get_num_instruments( s3m_file );
		num_patterns = get_num_patterns( s3m_file );
		flags = unsigned_short_le( s3m_file, 38 );
		tracker_version = unsigned_short_le( s3m_file, 40 );
		if( ( flags & 0x40 ) == 0x40 || tracker_version == 0x1300 ) {
			module.fast_volume_slides = true;
		}
		signed_samples = false;
		if( unsigned_short_le( s3m_file, 42 ) == 0x01 ) {
			signed_samples = true;
		}
		module.global_volume = s3m_file[ 48 ] & 0xFF;
		module.default_speed = s3m_file[ 49 ] & 0xFF;
		module.default_tempo = s3m_file[ 50 ] & 0xFF;
		master_volume = s3m_file[ 51 ] & 0x7F;
		module.channel_gain = ( master_volume << IBXM.FP_SHIFT ) >> 7;
		stereo_mode = ( s3m_file[ 51 ] & 0x80 ) == 0x80;
		default_panning = ( s3m_file[ 53 ] & 0xFF ) == 0xFC;
		channel_map = new int[ 32 ];
		num_channels = 0;
		for( channel_idx = 0; channel_idx < 32; channel_idx++ ) {
			channel_config = s3m_file[ 64 + channel_idx ] & 0xFF;
			channel_map[ channel_idx ] = -1;
			if( channel_config < 16 ) {
				channel_map[ channel_idx ] = num_channels;
				num_channels += 1;
			}
		}
		module.set_num_channels( num_channels );
		panning_offset = 96 + num_pattern_orders + num_instruments * 2 + num_patterns * 2;
		for( channel_idx = 0; channel_idx < 32; channel_idx++ ) {
			if( channel_map[ channel_idx ] < 0 ) continue;
			panning = 7;
			if( stereo_mode ) {
				panning = 12;
				if( ( s3m_file[ 64 + channel_idx ] & 0xFF ) < 8 ) {
					panning = 3;
				}
			}
			if( default_panning ) {
				flags = s3m_file[ panning_offset + channel_idx ] & 0xFF;
				if( ( flags & 0x20 ) == 0x20 ) {
					panning = flags & 0xF;
				}
			}
			module.set_initial_panning( channel_map[ channel_idx ], panning * 17 );
		}
		sequence = read_s3m_sequence( s3m_file );
		module.set_sequence_length( sequence.length );
		for( order_idx = 0; order_idx < sequence.length; order_idx++ ) {
			module.set_sequence( order_idx, sequence[ order_idx ] );
		}
		module.set_num_instruments( num_instruments );
		for( instrument_idx = 0; instrument_idx < num_instruments; instrument_idx++ ) {
			instrument = read_s3m_instrument( s3m_file, instrument_idx, signed_samples );
			module.set_instrument( instrument_idx + 1, instrument );
		}
		module.set_num_patterns( num_patterns );
		for( pattern_idx = 0; pattern_idx < num_patterns; pattern_idx++ ) {
			module.set_pattern( pattern_idx, read_s3m_pattern( s3m_file, pattern_idx, channel_map ) );
		}
		return module;
	}

	private static int[] read_s3m_sequence( byte[] s3m_file ) {
		int num_pattern_orders, sequence_length;
		int sequence_idx, order_idx, pattern_order;
		int[] sequence;
		num_pattern_orders = get_num_pattern_orders( s3m_file );
		sequence_length = 0;
		for( order_idx = 0; order_idx < num_pattern_orders; order_idx++ ) {
			pattern_order = s3m_file[ 96 + order_idx ] & 0xFF;
			if( pattern_order == 255 ) {
				break;
			} else if( pattern_order < 254 ) {
				sequence_length += 1;
			}
		}
		sequence = new int[ sequence_length ];
		sequence_idx = 0;
		for( order_idx = 0; order_idx < num_pattern_orders; order_idx++ ) {
			pattern_order = s3m_file[ 96 + order_idx ] & 0xFF;
			if( pattern_order == 255 ) {
				break;
			} else if( pattern_order < 254 ) {
				sequence[ sequence_idx ] = pattern_order;
				sequence_idx += 1;
			}
		}
		return sequence;
	}

	private static Instrument read_s3m_instrument( byte[] s3m_file, int instrument_idx, boolean signed_samples ) {
		int instrument_offset;
		int sample_data_offset, sample_data_length;
		int loop_start, loop_length, c2_rate, sample_idx, amplitude;
		boolean sixteen_bit;
		Instrument instrument;
		Sample sample;
		short[] sample_data;
		instrument_offset = get_instrument_offset( s3m_file, instrument_idx );
		instrument = new Instrument();
		instrument.name = ascii_text( s3m_file, instrument_offset + 48, 28 );
		sample = new Sample();
		if( s3m_file[ instrument_offset ] == 1 ) {
			sample_data_length = get_sample_data_length( s3m_file, instrument_offset );
			loop_start = unsigned_short_le( s3m_file, instrument_offset + 20 );
			loop_length = unsigned_short_le( s3m_file, instrument_offset + 24 ) - loop_start;
			sample.volume = s3m_file[ instrument_offset + 28 ] & 0xFF;
			if( s3m_file[ instrument_offset + 30 ] != 0 ) {
				throw new IllegalArgumentException( "ScreamTracker3: Packed samples not supported!" );
			}
			if( ( s3m_file[ instrument_offset + 31 ] & 0x01 ) == 0 ) {
				loop_length = 0;
			}
			if( ( s3m_file[ instrument_offset + 31 ] & 0x02 ) != 0 ) {
				throw new IllegalArgumentException( "ScreamTracker3: Stereo samples not supported!" );
			}
			sixteen_bit = ( s3m_file[ instrument_offset + 31 ] & 0x04 ) != 0;
			c2_rate = unsigned_short_le( s3m_file, instrument_offset + 32 );
			sample.transpose = LogTable.log_2( c2_rate ) - LogTable.log_2( 8363 );
			sample_data_offset = get_sample_data_offset( s3m_file, instrument_offset );
			if( sixteen_bit ) {
				if( signed_samples ) {
					throw new IllegalArgumentException( "ScreamTracker3: Signed 16-bit samples not supported!" );
				}
				sample_data_length >>= 1;
				sample_data = new short[ sample_data_length ];
				for( sample_idx = 0; sample_idx < sample_data_length; sample_idx++ ) {
					amplitude  = s3m_file[ sample_data_offset + sample_idx * 2 ] & 0xFF;
					amplitude |= ( s3m_file[ sample_data_offset + sample_idx * 2 + 1 ] & 0xFF ) << 8;
					sample_data[ sample_idx ] = ( short ) ( amplitude - 32768 );
				}
			} else {
				sample_data = new short[ sample_data_length ];
				if( signed_samples ) {
					for( sample_idx = 0; sample_idx < sample_data_length; sample_idx++ ) {
						amplitude = s3m_file[ sample_data_offset + sample_idx ] << 8;
						sample_data[ sample_idx ] = ( short ) amplitude;
					}
				} else {
					for( sample_idx = 0; sample_idx < sample_data_length; sample_idx++ ) {
						amplitude = ( s3m_file[ sample_data_offset + sample_idx ] & 0xFF ) << 8;
						sample_data[ sample_idx ] = ( short ) ( amplitude - 32768 );
					}
				}
			}
			sample.set_sample_data( sample_data, loop_start, loop_length, false );
		}
		instrument.set_num_samples( 1 );
		instrument.set_sample( 0, sample );
		return instrument;
	}
	
	private static Pattern read_s3m_pattern( byte[] s3m_file, int pattern_idx, int[] channel_map ) {
		int pattern_offset;
		int num_channels, num_notes;
		int row_idx, channel_idx, note_idx;
		int token, key, volume_column, effect, effect_param;
		byte[] pattern_data;
		Pattern pattern;
		num_channels = 0;
		for( channel_idx = 0; channel_idx < 32; channel_idx++ ) {
			if( channel_map[ channel_idx ] >= num_channels ) {
				num_channels = channel_idx + 1;
			}
		}
		num_notes = num_channels * 64;
		pattern_data = new byte[ num_notes * 5 ];
		row_idx = 0;
		pattern_offset = get_pattern_offset( s3m_file, pattern_idx ) + 2;
		while( row_idx < 64 ) {
			token = s3m_file[ pattern_offset ] & 0xFF;
			pattern_offset += 1;
			if( token > 0 ) {
				channel_idx = channel_map[ token & 0x1F ];
				note_idx = ( num_channels * row_idx + channel_idx ) * 5;
				if( ( token & 0x20 ) == 0x20 ) {
					/* Key + Instrument.*/
					if( channel_idx >= 0 ) {
						key = s3m_file[ pattern_offset ] & 0xFF;
						if( key == 255 ) {
							key = 0;
						} else if( key == 254 ) {
							key = 97;
						} else {
							key = ( ( key & 0xF0 ) >> 4 ) * 12 + ( key & 0x0F ) + 1;
							while( key > 96 ) {
								key = key - 12;
							}
						}
						pattern_data[ note_idx ] = ( byte ) key;
						pattern_data[ note_idx + 1 ] = s3m_file[ pattern_offset + 1 ];
					}
					pattern_offset += 2;
				}
				if( ( token & 0x40 ) == 0x40 ) {
					/* Volume.*/
					if( channel_idx >= 0 ) {
						volume_column = ( s3m_file[ pattern_offset ] & 0xFF ) + 0x10;
						pattern_data[ note_idx + 2 ] = ( byte ) volume_column;
					}
					pattern_offset += 1;
				}
				if( ( token & 0x80 ) == 0x80 ) {
					/* Effect + Param.*/
					if( channel_idx >= 0 ) {
						effect = s3m_file[ pattern_offset ] & 0xFF;
						effect_param = s3m_file[ pattern_offset + 1 ] & 0xFF;
						effect = effect_map[ effect & 0x1F ];
						if( effect == 0xFF ) {
							effect = 0;
							effect_param = 0;
						}
						if( effect == 0x0E ) {
							effect = effect_s_map[ ( effect_param & 0xF0 ) >> 4 ];
							effect_param = effect_param & 0x0F;
							switch( effect ) {
								case 0x08:
									effect = 0x08;
									effect_param = effect_param * 17;
									break;
								case 0x09:
									effect = 0x08;
									if( effect_param > 7 ) {
										effect_param -= 8;
									} else {
										effect_param += 8;
									}
									effect_param = effect_param * 17;
									break;
								case 0xFF:
									effect = 0;
									effect_param = 0;
									break;
								default:
									effect_param = ( ( effect & 0x0F ) << 4 ) | ( effect_param & 0x0F );
									effect = 0x0E;
									break;
							}
						}
						pattern_data[ note_idx + 3 ] = ( byte ) effect;
						pattern_data[ note_idx + 4 ] = ( byte ) effect_param;
					}
					pattern_offset += 2;
				}
			} else {
				row_idx += 1;
			}
		}
		pattern = new Pattern();
		pattern.num_rows = 64;
		pattern.set_pattern_data( pattern_data );
		return pattern;
	}

	private static byte[] read_s3m_file( byte[] header_96_bytes, DataInput data_input ) throws IOException {
		int s3m_file_length;
		int num_pattern_orders, num_instruments, num_patterns;
		int instrument_idx, pattern_idx;
		int instrument_offset, sample_data_offset, pattern_offset;
		byte[] s3m_file;
		if( !is_s3m( header_96_bytes ) ) {
			throw new IllegalArgumentException( "ScreamTracker3: Not an S3M file!" );
		}
		s3m_file = header_96_bytes;
		s3m_file_length = header_96_bytes.length;
		num_pattern_orders = get_num_pattern_orders( s3m_file );
		num_instruments = get_num_instruments( s3m_file );
		num_patterns = get_num_patterns( s3m_file );
		s3m_file_length += num_pattern_orders;
		s3m_file_length += num_instruments * 2;
		s3m_file_length += num_patterns * 2;
		/* Read enough of file to calculate the length.*/
		s3m_file = read_more( s3m_file, s3m_file_length, data_input );
		for( instrument_idx = 0; instrument_idx < num_instruments; instrument_idx++ ) {
			instrument_offset = get_instrument_offset( s3m_file, instrument_idx );
			instrument_offset += 80;
			if( instrument_offset > s3m_file_length ) {
				s3m_file_length = instrument_offset;
			}
		}
		for( pattern_idx = 0; pattern_idx < num_patterns; pattern_idx++ ) {
			pattern_offset = get_pattern_offset( s3m_file, pattern_idx );
			pattern_offset += 2;
			if( pattern_offset > s3m_file_length ) {
				s3m_file_length = pattern_offset;
			}
		}
		s3m_file = read_more( s3m_file, s3m_file_length, data_input );
		/* Read rest of file.*/
		for( instrument_idx = 0; instrument_idx < num_instruments; instrument_idx++ ) {
			instrument_offset = get_instrument_offset( s3m_file, instrument_idx );
			sample_data_offset = get_sample_data_offset( s3m_file, instrument_offset );
			sample_data_offset += get_sample_data_length( s3m_file, instrument_offset );
			if( sample_data_offset > s3m_file_length ) {
				s3m_file_length = sample_data_offset;
			}
		}
		for( pattern_idx = 0; pattern_idx < num_patterns; pattern_idx++ ) {
			pattern_offset = get_pattern_offset( s3m_file, pattern_idx );
			pattern_offset += get_pattern_length( s3m_file, pattern_offset );
			pattern_offset += 2;
			if( pattern_offset > s3m_file_length ) {
				s3m_file_length = pattern_offset;
			}
		}
		s3m_file = read_more( s3m_file, s3m_file_length, data_input );
		return s3m_file;
	}

	private static int get_num_pattern_orders( byte[] s3m_file ) {
		int num_pattern_orders;
		num_pattern_orders = unsigned_short_le( s3m_file, 32 );
		return num_pattern_orders;
	}

	private static int get_num_instruments( byte[] s3m_file ) {
		int num_instruments;
		num_instruments = unsigned_short_le( s3m_file, 34 );
		return num_instruments;
	}
	
	private static int get_num_patterns( byte[] s3m_file ) {
		int num_patterns;
		num_patterns = unsigned_short_le( s3m_file, 36 );
		return num_patterns;
	}

	private static int get_instrument_offset( byte[] s3m_file, int instrument_idx ) {
		int instrument_offset, pointer_offset;
		pointer_offset = 96 + get_num_pattern_orders( s3m_file );
		instrument_offset = unsigned_short_le( s3m_file, pointer_offset + instrument_idx * 2 ) << 4;
		return instrument_offset;
	}

	private static int get_sample_data_offset( byte[] s3m_file, int instrument_offset ) {
		int sample_data_offset;
		sample_data_offset = 0;
		if( s3m_file[ instrument_offset ] == 1 ) {
			sample_data_offset = ( s3m_file[ instrument_offset + 13 ] & 0xFF ) << 20;
			sample_data_offset |= unsigned_short_le( s3m_file, instrument_offset + 14 ) << 4;
		}
		return sample_data_offset;
	}

	private static int get_sample_data_length( byte[] s3m_file, int instrument_offset ) {
		int sample_data_length;
		boolean sixteen_bit;
		sample_data_length = 0;
		if( s3m_file[ instrument_offset ] == 1 ) {
			sample_data_length = unsigned_short_le( s3m_file, instrument_offset + 16 );
			sixteen_bit = ( s3m_file[ instrument_offset + 31 ] & 0x04 ) != 0;
			if( sixteen_bit ) {
				sample_data_length <<= 1;
			}
		}
		return sample_data_length;
	}

	private static int get_pattern_offset( byte[] s3m_file, int pattern_idx ) {
		int pattern_offset, pointer_offset;
		pointer_offset = 96 + get_num_pattern_orders( s3m_file );
		pointer_offset += get_num_instruments( s3m_file ) * 2;
		pattern_offset = unsigned_short_le( s3m_file, pointer_offset + pattern_idx * 2 ) << 4;
		return pattern_offset;
	}

	private static int get_pattern_length( byte[] s3m_file, int pattern_offset ) {
		int pattern_length;
		pattern_length = unsigned_short_le( s3m_file, pattern_offset );
		return pattern_length;
	}

	private static byte[] read_more( byte[] old_data, int new_length, DataInput data_input ) throws IOException {
		byte[] new_data;
		new_data = old_data;
		if( new_length > old_data.length ) {
			new_data = new byte[ new_length ];
			System.arraycopy( old_data, 0, new_data, 0, old_data.length );
			try {
				data_input.readFully( new_data, old_data.length, new_data.length - old_data.length );
			} catch( EOFException e ) {
				System.out.println( "ScreamTracker3: Module has been truncated!" );
			}
		}
		return new_data;
	}

	private static int unsigned_short_le( byte[] buffer, int offset ) {
		int value;
		value = buffer[ offset ] & 0xFF;
		value = value | ( ( buffer[ offset + 1 ] & 0xFF ) << 8 );
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

