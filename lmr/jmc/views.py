from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from .models import *
from .serializers import *
from rest_framework.permissions import AllowAny, IsAuthenticated

@api_view(['GET'])
def getRestaurant(request):
    datas = Restaurant.objects.all()
    serializer = RestaurantSerializer(datas, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getMenuByRestaurant(request, restaurant):
    uid = request.user.id
    datas = Menu.objects.filter(restaurant=restaurant)
    serializer = MenuSerializer(datas, context={'request': request}, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getReviewByRestaurant(request, restaurant):
    reviews = Review.objects.filter(restaurant=restaurant)
    serializer = ReviewSerializer(reviews, many=True)
    return Response(serializer.data)

@api_view(['GET'])
def getNutritionByMenu(request, menu):
    datas = Nutrition.objects.filter(menu=menu)
    serializer = NutritionSerializer(datas, many=True)
    return Response(serializer.data)

@api_view(['GET', 'POST', 'PUT'])
@permission_classes([IsAuthenticated])
def UserAllergyView(request):
    if request.method == 'POST':
        uid = {"user":request.user.id}
        datas = dict(request.data, **uid)
        serializer = UserAllergySerializer(data=datas)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    if request.method == 'PUT':
        uid = {"user":request.user.id}
        requestdatas = dict(request.data, **uid)
        datas = UserAllergy.objects.get(user=request.user.id)
        serializer = UserAllergySerializer(instance=datas, data=requestdatas)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    if request.method == 'GET':
        datas = UserAllergy.objects.filter(user=request.user.id)
        serializer = UserAllergySerializer(datas, many=True)
        return Response(serializer.data)
    
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        
