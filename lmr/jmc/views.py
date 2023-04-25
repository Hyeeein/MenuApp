from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from .models import *
from .serializers import *
from rest_framework.permissions import AllowAny, IsAuthenticated
from django.db.models import Max
import random
import requests

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def getRestaurant(request):
    datas = Restaurant.objects.all()
    serializer = RestaurantSerializer(datas, context={'request': request}, many=True)
    return Response(serializer.data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def getMenuByRestaurant(request, restaurant):
    datas = Menu.objects.filter(restaurant=restaurant)
    serializer = MenuSerializer(datas, context={'request': request}, many=True)
    return Response(serializer.data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def getReviewByRestaurant(request, restaurant):
    reviews = Review.objects.filter(restaurant=restaurant)
    serializer = ReviewGetSerializer(reviews, many=True)
    return Response(serializer.data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
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
        

@api_view(['GET']) # DB에 저장된 인덱스에 해당하는 리뷰를 조회
@permission_classes([IsAuthenticated])
def getReview(request, id):
    try:
        data = Review.objects.get(id=id)
    except Review.DoesNotExist:
        return Response(status=404)
    serializer = ReviewGetSerializer(data)
    return Response(serializer.data)

@api_view(['GET']) # 사용자가 작성한 리뷰 조회
@permission_classes([IsAuthenticated])
def getUserReview(request):
    tmp = Review.objects.filter(user_id=request.user.id)
    serializer = ReviewGetSerializer(tmp, many=True)
    return Response(serializer.data)

@api_view(['POST']) # 리뷰 작성 로직
@permission_classes([IsAuthenticated])
def postReview(request):
    if request.method == 'POST':
        uid = {"user":request.user.id}
        datas = dict(request.data, **uid)
        serializer = ReviewPostSerializer(data=datas)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=400)

@api_view(['DELETE']) # 리뷰 삭제 로직
@permission_classes([IsAuthenticated])
def deleteReview(request, id):
    try:
        review = Review.objects.get(id=id) # 사용자의 리뷰가 여러개일수있으니 리뷰 인덱스가 있어야함
        if review.user_id != request.user.id:
            return Response({"message":"user error"}, status=403)
    except Review.DoesNotExist:
        return Response({"message":"review not found"}, status=404)
    
    review.delete()
    return Response({"message":"delete success"}, status=200)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def getMenuPreference(request, n):
    menuobj = Menu.objects.all()
    max_id = menuobj.aggregate(max_id=Max("id"))['max_id']
    mc= menuobj.count()
    if n > mc:
        n = mc
    elif n >= 1 and n <= mc:
        pass
    else:
        n = 50
    random.seed(request.user.id)
    pk = random.sample(range(1, max_id+1),max_id)
    menu = Menu.objects.none()
    c = 0
    i = 0
    while c < n:
        menu = menu.union(Menu.objects.filter(id=pk[i]))
        if Menu.objects.filter(id=pk[i]).first()!=None:
            c+=1
        i+=1
    datas = menu
    serializer = MenuPreSerializer(datas, context={'request': request}, many=True)
    return Response(serializer.data)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def postMenuPreference(request):
    obj, created = PreferredMenu.objects.update_or_create(
    user_id=request.user.id,
    menu_id=request.data['menu'],
    defaults={'preference': request.data['preference']},
    )
    return Response({"message":"update success"}, status=200)

@api_view(['GET','PUT']) # 내 정보 수정에서 이메일, 닉네임, 한줄소개 데이터 조회
@permission_classes([IsAuthenticated])
def MypageView(request):
    if request.method == 'GET':
        tmp = User.objects.get(id=request.user.id)
        return Response({
            "nickname":tmp.nickname,
            "email":tmp.email,
            "introduction":tmp.introduction,
        })
    elif request.method == 'PUT':
        user = request.user
        serializer = UserUpdateSerializer(user, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST']) # 클라이언트에게 x,y(위도,경도) 값을 받아 현재 위치의 주소를 전달
@permission_classes([IsAuthenticated])
def AddressView(request):
    x = request.data.get('longitude')
    y = request.data.get('latitude')

    # 카카오맵 API에 전송할 파라미터 설정
    params = {
        'x': x,
        'y': y,
        'input_coord': 'WGS84'
    }

    # 카카오맵 API 호출
    response = requests.get('https://dapi.kakao.com/v2/local/geo/coord2address.json', params=params, headers={'Authorization': 'KakaoAK c2f38bb9330b0ea9d3c0b140afee1d73'})
    
    # 응답 처리
    if response.status_code == 200:
        result = response.json()['documents'][0]['address']['address_name']
    else:
        result = 'Failed to get address'

    return Response({'result': result})

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def postResfav(request):
    try:
        Resfav.objects.get(user_id=request.user.id, restaurant_id=request.data['restaurant']).delete()
    except Resfav.DoesNotExist:
        Resfav.objects.create(user_id=request.user.id, restaurant_id=request.data['restaurant'])
    return Response({"message":"success"}, status=200)
