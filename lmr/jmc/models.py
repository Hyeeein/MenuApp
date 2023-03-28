# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Allergy(models.Model):
    id = models.IntegerField(primary_key=True)
    allergy_name = models.CharField(db_column='allergy_ name', max_length=20, blank=True, null=True)  # Field renamed to remove unsuitable characters.

    class Meta:
        managed = True
        db_table = 'allergy'


class Category(models.Model):
    id = models.IntegerField(primary_key=True)
    name = models.CharField(max_length=20, blank=True, null=True)

    class Meta:
        managed = True
        db_table = 'category'


def MenuImagePath(instance, filename):
    restaurant = instance.restaurant
    return "menu/%s/%s" % (restaurant,filename)

class Menu(models.Model):
    id = models.IntegerField(primary_key=True)
    restaurant = models.ForeignKey('Restaurant', models.DO_NOTHING)
    category = models.CharField(max_length=20, blank=True, null=True)
    name = models.CharField(max_length=20, blank=True, null=True)
    price = models.IntegerField(blank=True, null=True)
    emotion = models.CharField(max_length=20, blank=True, null=True)
    weather = models.CharField(max_length=20, blank=True, null=True)
    image = models.ImageField(upload_to=MenuImagePath ,blank=True, null=True)

    class Meta:
        managed = True
        db_table = 'menu' 


class MenuAllergy(models.Model):
    allergy = models.ForeignKey(Allergy, models.DO_NOTHING)
    menu = models.ForeignKey(Menu, models.DO_NOTHING)

    class Meta:
        managed = True
        db_table = 'menu_allergy'


class MenuRecommendLog(models.Model):
    id = models.IntegerField(primary_key=True)
    datetime = models.DateTimeField(blank=True, null=True)
    user = models.ForeignKey('User', models.DO_NOTHING)
    menu = models.ForeignKey(Menu, models.DO_NOTHING)

    class Meta:
        managed = True
        db_table = 'menu_recommend_log'


class NutritionInformation(models.Model):
    id = models.IntegerField(primary_key=True)
    name = models.CharField(max_length=20, blank=True, null=True)
    gram = models.FloatField(blank=True, null=True)
    calorie = models.FloatField(blank=True, null=True)
    carbohydrate = models.FloatField(blank=True, null=True)
    protein = models.FloatField(blank=True, null=True)
    fat = models.FloatField(blank=True, null=True)
    saturatedfat = models.FloatField(blank=True, null=True)
    unsaturatedfat = models.FloatField(blank=True, null=True)
    cholesterol  = models.IntegerField(blank=True, null=True)
    sodium  = models.IntegerField(blank=True, null=True)
    potash = models.IntegerField(blank=True, null=True)
    menu = models.ForeignKey(Menu, models.DO_NOTHING)

    class Meta:
        managed = True
        db_table = 'nutrition_information'


class PreferredMenu(models.Model):
    preference = models.IntegerField()
    user = models.ForeignKey('User', models.CASCADE)
    menu = models.ForeignKey(Menu, models.DO_NOTHING)

    class Meta:
        managed = True
        db_table = 'preferred_menu'


class Restaurant(models.Model):
    id = models.IntegerField(primary_key=True)
    name = models.CharField(max_length=20)
    address = models.CharField(max_length=45)
    business_hours = models.CharField(max_length=20, blank=True, null=True)
    phone_number = models.CharField(max_length=20, blank=True, null=True)
    category_name = models.CharField(max_length=45, blank=True, null=True)
    image = models.ImageField(upload_to='restaurant/',blank=True, null=True)

    class Meta:
        managed = True
        db_table = 'restaurant'

def ReviewImagePath(instance, filename):
    restaurant = instance.restaurant
    return "review/%s/%s" % (restaurant,filename)

class Review(models.Model):
    id = models.IntegerField(primary_key=True)
    rating = models.FloatField(blank=True, null=True)
    content = models.TextField(blank=True, null=True)
    user = models.ForeignKey('User', models.CASCADE)
    menu = models.ForeignKey(Menu, models.DO_NOTHING)
    restaurant = models.ForeignKey('Restaurant', models.DO_NOTHING)
    image = models.ImageField(upload_to=ReviewImagePath,blank=True, null=True)

    class Meta:
        managed = True
        db_table = 'review'


class User(models.Model):
    id = models.IntegerField(primary_key=True)
    email = models.CharField(unique=True, max_length=30)
    password = models.CharField(max_length=45)
    gender = models.IntegerField(blank=True, null=True)
    age = models.IntegerField(blank=True, null=True)

    class Meta:
        managed = True
        db_table = 'user'


class UserAllergy(models.Model):
    user = models.ForeignKey(User, models.CASCADE)
    allergy = models.ForeignKey(Allergy, models.DO_NOTHING)

    class Meta:
        managed = True
        db_table = 'user_allergy'
