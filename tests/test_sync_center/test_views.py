# Copyright (c) 2014, TrailScribe Team.
# This content is released under the MIT License. See the file named LICENSE for details.
# Stdlib imports
import json

# Core Django imports
from django.core.urlresolvers import reverse
from django.test import TestCase

# Imports from app
from sync_center import views, util
from tests.factories.map_factory import MapFactory
from tests.factories.kml_factory import KMLFactory


class ViewTests(TestCase):

    def setUp(self):
        map1 = MapFactory(id=1, last_modified='2014-01-01T00:00:01Z')
	map2 = MapFactory(id=2, last_modified='2014-01-01T00:00:02Z')
	map3 = MapFactory(id=3, last_modified='2014-01-01T00:00:03Z')
	
	kml1 = KMLFactory(id=1, last_modified='2014-01-01T00:00:01Z', map=map1)
	kml2 = KMLFactory(id=2, last_modified='2014-01-01T00:00:02Z', map=map2)

    def test_sync_data(self):
        req_data = {}

        req_data_map1 = {}
        req_data_map1['id'] = 1;
        req_data_map1['last_modified'] = '2014-01-01T00:00:00Z'

        req_data_map2 = {}
        req_data_map2['id'] = 2;
        req_data_map2['last_modified'] = '2014-01-01T00:00:02Z'

        req_data_map = {}
        req_data_map['1'] = req_data_map1
        req_data_map['2'] = req_data_map2

	req_data['maps'] = req_data_map

    
        req_data_kml1 = {}
	req_data_kml1['id'] = 1
	req_data_kml1['last_modified'] = '2014-01-01T00:00:00Z'

	req_data_kml3 = {}
	req_data_kml3['id'] = 3
	req_data_kml3['last_modified'] = '2014-01-01T00:00:05Z'

        req_data_kml = {}
        req_data_kml['1'] = req_data_kml1
	req_data_kml['3'] = req_data_kml3

        req_data['kmls'] = req_data_kml


        resp = self.client.post('/sync/', content_type='application/json', data=json.dumps(req_data))
        resp = json.loads(resp.content)

        resp_map = []
        resp_kml = []

        for resp_data in resp:
	    model_name = resp_data['model']
	    pk = resp_data['pk']

	    if model_name == 'sync_center.map':
	        resp_map.append(pk)
	    elif model_name == 'sync_center.kml':
	        resp_kml.append(pk)

        self.assertEquals(resp_map, [1, 3])
	self.assertEquals(resp_kml, [1, 2])
