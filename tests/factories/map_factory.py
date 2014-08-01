# Copyright (c) 2014, TrailScribe Team.
# This content is released under the MIT License. See the file named LICENSE for details.
import factory
from sync_center import models

class MapFactory(factory.django.DjangoModelFactory):
    class Meta:
        model = models.Map
	django_get_or_create = ('id', 'last_modified',)

    name = factory.Sequence(lambda n: 'Map %d' % n)
    filename = factory.Sequence(lambda n: 'map%d.zip' % n)
    projection = 'EPSG:900913'
    min_zoom_level = 11
    max_zoom_level = 15
    min_x = 37.3680027864
    min_y = -122.134518893
    max_x = 37.4691074792
    max_y = -121.998720996
