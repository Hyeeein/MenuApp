from django.urls import path
from . import views

urlpatterns = [
    path('restaurant', views.getRestaurant, name="getRestaurant"),
    path('restaurant/<int:restaurant>/menu', views.getMenuByRestaurant, name="getMenuByRestaurant"),
    path('restaurant/<int:restaurant>/review', views.getReviewByRestaurant, name="getReviewByRestaurant"),
    path('menu/<int:menu>/nutrition', views.getNutritionByMenu, name="getNutritionByMenu"),
    path('signup', views.registers),
    path('login', views.login),
]
