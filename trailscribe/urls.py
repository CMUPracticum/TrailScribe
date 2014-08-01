# Copyright (c) 2014, TrailScribe Team.
# This content is released under the MIT License. See the file named LICENSE for details.
from django.conf.urls import patterns, include, url
from trailscribe import settings
from sync_center.views import map_list, kml_list, sync_data

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'trailscribe.views.home', name='home'),
    # url(r'^trailscribe/', include('trailscribe.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
    url(r'^maps/', map_list),
    url(r'^kmls/', kml_list),
    url(r'^sync/', sync_data, name='sync_data'),
)

if settings.DEBUG:
    urlpatterns += patterns('',
        url(r'^media/(?P<path>.*)$', 'django.views.static.serve', {'document_root': settings.MEDIA_ROOT}),
    )
