from rest_framework import serializers
from django.contrib.auth import get_user_model
from jmc.models import User
from django.contrib.auth.models import update_last_login
from django.contrib.auth import authenticate
from rest_framework.authtoken.models import Token

class UserCreateSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True)
    password = serializers.CharField(required=True)
    nickname = serializers.CharField()
    gender = serializers.CharField()
    age = serializers.IntegerField()

    def create(self, validated_data):
        user = User.objects.create(
            email=validated_data['email'],
            nickname=validated_data['nickname'],
            gender=validated_data['gender'],
            age=validated_data['age'],
        )
        user.set_password(validated_data['password']) 
        user.save()
        return user
    
class UserLoginSerializer(serializers.Serializer):
    email = serializers.CharField(max_length=64)
    password = serializers.CharField(max_length=128, write_only=True)
    token = serializers.CharField(max_length=255, read_only=True)

    def validate(self, data):
        email = data.get("email", None)
        password = data.get("password", None)
        user = authenticate(email=email, password=password)
        
        if user is None:
            raise serializers.ValidationError(
                'User with given email and password does not exists'
                )
        
        update_last_login(None, user)
        
        token1, _ = Token.objects.get_or_create(user=user)
        
        return {
            'email': user.email,
            'token': token1.key
        }


