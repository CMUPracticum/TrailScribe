from subprocess import call
import sys
import os.path
import xml.etree.ElementTree as ET
import mysql.connector
from mysql.connector import errorcode
from datetime import datetime
import ConfigParser

class MapMetadata:
	''' An object to store map metadata '''
	def __init__(self, name = "", projection = "EPSG:900913", minZoomLevel = 10, maxZoomLevel = 15, bounds = [], filename = ""):
		self.name = name
		self.projection = projection
		self.minZoomLevel = minZoomLevel
		self.maxZoomLevel = maxZoomLevel
		self.bounds = bounds
		self.filename = filename
		

def setup_map(source_map, map_name):
	# make sure file exists
	if not os.path.isfile(source_map):
		print "Input image does not exist!"
		sys.exit()

	if not map_name or len(map_name) == 0:
		print "Please provide a map name"
		sys.exit()

	# subprocess call to shell script that handles map tiling
	output = call(["./setup_map", source_map, map_name])

	if output != 0:
		print "Tiling unsuccessful!"
		sys.exit()
	else:
		metadata = MapMetadata()
		metadata.name = map_name

		tree = ET.parse('tiles/tilemapresource.xml')
		root = tree.getroot()

		for child in root:
			if child.tag == "SRS":
				metadata.projection = child.text

			if child.tag == "BoundingBox":		
				metadata.bounds = [float(child.attrib['miny']), float(child.attrib['minx']), float(child.attrib['maxy']), float(child.attrib['maxx'])]

		zoomLevels = []
		for tileset in root.iter('TileSet'):
			zoomLevels.append(int(tileset.attrib['order']))

		metadata.minZoomLevel = min(zoomLevels)
		metadata.maxZoomLevel = max(zoomLevels)		
		metadata.filename = map_name + "_tiles.zip"

		return metadata

def update_db(metadata):

	config = ConfigParser.ConfigParser(allow_no_value = True)
	config.read('config.ini')

	try:
		connection = mysql.connector.connect(database = config.get('mysqld', 'database'), user = config.get('mysqld', 'user'), password = config.get('mysqld', 'password'))
		cursor = connection.cursor()

		query = ("INSERT INTO sync_center_map " 
				"(name, projection, min_zoom_level, max_zoom_level, min_y, min_x, max_y, max_x, filename, last_modified) "
				"VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)")
		query_data = (metadata.name, metadata.projection, metadata.minZoomLevel, metadata.maxZoomLevel, 
					metadata.bounds[0], metadata.bounds[1], metadata.bounds[2], metadata.bounds[3], metadata.filename, datetime.now())

		# Insert new map metadata into database
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
		print "Usage: python setup_map.py source_image.tif map_name\nExpected number of arguments: 2. Got: " + str(len(sys.argv)-1)
		sys.exit()

	metadata = setup_map(sys.argv[1], sys.argv[2])
	update_db(metadata)

	# TODO: Delete source image, tfw and intermediary files

