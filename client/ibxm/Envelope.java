
package ibxm;

public class Envelope {
	public boolean sustain, looped;
	private int sustain_tick, loop_start_tick, loop_end_tick;
	private int[] ticks, ampls;

	public Envelope() {
		set_num_points( 1 );
	}

	public void set_num_points( int num_points ) {
		int point;
		if( num_points <= 0 ) {
			num_points = 1;
		}
		ticks = new int[ num_points ];
		ampls = new int[ num_points ];
		set_point( 0, 0, 0, false );
	}

	/* When you set a point, all subsequent points are reset. */
	public void set_point( int point, int tick, int ampl, boolean delta ) {
		if( point >= 0 && point < ticks.length ) {
			if( point == 0 ) {
				tick = 0;
			}
			if( point > 0 ) {
				if( delta ) tick += ticks[ point - 1 ];
				if( tick <= ticks[ point - 1 ] ) {
					System.out.println( "Envelope: Point not valid (" + tick + " <= " + ticks[ point - 1 ] + ")");
					tick = ticks[ point - 1 ] + 1;
				}
			}
			ticks[ point ] = tick;
			ampls[ point ] = ampl;
			point += 1;
			while( point < ticks.length ) {
				ticks[ point ] = ticks[ point - 1 ] + 1;
				ampls[ point ] = 0;
				point += 1;
			}
		}
	}

	public void set_sustain_point( int point ) {
		if( point < 0 ) {
			point = 0;
		}
		if( point >= ticks.length ) {
			point = ticks.length - 1;
		}
		sustain_tick = ticks[ point ];
	}

	public void set_loop_points( int start, int end ) {
		if( start < 0 ) {
			start = 0;
		}
		if( start >= ticks.length ) {
			start = ticks.length - 1;
		}
		if( end < start || end >= ticks.length ) {
			end = start;
		}
		loop_start_tick = ticks[ start ];
		loop_end_tick = ticks[ end ];
	}

	public int next_tick( int tick, boolean key_on ) {
		tick = tick + 1;
		if( looped && tick >= loop_end_tick ) {
			tick = loop_start_tick;
		}
		if( sustain && key_on && tick >= sustain_tick ) {
			tick = sustain_tick;
		}
		return tick;
	}

	public int calculate_ampl( int tick ) {
		int idx, point, delta_t, delta_a, ampl;
		ampl = ampls[ ticks.length - 1 ];
		if( tick < ticks[ ticks.length - 1 ] ) {
			point = 0;
			for( idx = 1; idx < ticks.length; idx++ ) {
				if( ticks[ idx ] <= tick ) {
					point = idx;
				}
			}
			delta_t = ticks[ point + 1 ] - ticks[ point ];
			delta_a = ampls[ point + 1 ] - ampls[ point ];
			ampl = ( delta_a << IBXM.FP_SHIFT ) / delta_t;
			ampl = ampl * ( tick - ticks[ point ] ) >> IBXM.FP_SHIFT;
			ampl = ampl + ampls[ point ];
		}
		return ampl;
	}
	
	public void dump() {
		int idx, tick;
		for( idx = 0; idx < ticks.length; idx++ ) {
			System.out.println( ticks[ idx ] + ", " + ampls[ idx ] );
		}
		for( tick = 0; tick < 222; tick++ ) {
			System.out.print( calculate_ampl( tick ) + ", " );
		}
	}
}

