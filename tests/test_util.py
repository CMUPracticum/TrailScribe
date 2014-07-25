# Core Django imports
from django.test import TestCase

# Imports from app
from utility import util
from sync_center.models import Map, KML


class UtilTests(TestCase):

    def setUp(self):
        Map.objects.get_or_create(id=1, name='Map 1', last_modified='2014-01-01T00:00:01Z', min_zoom_level=11, max_zoom_level=15, min_x=37.3680027864,
                                     min_y=-122.134518893, max_x=37.4691074792, max_y=-121.998720996)
        Map.objects.get_or_create(id=2, name='Map 2', last_modified='2014-01-01T00:00:02Z', min_zoom_level=11, max_zoom_level=15, min_x=37.3680027864,
                                     min_y=-122.134518893, max_x=37.4691074792, max_y=-121.998720996)
        Map.objects.get_or_create(id=3, name='Map 3', last_modified='2014-01-01T00:00:03Z', min_zoom_level=11, max_zoom_level=15, min_x=37.3680027864,
                                     min_y=-122.134518893, max_x=37.4691074792, max_y=-121.998720996)

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
