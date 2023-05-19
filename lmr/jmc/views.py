from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from .models import *
from .serializers import *
from rest_framework.permissions import AllowAny, IsAuthenticated
from django.db.models import Max
import random
import requests
from math import radians, sin, cos, sqrt, atan2


def distance(lat1, lon1, lat2, lon2):
    R = 6371 # 지구의 반경 (km)
    #lat1 = float(lat1)
    #lon1 = float(lon1)
    dLat = radians(lat2 - lat1)
    dLon = radians(lon2 - lon1)
    lat1 = radians(lat1)
    lat2 = radians(lat2)

    a = sin(dLat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dLon / 2) ** 2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    distance = R * c
    return distance

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
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['GET']) # 사용자가 작성한 리뷰 조회
@permission_classes([IsAuthenticated])
def getUserReview(request):
    tmp = Review.objects.filter(user_id=request.user.id)
    serializer = ReviewGetSerializer(tmp, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)

@api_view(['POST']) # 리뷰 작성 로직
@permission_classes([IsAuthenticated])
def postReview(request):
    if request.method == 'POST':
        uid = {"user":request.user.id}
        #datas = dict(request.data, **uid)
        datas = {"rating" : request.POST['rating'],
                 "content" : request.POST['content'],
                 "menu" : request.POST['menu'],
                 "restaurant" : request.POST['restaurant']}
        image = {"image":request.FILES['image']}
        datas.update(uid)
        datas.update(image)
        serializer = ReviewPostSerializer(data=datas)
        if serializer.is_valid():
            serializer.save()
            # 리뷰 작성 시 선호메뉴 데이터 추가
            reviewMenu(request.user.id, datas)
            return Response(serializer.data, status=status.HTTP_200_OK)


# 작성된 리뷰를 가져와 선호, 비선호 메뉴에 추가하는 함수
def reviewMenu(uid, datas):

    user_id = uid
    menu_id = datas.get('menu')
    content = datas.get('content')

    # 키워드로 입력한 단어
    a = "최고예요!"     # 긍정 1
    b = "낫배드"        # 보통 0
    c = "별로예요..."   # 부정 -1
    d = "빨리 나와요"   # 긍정 1
    e = "갓성비!"       # 긍정 1

    preference = 0

    # content에 a,b,c,d,e가 존재하면 preferredMenu DB에 저장하도록 바꿀 수 있는 권리(right)를 줌
    if a in content or d in content or e in content:
        preference = 1
    elif b in content:
        preference = 0
    elif c in content:
        preference = -1
    
    # 권리가 있으면 DB에 선호도를 저장
    if PreferredMenu.objects.filter(user_id=user_id, menu_id=menu_id).first() == None:
        PreferredMenu.objects.create(user_id=user_id, menu_id=menu_id, preference=preference)


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
    if n > 50:
        n = 50
    elif n >= 1 and n <= 50:
        pass
    else:
        n = 50
    #random.seed(request.user.id)
    pk = random.sample(range(1, max_id+1),max_id)
    menu = Menu.objects.none()
    c = 0
    i = 0
    while c < n:
        menu = menu.union(menuobj.filter(id=pk[i]))
        if menuobj.filter(id=pk[i]).exists():
            c+=1
        i+=1
    datas = menu
    serializer = MenuPreSerializer(datas, context={'request': request}, many=True)
    return Response(serializer.data)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def getAllMenuPreference(request):
    datas = Menu.objects.all()
    serializer = AllMenuPreSerializer(datas, context={'request': request}, many=True)
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
            "id":request.user.id,
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
        try:
            result = response.json()['documents'][0]['address']['address_name']
        except:
            result = 'Failed to get address'
            return Response({'result': result}, status=status.HTTP_400_BAD_REQUEST)
    else:
        result = 'Failed to get address'
        return Response({'result': result}, status=status.HTTP_400_BAD_REQUEST)

    return Response({'result': result}, status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def AroundRestaurant(request):
    mylongitude = request.data.get('longitude')
    mylatitude = request.data.get('latitude')
    mylongitude = float(mylongitude)
    mylatitude = float(mylatitude)


    tmp = Restaurant.objects.filter(address__contains='')
    serializer = AroundRestaurantSerializer(tmp, context={'request': request}, many=True)

    restlist = []
    
    i = 0
    n = len(serializer.data)
    while i<n:
        dist = distance(mylatitude, mylongitude, serializer.data[i]['latitude'], serializer.data[i]['longitude'])
        if dist <= 1:
            serializer.data[i]['distance'] = int(dist * 1000)
            restlist.append(serializer.data[i])
        i+=1

    return Response(restlist, status=200)


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def postResfav(request):
    try:
        Resfav.objects.get(user_id=request.user.id, restaurant_id=request.data['restaurant']).delete()
    except Resfav.DoesNotExist:
        Resfav.objects.create(user_id=request.user.id, restaurant_id=request.data['restaurant'])
    return Response({"message":"success"}, status=200)
