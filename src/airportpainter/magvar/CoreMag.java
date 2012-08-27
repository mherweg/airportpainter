/*
 * CoreMag.java
 *
 * Copyright (C) 2009 Francesco Brisa
 *
 * This file is part of AirportPainter.
 *
 * AirportPainter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AirportPainter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License V.3 for more details.
 *
 * You should have received a copy of the GNU General Public License V.3
 * along with AirportPainter.  If not, see <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * email at:
 * fbrisa@gmail.com  or  fbrisa@yahoo.it
 *
 */
package airportpainter.magvar;
import airportpainter.Airport;
import java.lang.Math;
import java.util.Calendar;

/**
 *
 * @author francesco
 */
public class CoreMag {
    static double a = 6378.137;       /* semi-major axis (equatorial radius) of WGS84 ellipsoid */
    static double b = 6356.7523142;   /* semi-minor axis referenced to the WGS84 ellipsoid */
    static double r_0 = 6371.2;       /* standard Earth magnetic reference radius  */

    static double M_PI=Math.PI;

    static double gnm_wmm2005[][] =
    {
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {-29556.8, -1671.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {-2340.6, 3046.9, 1657.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {1335.4, -2305.1, 1246.7, 674.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {919.8, 798.1, 211.3, -379.4, 100.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {-227.4, 354.6, 208.7, -136.5, -168.3, -14.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {73.2, 69.7, 76.7, -151.2, -14.9, 14.6, -86.3, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {80.1, -74.5, -1.4, 38.5, 12.4, 9.5, 5.7, 1.8, 0.0, 0.0, 0.0, 0.0, 0.0},
        {24.9, 7.7, -11.6, -6.9, -18.2, 10.0, 9.2, -11.6, -5.2, 0.0, 0.0, 0.0, 0.0},
        {5.6, 9.9, 3.5, -7.0, 5.1, -10.8, -1.3, 8.8, -6.7, -9.1, 0.0, 0.0, 0.0},
        {-2.3, -6.3, 1.6, -2.6, 0.0, 3.1, 0.4, 2.1, 3.9, -0.1, -2.3, 0.0, 0.0},
        {2.8, -1.6, -1.7, 1.7, -0.1, 0.1, -0.7, 0.7, 1.8, 0.0, 1.1, 4.1, 0.0},
        {-2.4, -0.4, 0.2, 0.8, -0.3, 1.1, -0.5, 0.4, -0.3, -0.3, -0.1, -0.3, -0.1},
    };

    static double hnm_wmm2005[][]=
    {
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 5079.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -2594.7, -516.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -199.9, 269.3, -524.2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 281.5, -226.0, 145.8, -304.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 42.4, 179.8, -123.0, -19.5, 103.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -20.3, 54.7, 63.6, -63.4, -0.1, 50.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -61.5, -22.4, 7.2, 25.4, 11.0, -26.4, -5.1, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 11.2, -21.0, 9.6, -19.8, 16.1, 7.7, -12.9, -0.2, 0.0, 0.0, 0.0, 0.0},
        {0.0, -20.1, 12.9, 12.6, -6.7, -8.1, 8.0, 2.9, -7.9, 6.0, 0.0, 0.0, 0.0},
        {0.0, 2.4, 0.2, 4.4, 4.8, -6.5, -1.1, -3.4, -0.8, -2.3, -7.9, 0.0, 0.0},
        {0.0, 0.3, 1.2, -0.8, -2.5, 0.9, -0.6, -2.7, -0.9, -1.3, -2.0, -1.2, 0.0},
        {0.0, -0.4, 0.3, 2.4, -2.6, 0.6, 0.3, 0.0, 0.0, 0.3, -0.9, -0.4, 0.8},
    };

    static double gtnm_wmm2005[][]=
    {
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {8.0, 10.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {-15.1, -7.8, -0.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.4, -2.6, -1.2, -6.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {-2.5, 2.8, -7.0, 6.2, -3.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {-2.8, 0.7, -3.2, -1.1, 0.1, -0.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {-0.7, 0.4, -0.3, 2.3, -2.1, -0.6, 1.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.2, -0.1, -0.3, 1.1, 0.6, 0.5, -0.4, 0.6, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.1, 0.3, -0.4, 0.3, -0.3, 0.2, 0.4, -0.7, 0.4, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
    };

    static double htnm_wmm2005[][]=
    {
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -20.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -23.2, -14.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 5.0, -7.0, -0.6, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 2.2, 1.6, 5.8, 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 1.7, 2.1, 4.8, -1.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -0.6, -1.9, -0.4, -0.5, -0.3, 0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.6, 0.4, 0.2, 0.3, -0.8, -0.2, 0.1, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, -0.2, 0.1, 0.3, 0.4, 0.1, -0.2, 0.4, 0.4, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
    };



    static int nmax = 12;

    static double[][] P=new double[13][13];
    static double[][] DP=new double[13][13];
    static double[][] gnm=new double[13][13];
    static double[][] hnm=new double[13][13];
    static double[] sm=new double[13];
    static double[] cm=new double[13];

    static double[] root=new double[13];
    static double[][][]  roots=new double[13][13][2];





    // coremag.cxx -- compute local magnetic variation given position,
    //                altitude, and date
    //
    // This is an implementation of the NIMA (formerly DMA) WMM2000
    //
    //    http://www.nima.mil/GandG/ngdc-wmm2000.html
    //
    // Copyright (C) 2000  Edward A Williams <Ed_Williams@compuserve.com>
    //
    // Adapted from Excel 3.0 version 3/27/94 EAW
    // Recoded in C++ by Starry Chan
    // WMM95 added and rearranged in ANSI-C EAW 7/9/95

    /* Convert date to Julian day    1950-2049 */
    public static long yymmdd_to_julian_days( int yy, int mm, int dd ) {
        long jd;

        yy = (yy < 50) ? (2000 + yy) : (1900 + yy);
        jd = dd - 32075L + 1461L * (yy + 4800L + (mm - 14) / 12 ) / 4;
        jd = jd + 367L * (mm - 2 - (mm - 14) / 12*12) / 12;
        jd = jd - 3 * ((yy + 4900L + (mm - 14) / 12) / 100) / 4;

        /* printf("julian date = %d\n", jd ); */
        return jd;
    }


private  static int SG_MAX2(int  a, int b) {
    return  a > b ? a : b;
}


/*
 * return variation (in radians) given geodetic latitude (radians),
 * longitude(radians), height (km) and (Julian) date
 * N and E lat and long are positive, S and W negative
*/
private static boolean been_here = false;

public static double calc_magvar( double lat, double lon, double h, long dat ) {
    been_here = false;
    double field[]= new double[6];

    /* output field B_r,B_th,B_phi,B_x,B_y,B_z */
    int n,m;
    /* reference date for current model is 1 januari 2005 */
    long date0_wmm2005 = yymmdd_to_julian_days(5,1,1);

    double yearfrac,sr,r,theta,c,s,psi,fn,fn_0,B_r,B_theta,B_phi,X,Y,Z;
    double sinpsi, cospsi, inv_s;



    double sinlat = Math.sin(lat);
    double coslat = Math.cos(lat);

    /* convert to geocentric coords: */
    // sr = Math.sqrt(pow(a*coslat,2.0)+pow(b*sinlat,2.0));
    sr = Math.sqrt(a*a*coslat*coslat + b*b*sinlat*sinlat);
    /* sr is effective radius */
    theta = Math.atan2(coslat * (h*sr + a*a),
		  sinlat * (h*sr + b*b));
    /* theta is geocentric co-latitude */

    r = h*h + 2.0*h * sr +
	(a*a*a*a - ( a*a*a*a - b*b*b*b ) * sinlat*sinlat ) /
	(a*a - (a*a - b*b) * sinlat*sinlat );

    r = Math.sqrt(r);

    /* r is geocentric radial distance */
    c = Math.cos(theta);
    s = Math.sin(theta);
    /* protect against zero divide at geographic poles */
    if (s==0) {
        inv_s =  1.0 / 0.0000000001;
    } else {
       inv_s =  1.0 / s;
    }


    /* zero out arrays */
    for ( n = 0; n <= nmax; n++ ) {
	for ( m = 0; m <= n; m++ ) {
	    P[n][m] = 0;
	    DP[n][m] = 0;
	}
    }

    /* diagonal elements */
    P[0][0] = 1;
    P[1][1] = s;
    DP[0][0] = 0;
    DP[1][1] = c;
    P[1][0] = c ;
    DP[1][0] = -s;

    // these values will not change for subsequent function calls
    if( ! been_here ) {
	for ( n = 2; n <= nmax; n++ ) {
	    root[n] = Math.sqrt((2.0*n-1) / (2.0*n));
	}

	for ( m = 0; m <= nmax; m++ ) {
	    double mm = m*m;
	    for ( n = SG_MAX2(m + 1, 2); n <= nmax; n++ ) {
		roots[m][n][0] = Math.sqrt((n-1)*(n-1) - mm);
		roots[m][n][1] = 1.0 / Math.sqrt( n*n - mm);
	    }
	}
	been_here = true;
    }

    for ( n=2; n <= nmax; n++ ) {
	// double root = Math.sqrt((2.0*n-1) / (2.0*n));
	P[n][n] = P[n-1][n-1] * s * root[n];
	DP[n][n] = (DP[n-1][n-1] * s + P[n-1][n-1] * c) *
	    root[n];
    }

    /* lower triangle */
    for ( m = 0; m <= nmax; m++ ) {
	// double mm = m*m;
	for ( n = SG_MAX2(m + 1, 2); n <= nmax; n++ ) {
	    // double root1 = Math.sqrt((n-1)*(n-1) - mm);
	    // double root2 = 1.0 / Math.sqrt( n*n - mm);
	    P[n][m] = (P[n-1][m] * c * (2.0*n-1) -
		       P[n-2][m] * roots[m][n][0]) *
		roots[m][n][1];

	    DP[n][m] = ((DP[n-1][m] * c - P[n-1][m] * s) *
			(2.0*n-1) - DP[n-2][m] * roots[m][n][0]) *
		roots[m][n][1];
	}
    }

    /* compute Gauss coefficients gnm and hnm of degree n and order m for the desired time
       achieved by adjusting the coefficients at time t0 for linear secular variation */
    /* WMM2005 */
    yearfrac = (dat - date0_wmm2005) / 365.25;
    for ( n = 1; n <= nmax; n++ ) {
	for ( m = 0; m <= nmax; m++ ) {
	    gnm[n][m] = gnm_wmm2005[n][m] + yearfrac * gtnm_wmm2005[n][m];
	    hnm[n][m] = hnm_wmm2005[n][m] + yearfrac * htnm_wmm2005[n][m];
	}
    }

    /* compute sm (sin(m lon) and cm (cos(m lon)) */
    for ( m = 0; m <= nmax; m++ ) {
	sm[m] = Math.sin(m * lon);
	cm[m] = Math.cos(m * lon);
    }

    /* compute B fields */
    B_r = 0.0;
    B_theta = 0.0;
    B_phi = 0.0;
    fn_0 = r_0/r;
    fn = fn_0 * fn_0;

    for ( n = 1; n <= nmax; n++ ) {
	double c1_n=0;
	double c2_n=0;
	double c3_n=0;
	for ( m = 0; m <= n; m++ ) {
	    double tmp = (gnm[n][m] * cm[m] + hnm[n][m] * sm[m]);
	    c1_n=c1_n + tmp * P[n][m];
	    c2_n=c2_n + tmp * DP[n][m];
	    c3_n=c3_n + m * (gnm[n][m] * sm[m] - hnm[n][m] * cm[m]) * P[n][m];
	}
	// fn=pow(r_0/r,n+2.0);
	fn *= fn_0;
	B_r = B_r + (n + 1) * c1_n * fn;
	B_theta = B_theta - c2_n * fn;
	B_phi = B_phi + c3_n * fn * inv_s;
    }

    /* Find geodetic field components: */
    psi = theta - ((M_PI / 2.0) - lat);

    sinpsi = Math.sin(psi);
    cospsi = Math.cos(psi);
    X = -B_theta * cospsi - B_r * sinpsi;
    Y = B_phi;
    Z = B_theta * sinpsi - B_r * cospsi;

    field[0]=B_r;
    field[1]=B_theta;
    field[2]=B_phi;
    field[3]=X;
    field[4]=Y;
    field[5]=Z;   /* output fields */


    double SGD_DEGREES_TO_RADIANS=00.017453;

    /* find variation in radians */
    /* return zero variation at magnetic pole X=Y=0. */
    /* E is positive */
    return (X != 0. || Y != 0.) ? Math.atan2(Y, X)/SGD_DEGREES_TO_RADIANS : (double) 0.;
}

    public static double getMagneticVariation(Airport airport) {
        double SGD_DEGREES_TO_RADIANS=00.017453;
        Calendar now = Calendar.getInstance();

        return CoreMag.calc_magvar(
            airport.getLat()*SGD_DEGREES_TO_RADIANS,
            airport.getLong()*SGD_DEGREES_TO_RADIANS,
            airport.getElevation(),
            CoreMag.yymmdd_to_julian_days(
                now.get(Calendar.YEAR)-2000,
                now.get(Calendar.MONTH)+1,
                now.get(Calendar.DAY_OF_MONTH)
            )
        );
    }

}
