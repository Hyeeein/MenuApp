from django.urls import path
from . import views

urlpatterns = [
    path('menurecommend', views.rcm),
]