# Copyright (c) 2014, TrailScribe Team.
# This content is released under the MIT License. See the file named LICENSE for details.
# Core Django imports
from django.test import TestCase

# Imports from app
from sync_center import util
from tests.factories.map_factory import MapFactory


class UtilTests(TestCase):

    def setUp(self):
        map1 = MapFactory(id=1, name='Map 1', filename='map1.zip', last_modified='2014-01-01T00:00:01Z')
	map2 = MapFactory(id=2, name='Map 2', filename='map2.zip', last_modified='2014-01-01T00:00:02Z')
        map3 = MapFactory(id=3, name='Map 3', filename='map3.zip', last_modified='2014-01-01T00:00:03Z')

    def test_get_update_id_list(self):
        req_data_map1 = {}
        req_data_map1['id'] = 1;
        req_data_map1['last_modified'] = '2014-01-01T00:00:00Z'

        req_data_map2 = {}
        req_data_map2['id'] = 2;
        req_data_map2['last_modified'] = '2014-01-01T00:00:02Z'

        req_data = {}
        req_data['1'] = req_data_map1
        req_data['2'] = req_data_map2

        id_list = util.get_update_id_list('map', req_data)
        self.assertEquals(id_list, [1, 3])
