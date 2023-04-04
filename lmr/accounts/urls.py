from django.urls import path
from . import views

urlpatterns = [
    path('signup', views.createUser),
    path('login', views.loginUser),
    path('logout', views.logoutUser),
    path('delete', views.deleteUser),
    path('checkemail', views.checkEmail),
    path('checknickname', views.checkNickname),
]
