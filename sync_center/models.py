# Copyright (c) 2014, TrailScribe Team.
# This content is released under the MIT License. See the file named LICENSE for details.
from django.db import models

# Create your models here.
class Map(models.Model):
    name = models.CharField(max_length = 255)
    projection = models.TextField() 
    min_zoom_level = models.PositiveIntegerField()
    max_zoom_level = models.PositiveIntegerField()
    min_x = models.FloatField()
    min_y = models.FloatField()
    max_x = models.FloatField()
    max_y = models.FloatField()
    filename = models.CharField(max_length = 255)
    last_modified = models.DateTimeField()


class KML(models.Model):
    name = models.CharField(max_length = 255)
    description = models.TextField()
    last_modified = models.DateTimeField()
    filename = models.CharField(max_length = 255)
    map = models.ForeignKey(Map)
