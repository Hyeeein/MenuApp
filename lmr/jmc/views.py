from rest_framework.response import Response
from rest_framework.decorators import api_view
from .models import *
from .serializers import *


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
    reviews = Review.objects.filter(restaurant=restaurant)
    serializer = ReviewSerializer(reviews, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getNutritionByMenu(request, menu):
    datas = NutritionInformation.objects.filter(menu=menu)
    serializer = NutritionSerializer(datas, many=True)
    return Response(serializer.data)
