AirportPainter v.1.2

###########################################
This program generates Airport diagrams from Flightgear data in pdf file format.
The code is a elaboration of the code from the kelpie flight planner.

###########################################
Example on how to launch the program, remember to set -fg-root to you FlightGear data folder

java -jar AirportPainter.jar -fg-root=/opt/fg/install/fgfs/data  -airport=KSFO

Launching in graphical mode:
Just double click on the AirportPainter.jar or launch the program without airport parameter:

java -jar AirportPainter.jar


###########################################
Other options:
###########################################
* -fg-root=path                         The path (absolute or relative) of out Flightgear installation folder
* -airport=ID                           The ICAO code of the airport to render
* -output=fileName                      Select a different file name for output (pdf and png files)
* -range=distance                       Do not render only choosen airport, but all airports within this nautical miles
* -bg-color=RRGGBB                      Use color as background (Now works only with FFFFFF)
* -drawObjectsInAirport=[true|false]    draw objects from stg fiels in the airport sheets
* -drawObjectsInMap=[true|false]        draw objects from stg fiels in the map
* -drawTerrainInMap=[true|false]        draw terrain from btg fiels in the map


###########################################
Remember, do not use this program for real life charts !
###########################################

###########################################
Author: Francesco angelo Brisa <fbrisa@gmail.com> <fbrisa@yahoo.it>
###########################################


###########################################
Changelog:
###########################################
* 1.2: added terrain paint support
* 1.1: padding coordinate to 000 to load objects in sceneries folder.
* 1.0: Initial release
	
	
