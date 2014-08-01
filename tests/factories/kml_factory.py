# Copyright (c) 2014, TrailScribe Team.
# This content is released under the MIT License. See the file named LICENSE for details.
import factory
from sync_center import models
from tests.factories.map_factory import MapFactory

class KMLFactory(factory.django.DjangoModelFactory):
    class Meta:
        model = models.KML
	django_get_or_create = ('id', 'last_modified', 'map')

    name = factory.Sequence(lambda n: "KML %d" % n)
    description = factory.Sequence(lambda n: 'KML Description %d' % n)
    filename = factory.Sequence(lambda n: 'kml%d.zip' % n)
