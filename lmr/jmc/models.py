# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models

from django.contrib.auth.models import BaseUserManager, AbstractBaseUser, PermissionsMixin
from django.utils import timezone
from django.utils.translation import ugettext_lazy as _

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

'''
class User(models.Model):
    id = models.IntegerField(primary_key=True)
    email = models.CharField(unique=True, max_length=30)
    password = models.CharField(max_length=45)
    gender = models.IntegerField(blank=True, null=True)
    age = models.IntegerField(blank=True, null=True)

    class Meta:
        managed = True
        db_table = 'user'
'''

class UserManager(BaseUserManager):
    use_in_migrations = True

    def _create_user(self, email, password, **extra_fields):
        if not email:
            raise ValueError('The given email must be set')
        email = self.normalize_email(email)
        user = self.model(email=email, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_user(self, email, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', False)
        extra_fields.setdefault('is_superuser', False)
        return self._create_user(email, password, **extra_fields)

    def create_superuser(self, email, password, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)

        if extra_fields.get('is_staff') is not True:
            raise ValueError('Superuser must have is_staff=True.')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must have is_superuser=True.')

        return self.create_user(email, password, **extra_fields)


class User(AbstractBaseUser, PermissionsMixin):
    email = models.EmailField(verbose_name=_('email id'),max_length=64,unique=True,help_text='EMAIL ID.')
    username = models.CharField(verbose_name=_('username'), max_length=30, unique=True, null=True)
    gender = models.IntegerField(blank=True, null=True)
    age = models.IntegerField(blank=True, null=True)

    is_staff = models.BooleanField(
        _('staff status'),
        default=False,
        help_text=_('Designates whether the user can log into this admin site.'),
    )
    is_active = models.BooleanField(
        _('active'),
        default=True,
        help_text=_(
            'Designates whether this user should be treated as active. '
            'Unselect this instead of deleting accounts.'
        ),
    )
    date_joined = models.DateTimeField(_('date joined'), default=timezone.now)

    objects = UserManager()

    EMAIL_FIELD = 'email'
    USERNAME_FIELD = 'email'
    GENDER_FIELD = 'gender'
    AGE_FIELD = 'age'

    class Meta:
        verbose_name = _('user')
        verbose_name_plural = _('users')
        managed = True
        db_table = 'user'

    def __str__(self):
        return self.email

    def get_short_name(self):
        return self.email



class UserAllergy(models.Model):
    user = models.ForeignKey(User, models.CASCADE)
    allergy = models.ForeignKey(Allergy, models.DO_NOTHING)

    class Meta:
        managed = True
        db_table = 'user_allergy'
