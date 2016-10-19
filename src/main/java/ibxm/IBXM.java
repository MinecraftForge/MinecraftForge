
package ibxm;

public class IBXM {
	public static final String VERSION = "ibxm alpha 51 (c)2008 mumart@gmail.com";

	public static final int FP_SHIFT = 15;
	public static final int FP_ONE = 1 << FP_SHIFT;
	public static final int FP_MASK = FP_ONE - 1;

	private int sampling_rate, resampling_quality, volume_ramp_length;
	private int tick_length_samples, current_tick_samples;
	private int[] mixing_buffer, volume_ramp_buffer;

	private Module module;
	private Channel[] channels;
	private int[] global_volume, note;
	private int current_sequence_index, next_sequence_index;
	private int current_row, next_row;
	private int tick_counter, ticks_per_row;
	private int pattern_loop_count, pattern_loop_channel;

	public IBXM( int sample_rate ) {
        
/** MODIFIED 13 Oct 2009 by Paul Lamb **/
//		System.out.println( VERSION );
/***************************************/

		if( sample_rate < 8000 ) {
			sample_rate = 8000;
		}
		sampling_rate = sample_rate;
		volume_ramp_length = sampling_rate >> 10;
		volume_ramp_buffer = new int[ volume_ramp_length * 2 ];
		mixing_buffer = new int[ sampling_rate / 6 ];
		global_volume = new int[ 1 ];
		note = new int[ 5 ];
		set_module( new Module() );
		set_resampling_quality( 1 );
	}
	
	public void set_module( Module m ) {
		int channel_idx;
		module = m;
		channels = new Channel[ module.get_num_channels() ];
		for( channel_idx = 0; channel_idx < channels.length; channel_idx++ ) {
			channels[ channel_idx ] = new Channel( module, sampling_rate, global_volume );
		}
		set_sequence_index( 0, 0 );
	}

	public void set_resampling_quality( int quality ) {
		resampling_quality = quality;
	}
	
	public int calculate_song_duration() {
		int song_duration;
		set_sequence_index( 0, 0 );
		next_tick();
		song_duration = tick_length_samples;
		while( !next_tick() ) {
			song_duration += tick_length_samples;
		}
		set_sequence_index( 0, 0 );
		return song_duration;
	}
	
	public void set_sequence_index( int sequence_index, int row ) {
		int channel_idx;
		global_volume[ 0 ] = 64;
		for( channel_idx = 0; channel_idx < channels.length; channel_idx++ ) {
			channels[ channel_idx ].reset();
			channels[ channel_idx ].set_panning( module.get_initial_panning( channel_idx ) );
		}
		set_global_volume( module.global_volume );
		set_speed( 6 );
		set_speed( module.default_speed );
		set_tempo( 125 );
		set_tempo( module.default_tempo );
		pattern_loop_count = -1;
		next_sequence_index = sequence_index;
		next_row = row;
		tick_counter = 0;
		current_tick_samples = tick_length_samples;
		clear_vol_ramp_buffer();
	}

    public void seek( int sample_position ) {
		//int idx; Forge, Unused
		set_sequence_index( 0, 0 );
		next_tick();
		while( sample_position > tick_length_samples ) {
			sample_position -= tick_length_samples;
			next_tick();
		}
		mix_tick();
		current_tick_samples = sample_position;
	}

	public void get_audio( byte[] output_buffer, int frames ) {
		int output_idx, mix_idx, mix_end, count, amplitude;
		output_idx = 0;
		while( frames > 0 ) {
			count = tick_length_samples - current_tick_samples;
			if( count > frames ) {
				count = frames;
			}
			mix_idx = current_tick_samples << 1;
			mix_end = mix_idx + ( count << 1 ) - 1;
			while( mix_idx <= mix_end ) {
				amplitude = mixing_buffer[ mix_idx ];
				if( amplitude > 32767 ) {
					amplitude = 32767;
				}
				if( amplitude < -32768 ) {
					amplitude = -32768;
				}
				output_buffer[ output_idx     ] = ( byte ) ( amplitude >> 8 );
				output_buffer[ output_idx + 1 ] = ( byte ) ( amplitude & 0xFF );
				output_idx += 2;
				mix_idx += 1;
			}
			current_tick_samples = mix_idx >> 1;
			frames -= count;
			if( frames > 0 ) {
				next_tick();
				mix_tick();
				current_tick_samples = 0;
			}
		}
	}

	private void mix_tick() {
		int channel_idx, mix_idx, mix_len;
		mix_idx = 0;
		mix_len = tick_length_samples + volume_ramp_length << 1;
		while( mix_idx < mix_len ) {
			mixing_buffer[ mix_idx ] = 0;
			mix_idx += 1;
		}
		for( channel_idx = 0; channel_idx < channels.length; channel_idx++ ) {
			mix_len = tick_length_samples + volume_ramp_length;
			channels[ channel_idx ].resample( mixing_buffer, 0, mix_len, resampling_quality );
		}
		volume_ramp();
	}

	private boolean next_tick() {
		int channel_idx;
		boolean song_end;
		for( channel_idx = 0; channel_idx < channels.length; channel_idx++ ) {
			channels[ channel_idx ].update_sample_idx( tick_length_samples );
		}
		tick_counter -= 1;
		if( tick_counter <= 0 ) {
			tick_counter = ticks_per_row;
			song_end = next_row();
		} else {
			for( channel_idx = 0; channel_idx < channels.length; channel_idx++ ) {
				channels[ channel_idx ].tick();
			}
			song_end = false;
		}
		return song_end;
	}
	
	private boolean next_row() {
		int channel_idx, effect, effect_param;
		boolean song_end;
		Pattern pattern;
		song_end = false;
		if( next_sequence_index < 0 ) {
			/* Bad next sequence index.*/
			next_sequence_index = 0;
			next_row = 0;
		}
		if( next_sequence_index >= module.get_sequence_length() ) {
			/* End of sequence.*/
			song_end = true;
			next_sequence_index = module.restart_sequence_index;
			if( next_sequence_index < 0 ) {
				next_sequence_index = 0;
			}
			if( next_sequence_index >= module.get_sequence_length() ) {
				next_sequence_index = 0;
			}
			next_row = 0;
		}
		if( next_sequence_index < current_sequence_index ) {
			/* Jump to previous pattern. */
			song_end = true;
		}
		if( next_sequence_index == current_sequence_index ) {
			if( next_row <= current_row ) {
				if( pattern_loop_count < 0 ) {
					/* Jump to previous row in the same pattern, but not a pattern loop. */
					song_end = true;
				}
			}
		}
		current_sequence_index = next_sequence_index;
		pattern = module.get_pattern_from_sequence( current_sequence_index );
		if( next_row < 0 || next_row >= pattern.num_rows ) {
			/* Bad next row.*/
			next_row = 0;
		}
		current_row = next_row;
		next_row = current_row + 1;
		if( next_row >= pattern.num_rows ) {
			next_sequence_index = current_sequence_index + 1;
			next_row = 0;
		}
		for( channel_idx = 0; channel_idx < channels.length; channel_idx++ ) {
			pattern.get_note( note, current_row * channels.length + channel_idx );
			effect = note[ 3 ];
			effect_param = note[ 4 ];
			channels[ channel_idx ].row( note[ 0 ], note[ 1 ], note[ 2 ], effect, effect_param );
			switch( effect ) {
				case 0x0B:
					/* Pattern Jump.*/
					if( pattern_loop_count < 0 ) {
						next_sequence_index = effect_param;
						next_row = 0;
					}
					break;
				case 0x0D:
					/* Pattern Break.*/
					if( pattern_loop_count < 0 ) {
						next_sequence_index = current_sequence_index + 1;
						next_row = ( effect_param >> 4 ) * 10 + ( effect_param & 0x0F );
					}
					break;
				case 0x0E:
					/* Extended.*/
					switch( effect_param & 0xF0 ) {
						case 0x60:
							/* Pattern loop.*/
							if( ( effect_param & 0x0F ) == 0 ) {
								/* Set loop marker on this channel. */
								channels[ channel_idx ].pattern_loop_row = current_row;
							}
							if( channels[ channel_idx ].pattern_loop_row < current_row ) {
								/* Marker and parameter are valid. Begin looping. */
								if( pattern_loop_count < 0 ) {
									/* Not already looping, begin. */
									pattern_loop_count = effect_param & 0x0F;
									pattern_loop_channel = channel_idx;
								}
								if( pattern_loop_channel == channel_idx ) {
									/* Loop in progress on this channel. Next iteration. */
									if( pattern_loop_count == 0 ) {
										/* Loop finished. */
										/* Invalidate current marker. */
										channels[ channel_idx ].pattern_loop_row = current_row + 1;
									} else {
										/* Count must be higher than zero. */
										/* Loop and cancel any breaks on this row. */
										next_row = channels[ channel_idx ].pattern_loop_row;
										next_sequence_index = current_sequence_index;
									}
									pattern_loop_count -= 1;
								}
							}
							break;
						case 0xE0:
							/* Pattern delay.*/
							tick_counter += ticks_per_row * ( effect_param & 0x0F );
							break;
					}
					break;
				case 0x0F:
					/* Set Speed/Tempo.*/
					if( effect_param < 32 ) {
						set_speed( effect_param );
						tick_counter = ticks_per_row;
					} else {
						set_tempo( effect_param );
					}
					break;
				case 0x25:
					/* S3M Set Speed.*/
					set_speed( effect_param );
					tick_counter = ticks_per_row;
					break;
			}
		}
		return song_end;
	}

	private void set_global_volume( int volume ) {
		if( volume < 0 ) {
			volume = 0;
		}
		if( volume > 64 ) {
			volume = 64;
		}
		global_volume[ 0 ] = volume;
	}

	private void set_speed( int speed ) {
		if( speed > 0 && speed < 256 ) {
			ticks_per_row = speed;
		}
	}

	private void set_tempo( int bpm ) {
		if( bpm > 31 && bpm < 256 ) {
			tick_length_samples = ( sampling_rate * 5 ) / ( bpm * 2 );
		}
	}	

	private void volume_ramp() {
		int ramp_idx, next_idx, ramp_end;
		int volume_ramp_delta, volume, sample;
		sample = 0;
		volume_ramp_delta = FP_ONE / volume_ramp_length;
		volume = 0;
		ramp_idx = 0;
		next_idx = 2 * tick_length_samples;
		ramp_end = volume_ramp_length * 2 - 1;
		while( ramp_idx <= ramp_end ) {
			sample = volume_ramp_buffer[ ramp_idx ] * ( FP_ONE - volume ) >> FP_SHIFT;
			mixing_buffer[ ramp_idx ] = sample + ( mixing_buffer[ ramp_idx ] * volume >> FP_SHIFT );
			volume_ramp_buffer[ ramp_idx ] = mixing_buffer[ next_idx + ramp_idx ];
			sample = volume_ramp_buffer[ ramp_idx + 1 ] * ( FP_ONE - volume ) >> FP_SHIFT;
			mixing_buffer[ ramp_idx + 1 ] = sample + ( mixing_buffer[ ramp_idx + 1 ] * volume >> FP_SHIFT );
			volume_ramp_buffer[ ramp_idx + 1 ] = mixing_buffer[ next_idx + ramp_idx + 1 ];
			volume += volume_ramp_delta;
			ramp_idx += 2;
		}
	}
	
	private void clear_vol_ramp_buffer() {
		int ramp_idx, ramp_end;
		ramp_idx = 0;
		ramp_end = volume_ramp_length * 2 - 1;
		while( ramp_idx <= ramp_end ) {
			volume_ramp_buffer[ ramp_idx ] = 0;
			ramp_idx += 1;
		}
	}
}

