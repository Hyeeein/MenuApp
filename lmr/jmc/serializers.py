from rest_framework.serializers import ModelSerializer
from rest_framework import serializers
from django.db.models import Avg
from .models import *

class RestaurantSerializer(ModelSerializer):
    rating = serializers.SerializerMethodField()
    
    class Meta:
        model = Restaurant
        fields = ('id','name','address','business_hours','phone_number','category_name','image','rating')

    def get_rating(self, obj):
        rating = Review.objects.filter(restaurant=obj.id).aggregate(Avg('rating'))['rating__avg']
        return rating

class MenuSerializer(ModelSerializer):
    class Meta:
        model = Menu
        fields = '__all__'

class UserSerializer(ModelSerializer):
    class Meta:
        model = User
        fields = ('id','email','gender','age')

class ReviewSerializer(ModelSerializer):
    menu = MenuSerializer()
    user = UserSerializer()
    class Meta:
        model = Review
        fields = ('id','rating','content','user','menu','restaurant','image')

class NutritionSerializer(ModelSerializer):
    class Meta:
        model = Nutrition
        fields = '__all__'
