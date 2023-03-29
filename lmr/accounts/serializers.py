from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import User
from django.contrib.auth.models import update_last_login
from django.contrib.auth import authenticate
from rest_framework.authtoken.models import Token



User = get_user_model()

class UserCreateSerializer(serializers.Serializer):
    username = serializers.CharField(required=True)
    email = serializers.EmailField(required=True)
    password = serializers.CharField(required=True)
    repassword = serializers.CharField(required=True)
    gender = serializers.CharField(required=True)
    age = serializers.IntegerField(required=True)
    allergy = serializers.CharField(required=True)
    preference = serializers.CharField(required=True)

    def validate(self, data):
        password = data.get('password')
        repassword = data.get('repassword')
        if password != repassword:
            raise serializers.ValidationError("Passwords don't match")
        return data

    def create(self, validated_data):
        user = User.objects.create(
            username=validated_data['username'],
            email=validated_data['email'],
            gender=validated_data['gender'],
            age=validated_data['age'],
            allergy=validated_data['allergy'],
            preference = validated_data['preference'],
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
        token1, _ = Token.objects.get_or_create(user=user)

        if user is None:
            return {
                'email': 'None'
            }
        try:
            update_last_login(None, user)
        except User.DoesNotExist:
            raise serializers.ValidationError(
                'User with given email and password does not exists'
            )
        return {
            'email': user.email,
            'token': token1.key
        }