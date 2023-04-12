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
    checkallergy = serializers.SerializerMethodField()
    
    class Meta:
        model = Menu
        fields = ('id','restaurant','category','name','price','emotion','weather','image','checkallergy')

    def get_checkallergy(self, obj):
        user = self.context.get("request").user
        egg = UserAllergy.objects.get(user=user.id).달걀
        milk = UserAllergy.objects.get(user=user.id).우유
        wheat = UserAllergy.objects.get(user=user.id).밀
        bean = UserAllergy.objects.get(user=user.id).콩
        peanut = UserAllergy.objects.get(user=user.id).땅콩
        fish = UserAllergy.objects.get(user=user.id).생선
        meat = UserAllergy.objects.get(user=user.id).고기
        shellfish = UserAllergy.objects.get(user=user.id).조개
        crustaceans = UserAllergy.objects.get(user=user.id).갑각류

        if egg==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="달걀").exists():
                return True
        if milk==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="우유").exists():
                return True
        if wheat==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="밀").exists():
                return True
        if bean==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="콩").exclude(allergy__contains="땅콩").exists():
                return True
        if peanut==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="땅콩").exists():
                return True
        if fish==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="생선").exists():
                return True
        if meat==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="고기").exists():
                return True
        if shellfish==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="조개").exists():
                return True
        if crustaceans==1:
            if Nutrition.objects.filter(menu=obj.id, allergy__contains="갑각류").exists():
                return True
        else:
            return False

class UserNicknameSerializer(ModelSerializer):
    class Meta:
        model = User
        fields = ['nickname']



class MenuNameSerializer(ModelSerializer):
    
    class Meta:
        model = Menu
        fields = ('id','name')

class RestaurantNameSerializer(ModelSerializer):
    
    class Meta:
        model = Restaurant
        fields = ('id','name')

class ReviewGetSerializer(ModelSerializer):
    menu = MenuNameSerializer()
    restaurant = RestaurantNameSerializer()
    user = UserNicknameSerializer()
    class Meta:
        model = Review
        fields = ('id','rating','content','datetime','user','menu','restaurant','image')

class ReviewPostSerializer(ModelSerializer):
    class Meta:
        model = Review
        fields = ('rating','content','user','menu','restaurant','image')

class NutritionSerializer(ModelSerializer):
    class Meta:
        model = Nutrition
        fields = '__all__'

class UserAllergySerializer(ModelSerializer):    
    class Meta:
        model = UserAllergy
        fields = '__all__'

class MenuPreSerializer(ModelSerializer):
    preference = serializers.SerializerMethodField()
    
    class Meta:
        model = Menu
        fields = ('id','category','name','image','preference')

    def get_preference(self, obj):
        user = self.context.get("request").user
        pre = PreferredMenu.objects.filter(user=user.id, menu=obj.id).first()
        if pre is None:
            return -1
        preference = pre.preference
        return preference

class PreMenuSerializer(ModelSerializer):
    class Meta:
        model = PreferredMenu
        fields = '__all__'
    
class UserUpdateSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('username', 'email', 'nickname', 'Introduction')