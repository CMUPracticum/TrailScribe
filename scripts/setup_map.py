from subprocess import call
import sys
import os.path
import xml.etree.ElementTree as ET
import mysql.connector
from mysql.connector import errorcode
from datetime import datetime
import ConfigParser

class MapMetadata:
    """An object to store map metadata."""
    def __init__(self, name="", projection="EPSG:900913", minZoomLevel=10, 
                maxZoomLevel=15, bounds={'minx':-180,'miny':-90,'maxx':180,'maxy':90}, filename=""):
        self.name = name
        self.projection = projection
        self.minZoomLevel = minZoomLevel
        self.maxZoomLevel = maxZoomLevel
        self.bounds = bounds
        self.filename = filename

def create_map_metadata(mapname):
    """
    Parse XML containing the metadata for the newly created tiles
    and return the map metadata
    Parameters:
    mapname - The name of the map
    """
    metadata = MapMetadata()
    metadata.name = mapname

    tree = ET.parse('./tmp/tiles/tilemapresource.xml')
    root = tree.getroot()

    for child in root:
        if child.tag == "SRS":
            metadata.projection = child.text

        if child.tag == "BoundingBox":
            # In the XML file, the attributes that contains x and y coordinates are switched.
            # They are set correctly in the metadata.bounds dict
            metadata.bounds['minx'] = float(child.attrib['miny'])
            metadata.bounds['miny'] = float(child.attrib['minx'])
            metadata.bounds['maxx'] = float(child.attrib['maxy'])
            metadata.bounds['maxy'] = float(child.attrib['maxx'])

    zoomLevels = []
    for tileset in root.iter('TileSet'):
        zoomLevels.append(int(tileset.attrib['order']))

    metadata.minZoomLevel = min(zoomLevels)
    metadata.maxZoomLevel = max(zoomLevels)     
    metadata.filename = mapname + "_tiles.zip"

    return metadata


def setup_map(sourcemap, mapname):
    """
    Given a Geotiff source map, and a mapname, create and return a processed and tiled map metadata.
    """
    
    if not os.path.isfile(sourcemap):
        print "Input image does not exist!"
        sys.exit()

    if not mapname or len(mapname) == 0:
        print "Please provide a map name"
        sys.exit()

    # Subprocess call to shell script that handles map tiling
    output = call(["./setup_map", sourcemap, mapname])

    if output != 0:
        print "Tiling unsuccessful!"
        sys.exit()
    else:
        return create_map_metadata(mapname)


def map_exists(mapname, cursor):
    """
    Check if a given map already exists in the database, 
    return True or False.
    """
    query = ("SELECT id FROM sync_center_map WHERE name = %s")
    query_data = (mapname, )    

    cursor.execute(query, query_data)   
    rows = cursor.fetchall()

    if rows:        
        return True
    else:
        return False


def update_db(metadata):
    """
    Update database with given map metadata.
    """
    config = ConfigParser.ConfigParser(allow_no_value=True)
    config.read('config.ini')

    try:
        connection = mysql.connector.connect(database=config.get('mysqld', 'database'), user=config.get('mysqld', 'user'), 
                                             password=config.get('mysqld', 'password'))

        cursor = connection.cursor()

        # If the map already exists, check if the user wants to proceed with updating the map metadata
        if map_exists(metadata.name, cursor):
            
            stdin = raw_input("This map already exists in the database. Would you like to update the record for this map " + 
                              " with the new data (Y/yes or N/no)? ")

            if stdin.lower() in ['y', 'yes']:
                query = ("UPDATE sync_center_map SET "
                         "name = %s, projection = %s, min_zoom_level = %s, max_zoom_level = %s, min_y = %s, min_x = %s, "
                         "max_y = %s, max_x = %s, filename = %s, last_modified = now() WHERE name = %s")
                query_data = (metadata.name, metadata.projection, metadata.minZoomLevel, metadata.maxZoomLevel, 
                              metadata.bounds['miny'], metadata.bounds['minx'], metadata.bounds['maxy'], metadata.bounds['maxx'], metadata.filename, metadata.name)
            else:
                print "Operation aborted. Please provide a unique map name and run the script again."
                sys.exit()
        # Otherwise, just insert the record for the new map
        else:
            query = ("INSERT INTO sync_center_map " 
                    "(name, projection, min_zoom_level, max_zoom_level, min_y, min_x, max_y, max_x, filename, last_modified) "
                    "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, now())")
            query_data = (metadata.name, metadata.projection, metadata.minZoomLevel, metadata.maxZoomLevel, 
                          metadata.bounds['miny'], metadata.bounds['minx'], metadata.bounds['maxy'], metadata.bounds['maxx'], metadata.filename)

        cursor.execute(query, query_data)
        # Commit
        connection.commit()

    except mysql.connector.Error as error:
        if error.errno == errorcode.ER_ACCESS_DENIED_ERROR:
            print "Wrong username or password"
        elif error.errno == errorcode.ER_BAD_DB_ERROR:
            print "No such database"
        else:
            print error
    finally:
        cursor.close()
        connection.close()


if __name__ == "__main__":  
    if len(sys.argv) != 3:
        print "Usage: python setup_map.py tmp/<source_image.tif> <map_name>\nExpected number of arguments: 2. Got: " + str(len(sys.argv)-1)
        sys.exit()

    metadata = setup_map(sys.argv[1], sys.argv[2])
    update_db(metadata) 
