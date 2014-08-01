# TrailScribeServer
TrailScribe Server is implemented using Python and Django. It provides services for TrailScribe clients to synchronize maps and KMLs for offline use. The two major components of TrailScribe server are Tiling Service and Sync Center.

## Tiling Service
Tiling Service is comprised of a Python and a shell script that allows the user to create map tiles from a GeoTIFF raster satellite image. It uses GDAL to process all geospatial data.

### Installation
* Requirements
 - Ubuntu 12.04.4
 - Python 2.7
 - MySQL-python
 - GDAL
 - python-gdal

* Installing MySQL-python
 - `apt-get install python-pip`
 - `pip install -U pip`
 - `apt-get install python-dev libmysqlclient-dev`
 - `pip install MySQL-python`
 - `pip install --allow-external mysql-connector-python mysql-connector-python`

* Installing GDAL and python-gdal
 - `apt-get install gdal-bin`
 - `apt-get install python-gdal`

* The files **setup_map** and **setup_map.py** must be at directory:
 - `~/trailscribe/scripts/`

* Create folder `tmp` at:
 - `mkdir ~/trailscribe/scripts/tmp`

* The files **setup_map** and **setup_map.py** must be executable
 - `chmod 755 setup_map setup_map.py`

### Usage
* Start by uploading a Geotiff map image to the appropriate location on the server.
 - `scp map.tif <user>@<host>:~/trailscribe/scripts/tmp/.`
* Upload .tfw (plain text - World file) at the same directory. The map and its respective TFW file must have the same names.
 - `scp map.tfw <user>@<host>:~/trailscribe/scripts/tmp/.`
* Run Python script. Usage: `python setup_map.py tmp/<source map> <target map name>`. 
 - `python setup_map.py tmp/map.tif map1`
* If this map already exists in the database, the user will be prompted for an update:
 - This map already exists in the database. Would you like to update the record for this map with the new data (Y/yes or N/no)?
 - If the user inputs Y/yes, the map tiles will be updated. If they input anything else, the script will abort:
 - Operation aborted. Please provide a unique map name and run the script again.
* Newly created tiles can be found at: `~/trailscribe/media/map/map1_tiles.zip`

### Alternative Usage
Alternatively, the user can just upload a zip folder containing TMS-structured map tiles in the following directory on the TrailScribe Server: 
* `scp my_map_tiles.zip <user>@<host>:~/trailscribe/media/map/my_map_tiles.zip`

And then run a SQL query to insert the map's metadata to the server database:
* `INSERT INTO sync_center_map (name, projection, min_zoom_level, max_zoom_level, min_y, min_x, max_y, max_x, filename, last_modified) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, now())`


## Sync Center
Sync Center is a Django web app that provides RESTful API for TrailScribe Client to download latest maps and KMLs for offline use. It is integrated with Apache HTTP Server and uses MySQL as the database server.

### Installation
* Requirements
 - Ubuntu 12.04.4
 - Python 2.7
 - Django 1.4.11
 - MySQL 5.5.37
 - Apache 2.2.22
 - MySQLdb
 - mod_wsgi

* Install MySQLdb
 - `sudo apt-get install python-mysqldb

* Install mod_wsgi & Apache Worker MPM
 - `sudo apt-get install apache2-mpm-worker
 - `sudo apt-get install apache2-threaded-dev
 - `pip install mod_wsgi

### Set Up Environment
* Clone TrailScribe Server project from GitHub
 - `cd /home/scribe
 - `git clone https://github.com/CMUPracticum/TrailScribeServer.git
 - `mv TrailScribeServer trailscribe

* Create Database for TrailScribe Server
 - Log on MySQL
   `mysql -u <username> -p <password>
 - Create database
   `CREATE DATABASE trailscribe;

* Integrate Django and Apache HTTP Server
 - Edit httpd.conf
   `vim /etc/apache2/httpd.conf
 - Add the following content to httpd.conf
   `WSGIScriptAlias / /home/scribe/trailscribe/trailscribe/wsgi.py
   `WSGIPythonPath /home/scribe/trailscribe
   `<Directory /home/scribe/trailscribe/trailscribe>
   `  <Files wsgi.py>
   `    Order deny,allow
   `    Allow from all
   `  </Files>
   `</Directory>
 - Restart Apache HTTP Server
   `sudo service apache2 restart