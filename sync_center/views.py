# Stdlib imports
import json
from datetime import datetime
from pytz import timezone

# Core Django imports
from django.http import HttpRequest, HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.core import serializers
from django.utils.timezone import utc

# Imports from app
from sync_center.models import Map, KML
from trailscribe import settings
from utility import util


def map_list(request):
    maps = Map.objects.all()
    for map in maps:
        map.filename = request.build_absolute_uri(settings.MEDIA_URL + 'map/' + map.filename)

    return HttpResponse(serializers.serialize('json', maps), content_type = "application/json")

def kml_list(request):
    kmls = KML.objects.all()
    for kml in kmls:
        kml.filename = request.build_absolute_url(settings.MEDIA_URL + 'kml/' + kml.filename)

    return HttpResponse(serializers.serialize('json', kmls), content_type = "application/json")

@csrf_exempt
def sync_data(request):
    if request.method == 'POST':
        request_data = json.loads(request.body)

        request_maps = request_data['maps']
        map_id_list = util.get_update_id_list('map', request_maps)
        maps = Map.objects.filter(id__in = map_id_list)

        for map in maps:
            map.filename = request.build_absolute_uri(settings.MEDIA_URL + 'map/' + map.filename)


        request_kmls = request_data['kmls']      
        kml_id_list = util.get_update_id_list('kml', request_maps)
        kmls = KML.objects.filter(id__in = kml_id_list)

        for kml in kmls:
            kml.filename = request.build_absolute_uri(settings.MEDIA_URL + 'kml/' + kml.filename)


        response = []
        for map in maps:
            response.append(map)

        for kml in kmls:
            response.append(kml)

        return HttpResponse(serializers.serialize('json', response), content_type = "application/json")
