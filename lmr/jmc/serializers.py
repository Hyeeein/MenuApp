from rest_framework.serializers import ModelSerializer
from rest_framework import serializers
from .models import *

class RestaurantSerializer(ModelSerializer):
    class Meta:
        model = Restaurant
        fields = '__all__'

class MenuSerializer(ModelSerializer):
    class Meta:
        model = Menu
        fields = '__all__'
        
class UserSerializer(ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'
        read_only_fields = ('id',)
'''
class UserSerializer(ModelSerializer):
    class Meta:
        model = User
        fields = ('id','email','gender','age')
'''
class ReviewSerializer(ModelSerializer):
    menu = MenuSerializer()
    user = UserSerializer()
    class Meta:
        model = Review
        fields = ('id','rating','content','user','menu','image')

class NutritionSerializer(ModelSerializer):
    class Meta:
        model = NutritionInformation
        fields = '__all__'

