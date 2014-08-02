# TrailScribe

More than 40 years ago, the last humans to walk on the Moon used cuff  checklists and map books to stay on track. What will the next generation of lunar explorers use?

The primary goal of the TrailScribe project is to build an open source Android mapping application usable as an electronic field notebook to collect, share and view information in the field.

### System Design

The solution comprises of two components: TrailScribe Client and TrailScribe Server. TrailScribe Client is built on top of technologies including OpenLayers, SQLite, and Android. TrailScribe Server is built on top of Python, Django, GDAL, and MySQL. They communicate with each other using a RESTful API.
  * [TrailScribe Client](https://github.com/CMUPracticum/TrailScribe/tree/master/TrailScribe)
  * [TralScribe Server](https://github.com/CMUPracticum/TrailScribeServer)

![System Design](https://github.com/CMUPracticum/TrailScribe/blob/master/SystemDesign.png "System Design")

#### Authors
  * [Ching-Lun Lin](https://github.com/cllin) - <cllin@alumni.cmu.edu>
  * [David Lee](https://github.com/Chih-Shao) - <chihshao.lee@alumni.cmu.edu>
  * [Emilia Torino](https://github.com/metorino) - <maria.e.torino@alumni.cmu.edu>
  * [Isil Demir](https://github.com/isilien) - <isildemir@alumni.cmu.edu>
  * [Mia Manalastas](https://github.com/mia-m) - [mia\_elline@alumni.cmu.edu]('mia_elline@alumni.cmu.edu')
