# Stdlib imports
from datetime import datetime
from pytz import timezone

# Core Django imports
from django.utils.timezone import utc

# Imports from app
from sync_center.models import Map, KML


def get_update_id_list(model_name, req_data):
    db_data = None

    if model_name == 'map':
        db_data = Map.objects.all()
    elif model_name == 'kml':
        db_data = KML.objects.all()

    id_list = []

    for data in db_data:
        id_str = str(data.id)

        if id_str not in req_data.keys():
            id_list.append(data.id)
        else:
            req_last_modified = datetime.strptime(req_data[id_str]['last_modified'], '%Y-%m-%dT%H:%M:%SZ').replace(tzinfo = utc)

            if data.last_modified > req_last_modified:
                id_list.append(data.id)

    return id_list
