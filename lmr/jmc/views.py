from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import *
from .serializers import *
from rest_framework import status
from django.contrib.auth import authenticate, login
from django.http import JsonResponse


@api_view(['GET'])
def getRestaurant(request):
    datas = Restaurant.objects.all()
    serializer = RestaurantSerializer(datas, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getMenuByRestaurant(request, restaurant):
    datas = Menu.objects.filter(restaurant=restaurant)
    serializer = MenuSerializer(datas, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getMenu(request):
    datas = Menu.objects.all()
    serializer = MenuSerializer(datas, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getReviewByRestaurant(request, restaurant):
    menu_ids = Menu.objects.filter(restaurant=restaurant).values_list('id', flat=True)
    reviews = Review.objects.filter(menu__in=menu_ids)
    serializer = ReviewSerializer(reviews, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getNutritionByMenu(request, menu):
    datas = NutritionInformation.objects.filter(menu=menu)
    serializer = NutritionSerializer(datas, many=True)
    return Response(serializer.data)

@api_view(['POST'])
def registers(request):
    serializer = UserSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
def login(request):
    email = request.data.get('email')
    password = request.data.get('password')
    user = authenticate(email=email, password=password)
    if user is not None:
        return JsonResponse({'success': True})
    else:
        return JsonResponse({'success': False, 'message': 'Invalid credentials'})