
package ibxm;

public class Channel {
	public int pattern_loop_row;

	private Module module;
	private Instrument instrument;
	private Sample sample;
	private int[] global_volume, current_note;
	private boolean linear_periods, fast_volume_slides, key_on, silent;
	private int sample_idx, sample_frac, step, left_gain, right_gain;
	private int volume, panning, fine_tune, period, porta_period, key_add;
	private int tremolo_speed, tremolo_depth, tremolo_tick, tremolo_wave, tremolo_add;
	private int vibrato_speed, vibrato_depth, vibrato_tick, vibrato_wave, vibrato_add;
	private int volume_slide_param, portamento_param, retrig_param;
	private int volume_envelope_tick, panning_envelope_tick;
	private int effect_tick, trigger_tick, fade_out_volume, random_seed;

	private int log_2_sampling_rate;
	private static final int LOG_2_29024 = LogTable.log_2( 29024 );
	private static final int LOG_2_8287 = LogTable.log_2( 8287 );
	private static final int LOG_2_8363 = LogTable.log_2( 8363 );
	private static final int LOG_2_1712 = LogTable.log_2( 1712 );

	private static final int[] sine_table = new int[] {
		  0, 24 ,  49,  74,  97, 120, 141, 161, 180, 197, 212, 224, 235, 244, 250, 253,
		255, 253, 250, 244, 235, 224, 212, 197, 180, 161, 141, 120,  97,  74,  49,  24
	};

	public Channel( Module mod, int sampling_rate, int[] global_vol ) {
		module = mod;
		global_volume = global_vol;
		linear_periods = module.linear_periods;
		fast_volume_slides = module.fast_volume_slides;
		current_note = new int[ 5 ];
		log_2_sampling_rate = LogTable.log_2( sampling_rate );
	}
	
	public void reset() {
		tremolo_speed = 0;
		tremolo_depth = 0;
		tremolo_wave = 0;
		vibrato_speed = 0;
		vibrato_depth = 0;
		vibrato_wave = 0;
		volume_slide_param = 0;
		portamento_param = 0;
		retrig_param = 0;
		random_seed = 0xABC123;
		instrument = module.get_instrument( 0 );
		row( 48, 256, 0, 0, 0 );
	}
	
	public void resample( int[] mixing_buffer, int frame_offset, int frames, int quality ) {
		if( !silent ) {
			switch( quality ) {
				default:
					sample.resample_nearest( sample_idx, sample_frac, step, left_gain, right_gain, mixing_buffer, frame_offset, frames );
					break;
				case 1:
					sample.resample_linear( sample_idx, sample_frac, step, left_gain, right_gain, mixing_buffer, frame_offset, frames );
					break;
				case 2:
					sample.resample_sinc( sample_idx, sample_frac, step, left_gain, right_gain, mixing_buffer, frame_offset, frames );
					break;
			}
		}
	}

	public void update_sample_idx( int samples ) {
		sample_frac += step * samples;
		sample_idx += sample_frac >> IBXM.FP_SHIFT;
		sample_frac &= IBXM.FP_MASK;
	}
	
	public void set_volume( int vol ) {
		if( vol < 0 ) {
			vol = 0;
		}
		if( vol > 64 ) {
			vol = 64;
		}
		volume = vol;
	}
	
	public void set_panning( int pan ) {
		if( pan < 0 ) {
			pan = 0;
		}
		if( pan > 255 ) {
			pan = 255;
		}
		panning = pan;
	}
	
	public void row( int key, int inst_idx, int volume_column, int effect, int effect_param ) {
		effect = effect & 0xFF;
		if( effect >= 0x30 ) {
			/* Effects above 0x30 are internal.*/
			effect = 0;
		}
		if( effect == 0x00 && effect_param != 0 ) {
			/* Arpeggio.*/
			effect = 0x40;
		}
		if( effect == 0x0E ) {
			/* Renumber 0x0Ex effect command.*/
			effect = 0x30 + ( ( effect_param & 0xF0 ) >> 4 );
			effect_param = effect_param & 0x0F;
		}
		if( effect == 0x21 ) {
			/* Renumber 0x21x effect command.*/
			effect = 0x40 + ( ( effect_param & 0xF0 ) >> 4 );
			effect_param = effect_param & 0x0F;
		}
		current_note[ 0 ] = key;
		current_note[ 1 ] = inst_idx;
		current_note[ 2 ] = volume_column;
		current_note[ 3 ] = effect;
		current_note[ 4 ] = effect_param;
		effect_tick = 0;		
		trigger_tick += 1;
		update_envelopes();
		key_add = 0;
		vibrato_add = 0;
		tremolo_add = 0;
		if( ! ( effect == 0x3D && effect_param > 0 ) ) {
			/* Not note delay.*/
			trigger( key, inst_idx, volume_column, effect );
			/* Handle volume column.*/
			switch( volume_column & 0xF0 ) {
				case 0x00:
					/* Do nothing.*/
					break;
				case 0x60:
					/* Volume slide down.*/
					break;
				case 0x70:
					/* Volume slide up.*/
					break;
				case 0x80:
					/* Fine volume slide down.*/
					set_volume( volume - ( volume_column & 0x0F ) );
					break;
				case 0x90:
					/* Fine volume slide up.*/
					set_volume( volume + ( volume_column & 0x0F ) );
					break;
				case 0xA0:
					/* Set vibrato speed.*/
					set_vibrato_speed( volume_column & 0x0F );
					break;
				case 0xB0:
					/* Vibrato.*/
					set_vibrato_depth( volume_column & 0x0F );
					vibrato();
					break;
				case 0xC0:
					/* Set panning.*/
					set_panning( ( volume_column & 0x0F ) << 4 );
					break;
				case 0xD0:
					/* Panning slide left.*/
					break;
				case 0xE0:
					/* Panning slide right.*/
					break;
				case 0xF0:
					/* Tone portamento.*/
					set_portamento_param( volume_column & 0x0F );
					break;
				default:
					/* Set volume.*/
					set_volume( volume_column - 0x10 );
					break;
			}
		}
		if( instrument.vibrato_depth > 0 ) {
			auto_vibrato();
		}
		switch( effect ) {
			case 0x01:
				/* Portmento Up.*/
				set_portamento_param( effect_param );
				portamento_up();
				break;
			case 0x02:
				/* Portamento Down.*/
				set_portamento_param( effect_param );
				portamento_down();
				break;
			case 0x03:
				/* Tone Portamento.*/
				set_portamento_param( effect_param );
				break;
			case 0x04:
				/* Vibrato.*/
				set_vibrato_speed( ( effect_param & 0xF0 ) >> 4 );
				set_vibrato_depth( effect_param & 0x0F );
				vibrato();
				break;
			case 0x05:
				/* Tone Portamento + Volume Slide.*/
				set_volume_slide_param( effect_param );
				volume_slide();
				break;
			case 0x06:
				/* Vibrato + Volume Slide.*/
				set_volume_slide_param( effect_param );
				vibrato();
				volume_slide();
				break;
			case 0x07:
				/* Tremolo.*/
				set_tremolo_speed( ( effect_param & 0xF0 ) >> 4 );
				set_tremolo_depth( effect_param & 0x0F );
				tremolo();
				break;
			case 0x08:
				/* Set Panning.*/
				set_panning( effect_param );
				break;
			case 0x09:
				/* Set Sample Index.*/
				set_sample_index( effect_param << 8 );
				break;
			case 0x0A:
				/* Volume Slide.*/
				set_volume_slide_param( effect_param );
				volume_slide();
				break;
			case 0x0B:
				/* Pattern Jump.*/
				break;
			case 0x0C:
				/* Set volume.*/
				set_volume( effect_param );
				break;
			case 0x0D:
				/* Pattern Break.*/
				break;
			case 0x0E:
				/* Extended Commands (See 0x30-0x3F).*/
				break;
			case 0x0F:
				/* Set Speed/Tempo.*/
				break;
			case 0x10:
				/* Set Global Volume.*/
				set_global_volume( effect_param );
				break;
			case 0x11:
				/* global Volume Slide.*/
				set_volume_slide_param( effect_param );
				break;
			case 0x14:
				/* Key Off*/
				if( effect_param == 0 ) {
					key_on = false;
				}
				break;
			case 0x15:
				/* Set Envelope Tick.*/
				set_envelope_tick( effect_param );
				break;
			case 0x19:
				/* Panning Slide.*/
				set_volume_slide_param( effect_param );
				break;
			case 0x1B:
				/* Retrig + Volume Slide.*/
				set_retrig_param( effect_param );
				retrig_volume_slide();
				break;
			case 0x1D:
				/* Tremor.*/
				set_retrig_param( effect_param );
				tremor();
				break;
			case 0x24:
				/* S3M Fine Vibrato.*/
				set_vibrato_speed( ( effect_param & 0xF0 ) >> 4 );
				set_vibrato_depth( effect_param & 0x0F );
				fine_vibrato();
				break;
			case 0x25:
				/* S3M Set Speed.*/
				break;
			case 0x30:
				/* Amiga Set Filter.*/
				break;
			case 0x31:
				/* Fine Portamento Up.*/
				set_portamento_param( 0xF0 | effect_param );
				portamento_up();
				break;
			case 0x32:
				/* Fine Portamento Down.*/
				set_portamento_param( 0xF0 | effect_param );
				portamento_down();
				break;
			case 0x33:
				/* Set Glissando Mode.*/
				break;
			case 0x34:
				/* Set Vibrato Waveform.*/
				set_vibrato_wave( effect_param );
				break;
			case 0x35:
				/* Set Fine Tune.*/
				break;
			case 0x36:
				/* Pattern Loop.*/
				break;
			case 0x37:
				/* Set Tremolo Waveform.*/
				set_tremolo_wave( effect_param );
				break;
			case 0x38:
				/* Set Panning(Obsolete).*/
				break;
			case 0x39:
				/* Retrig.*/
				set_retrig_param( effect_param );
				break;
			case 0x3A:
				/* Fine Volume Slide Up.*/
				set_volume_slide_param( ( effect_param << 4 ) | 0x0F );
				volume_slide();
				break;
			case 0x3B:
				/* Fine Volume Slide Down.*/
				set_volume_slide_param( 0xF0 | effect_param );
				volume_slide();
				break;
			case 0x3C:
				/* Note Cut.*/
				if( effect_param == 0 ) {
					set_volume( 0 );
				}
				break;
			case 0x3D:
				/* Note Delay.*/
				break;
			case 0x3E:
				/* Pattern Delay.*/
				break;
			case 0x3F:
				/* Invert Loop.*/
				break;
			case 0x40:
				/* Arpeggio.*/
				break;
			case 0x41:
				/* Extra Fine Porta Up.*/
				set_portamento_param( 0xE0 | effect_param );
				portamento_up();
				break;
			case 0x42:
				/* Extra Fine Porta Down.*/
				set_portamento_param( 0xE0 | effect_param );
				portamento_down();
				break;
		}
		calculate_amplitude();
		calculate_frequency();
	}

	public void tick() {
		int volume_column, effect, effect_param;
		volume_column = current_note[ 2 ];
		effect = current_note[ 3 ];
		effect_param = current_note[ 4 ];
		effect_tick += 1;
		if( effect == 0x3D && effect_param == effect_tick ) {
			/* Note delay.*/
			row( current_note[ 0 ], current_note[ 1 ], volume_column, 0, 0 );
		} else {
			trigger_tick += 1;
			vibrato_tick += 1;
			tremolo_tick += 1;
			update_envelopes();
			key_add = 0;
			vibrato_add = 0;
			tremolo_add = 0;
			if( instrument.vibrato_depth > 0 ) {
				auto_vibrato();
			}
			switch( volume_column & 0xF0 ) {
				case 0x60:
					/* Volume Slide Down.*/
					set_volume( volume - ( volume_column & 0x0F ) );
					break;
				case 0x70:
					/* Volume Slide Up.*/
					set_volume( volume + ( volume_column & 0x0F ) );
					break;
				case 0xB0:
					/* Vibrato.*/
					vibrato();
					break;
				case 0xD0:
					/* Panning Slide Left.*/
					set_panning( panning - ( volume_column & 0x0F ) );
					break;
				case 0xE0:
					/* Panning Slide Right.*/
					set_panning( panning + ( volume_column & 0x0F ) );
					break;
				case 0xF0:
					/* Tone Portamento.*/
					tone_portamento();
					break;
			}
			switch( effect ) {
				case 0x01:
					/* Portamento Up.*/
					portamento_up();
					break;
				case 0x02:
					/* Portamento Down.*/
					portamento_down();
					break;
				case 0x03:
					/* Tone Portamento.*/
					tone_portamento();
					break;
				case 0x04:
					/* Vibrato.*/
					vibrato();
					break;
				case 0x05:
					/* Tone Portamento + Volume Slide.*/
					tone_portamento();
					volume_slide();
					break;
				case 0x06:
					/* Vibrato + Volume Slide */
					vibrato();
					volume_slide();
					break;
				case 0x07:
					/* Tremolo.*/
					tremolo();
					break;
				case 0x0A:
					/* Volume Slide.*/
					volume_slide();
					break;
				case 0x11:
					/* Global Volume Slide.*/
					global_volume_slide();
					break;
				case 0x14:
					/* Key off.*/
					if( effect_tick == effect_param ) {
						key_on = false;
					}
					break;
				case 0x19:
					/* Panning Slide.*/
					panning_slide();
					break;
				case 0x1B:
					/* Retrig + Volume Slide.*/
					retrig_volume_slide();
					break;
				case 0x1D:
					/* Tremor.*/
					tremor();
					break;
				case 0x24:
					/* S3M Fine Vibrato.*/
					fine_vibrato();
					break;
				case 0x39:
					/* Retrig.*/
					retrig_volume_slide();
					break;
				case 0x3C:
					/* Note Cut.*/
					if( effect_tick == effect_param ) {
						set_volume( 0 );
					}
					break;
				case 0x40:
					/* Arpeggio.*/
					switch( effect_tick % 3 ) {
						case 1:
							key_add = ( effect_param & 0xF0 ) >> 4;
							break;
						case 2:
							key_add = effect_param & 0x0F;
							break;
					}
					break;
			}
		}
		calculate_amplitude();
		calculate_frequency();
	}

	private void set_vibrato_speed( int speed ) {
		if( speed > 0 ) {
			vibrato_speed = speed;
		}
	}

	private void set_vibrato_depth( int depth ) {
		if( depth > 0 ) {
			vibrato_depth = depth;
		}
	}
	
	private void set_vibrato_wave( int wave ) {
		if( wave < 0 || wave > 7 ) {
			wave = 0;
		}
		vibrato_wave = wave;
	}

	private void set_tremolo_speed( int speed ) {
		if( speed > 0 ) {
			tremolo_speed = speed;
		}
	}

	private void set_tremolo_depth( int depth ) {
		if( depth > 0 ) {
			tremolo_depth = depth;
		}
	}

	private void set_tremolo_wave( int wave ) {
		if( wave < 0 || wave > 7 ) {
			wave = 0;
		}
		tremolo_wave = wave;
	}

	private void vibrato() {
		int vibrato_phase;
		vibrato_phase = vibrato_tick * vibrato_speed;
		vibrato_add += waveform( vibrato_phase, vibrato_wave ) * vibrato_depth >> 5;
	}

	private void fine_vibrato() {
		int vibrato_phase;
		vibrato_phase = vibrato_tick * vibrato_speed;
		vibrato_add += waveform( vibrato_phase, vibrato_wave ) * vibrato_depth >> 7;
	}

	private void tremolo() {
		int tremolo_phase;
		tremolo_phase = tremolo_tick * tremolo_speed;
		tremolo_add += waveform( tremolo_phase, tremolo_wave ) * tremolo_depth >> 6;
	}

	private void set_portamento_param( int param ) {
		if( param != 0 ) {
			portamento_param = param;
		}
	}

	private void tone_portamento() {
		int new_period;
		if( porta_period < period ) {
			new_period = period - ( portamento_param << 2 );
			if( new_period < porta_period ) {
				new_period = porta_period;
			}
			set_period( new_period );
		}
		if( porta_period > period ) {
			new_period = period + ( portamento_param << 2 );
			if( new_period > porta_period ) {
				new_period = porta_period;
			}
			set_period( new_period );
		}
	}
	
	private void portamento_up() {
		if( ( portamento_param & 0xF0 ) == 0xE0 ) {
			/* Extra-fine porta.*/
			if( effect_tick == 0 ) {
				set_period( period - ( portamento_param & 0x0F ) );
			}	
		} else if( ( portamento_param & 0xF0 ) == 0xF0 ) {
			/* Fine porta.*/
			if( effect_tick == 0 ) {
				set_period( period - ( ( portamento_param & 0x0F ) << 2 ) );
			}
		} else {
			/* Normal porta.*/
			if( effect_tick > 0 ) {
				set_period( period - ( portamento_param << 2 ) );
			}
		}
	}
	
	private void portamento_down() {
		if( ( portamento_param & 0xF0 ) == 0xE0 ) {
			/* Extra-fine porta.*/
			if( effect_tick == 0 ) {
				set_period( period + ( portamento_param & 0x0F ) );
			}	
		} else if( ( portamento_param & 0xF0 ) == 0xF0 ) {
			/* Fine porta.*/
			if( effect_tick == 0 ) {
				set_period( period + ( ( portamento_param & 0x0F ) << 2 ) );
			}
		} else {
			/* Normal porta.*/
			if( effect_tick > 0 ) {
				set_period( period + ( portamento_param << 2 ) );
			}
		}
	}

	private void set_period( int p ) {
		if( p < 32 ) {
			p = 32;
		}
		if( p > 32768 ) {
			p = 32768;
		}
		period = p;
	}

	private void set_global_volume( int vol ) {
		if( vol < 0 ) {
			vol = 0;
		}
		if( vol > 64 ) {
			vol = 64;
		}
		global_volume[ 0 ] = vol;
	}

	private void set_volume_slide_param( int param ) {
		if( param != 0 ) {
			volume_slide_param = param;
		}
	}

	private void global_volume_slide() {
		int up, down;
		up = ( volume_slide_param & 0xF0 ) >> 4;
		down = volume_slide_param & 0x0F;
		set_global_volume( global_volume[ 0 ] + up - down );
	}

	private void volume_slide() {
		int up, down;
		up = ( volume_slide_param & 0xF0 ) >> 4;
		down = volume_slide_param & 0x0F;
		if( down == 0x0F && up > 0 ) {
			/* Fine slide up.*/
			if( effect_tick == 0 ) {
				set_volume( volume + up );
			}
		} else if( up == 0x0F && down > 0 ) {
			/* Fine slide down.*/
			if( effect_tick == 0 ) {
				set_volume( volume - down );
			}
		} else {
			/* Normal slide.*/
			if( effect_tick > 0 || fast_volume_slides ) {
				set_volume( volume + up - down );
			}
		}
	}

	private void panning_slide() {
		int left, right;
		left = ( volume_slide_param & 0xF0 ) >> 4;
		right = volume_slide_param & 0x0F;
		set_panning( panning - left + right );
	}

	private void set_retrig_param( int param ) {
		if( param != 0 ) {
			retrig_param = param;
		}
	}

	private void tremor() {
		int on_ticks, cycle_length, cycle_index;
		on_ticks = ( ( retrig_param & 0xF0 ) >> 4 ) + 1;
		cycle_length = on_ticks + ( retrig_param & 0x0F ) + 1;
		cycle_index = trigger_tick % cycle_length;
		if( cycle_index >= on_ticks ) {
			tremolo_add = -64;
		}
	}

	private void retrig_volume_slide() {
		int retrig_volume, retrig_tick;
		retrig_volume = ( retrig_param & 0xF0 ) >> 4;
		retrig_tick = retrig_param & 0x0F;
		if( retrig_tick > 0 && ( trigger_tick % retrig_tick ) == 0 ) {
			set_sample_index( 0 );
			switch( retrig_volume ) {
				case 0x01:
					set_volume( volume - 1 );
					break;
				case 0x02:
					set_volume( volume - 2 );
					break;
				case 0x03:
					set_volume( volume - 4 );
					break;
				case 0x04:
					set_volume( volume - 8 );
					break;
				case 0x05:
					set_volume( volume - 16 );
					break;
				case 0x06:
					set_volume( volume - volume / 3 );
					break;
				case 0x07:
					set_volume( volume / 2 );
					break;
				case 0x09:
					set_volume( volume + 1 );
					break;
				case 0x0A:
					set_volume( volume + 2 );
					break;
				case 0x0B:
					set_volume( volume + 4 );
					break;
				case 0x0C:
					set_volume( volume + 8 );
					break;
				case 0x0D:
					set_volume( volume + 16 );
					break;
				case 0x0E:
					set_volume( volume + volume / 2 );
					break;
				case 0x0F:
					set_volume( volume * 2 );
					break;
			}
		}
	}

	private void set_sample_index( int index ) {
		if( index < 0 ) {
			index = 0;
		}
		sample_idx = index;
		sample_frac = 0;
	}

	private void set_envelope_tick( int tick ) {
		volume_envelope_tick = tick;
		panning_envelope_tick = tick;
	}

	private void trigger( int key, int instrument_idx, int volume_column, int effect ) {
		if( instrument_idx > 0 ) {
			instrument = module.get_instrument( instrument_idx );
			sample = instrument.get_sample_from_key( key );
			set_volume( sample.volume );
			if( sample.set_panning ) {
				set_panning( sample.panning );
			}
			set_envelope_tick( 0 );
			fade_out_volume = 32768;
			key_on = true;
		}
		if( key > 0 ) {
			if( key < 97 ) {
				porta_period = key_to_period( key );
				if( effect != 0x03 && effect != 0x05 ) {
					if( ( volume_column & 0xF0 ) != 0xF0 ) {
						/* Not portamento.*/
						trigger_tick = 0;
						if( vibrato_wave < 4 ) {
							vibrato_tick = 0;
						}
						if( tremolo_wave < 4 ) {
							tremolo_tick = 0;
						}
						set_period( porta_period );
						set_sample_index( 0 );
					}
				}
			} else {
				/* Key off.*/
				key_on = false;
			}
		}
	}

	private void update_envelopes() {
		Envelope envelope;
		if( instrument.volume_envelope_active ) {
			if( !key_on ) {
				fade_out_volume -= instrument.volume_fade_out & 0xFFFF;
				if( fade_out_volume < 0 ) {
					fade_out_volume = 0;
				}
			}
			envelope = instrument.get_volume_envelope();
			volume_envelope_tick = envelope.next_tick( volume_envelope_tick, key_on );
		}
		if( instrument.panning_envelope_active ) {
			envelope = instrument.get_panning_envelope();
			panning_envelope_tick = envelope.next_tick( panning_envelope_tick, key_on );
		}
	}

	private void auto_vibrato() {
		int sweep, depth, rate;
		sweep = instrument.vibrato_sweep & 0xFF;
		depth = instrument.vibrato_depth & 0x0F;
		rate = instrument.vibrato_rate & 0x3F;
		if( trigger_tick < sweep ) {
			depth = depth * trigger_tick / sweep;
		}
		vibrato_add += waveform( trigger_tick * rate, 0 ) * depth >> 9;
	}

	private int waveform( int phase, int wform ) {
		int amplitude;
		amplitude = 0;
		switch( wform & 0x3 ) {
			case 0:
				/* Sine. */
				if( ( phase & 0x20 ) == 0 ) {
					amplitude =  sine_table[ phase & 0x1F ];
				} else {
					amplitude = -sine_table[ phase & 0x1F ];
				}
				break;
			case 1:
				/* Saw. */
				if( ( phase & 0x20 ) == 0 ) {
					amplitude =   ( phase & 0x1F ) << 3;
				} else {
					amplitude = ( ( phase & 0x1F ) << 3 ) - 255;
				}
				break;
			case 2:
				/* Square. */
				if( ( phase & 0x20 ) == 0 ) {
					amplitude =  255;
				} else {
					amplitude = -255;
				}
				break;
			case 3:
				/* Random. */
				amplitude = ( random_seed >> 15 ) - 255;
				random_seed = ( random_seed * 65 + 17 ) & 0xFFFFFF;
				break;
		}
		return amplitude;
	}

	private int key_to_period( int key ) {
		int octave, log_2_period, period_out;
		octave = ( key << IBXM.FP_SHIFT ) / 12 + sample.transpose;
		if( linear_periods ) {
			period_out = 7744 - ( octave * 768 >> IBXM.FP_SHIFT );
		} else {
			log_2_period = LOG_2_29024 - octave;
			period_out = LogTable.raise_2( log_2_period );
			period_out = period_out >> ( IBXM.FP_SHIFT - 1 );
			period_out = ( period_out >> 1 ) + ( period_out & 1 );
		}
		return period_out;
	}

	private void calculate_amplitude() {
		int envelope_volume, tremolo_volume, amplitude;
		int envelope_panning, mixer_panning, panning_range;
		Envelope envelope;
		envelope_volume = 0;
		if( instrument.volume_envelope_active ) {
			envelope = instrument.get_volume_envelope();
			envelope_volume = envelope.calculate_ampl( volume_envelope_tick );
		} else {
			if( key_on ) {
				envelope_volume = 64;
			}
		}
		tremolo_volume = volume + tremolo_add;
		if( tremolo_volume < 0 ) {
			tremolo_volume = 0;
		}
		if( tremolo_volume > 64 ) {
			tremolo_volume = 64;
		}
		amplitude = tremolo_volume << IBXM.FP_SHIFT - 6;
		amplitude = amplitude * envelope_volume >> 6;
		amplitude = amplitude * fade_out_volume >> 15;
		amplitude = amplitude * global_volume[ 0 ] >> 6;
		amplitude = amplitude * module.channel_gain >> IBXM.FP_SHIFT;
		silent = sample.has_finished( sample_idx );
		if( amplitude <= 0 ) {
			silent = true;
		} else {
			envelope_panning = 32;
			if( instrument.panning_envelope_active ) {
				envelope = instrument.get_panning_envelope();
				envelope_panning = envelope.calculate_ampl( panning_envelope_tick );
			}
			mixer_panning = ( panning & 0xFF ) << IBXM.FP_SHIFT - 8;
			panning_range = IBXM.FP_ONE - mixer_panning;
			if( panning_range > mixer_panning ) {
				panning_range = mixer_panning;
			}
			mixer_panning = mixer_panning + ( panning_range * ( envelope_panning - 32 ) >> 5 );
			left_gain = amplitude * ( IBXM.FP_ONE - mixer_panning ) >> IBXM.FP_SHIFT;
			right_gain = amplitude * mixer_panning >> IBXM.FP_SHIFT;
		}
	}

	private void calculate_frequency() {
		int vibrato_period, log_2_freq;	
		vibrato_period = period + vibrato_add;
		if( vibrato_period < 32 ) {
			vibrato_period = 32;
		}
		if( vibrato_period > 32768 ) {
			vibrato_period = 32768;
		}
		if( linear_periods ) {
			log_2_freq = LOG_2_8363 + ( 4608 - vibrato_period << IBXM.FP_SHIFT ) / 768;
		} else {
			log_2_freq = module.pal ? LOG_2_8287 : LOG_2_8363;
			log_2_freq = log_2_freq + LOG_2_1712 - LogTable.log_2( vibrato_period );
		}
		log_2_freq += ( key_add << IBXM.FP_SHIFT ) / 12;
		step = LogTable.raise_2( log_2_freq - log_2_sampling_rate );
	}
}

