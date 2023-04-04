from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate
from rest_framework.views import APIView
from .serializers import *
from jmc.models import User
from rest_framework.authtoken.models import Token


@api_view(['POST'])
@permission_classes([AllowAny])
def createUser(request):
    if request.method == 'POST':
        serializer = UserCreateSerializer(data=request.data)
        if not serializer.is_valid(raise_exception=True):
            return Response({"message": "Request Body Error."}, status=status.HTTP_409_CONFLICT)

        if User.objects.filter(email=serializer.validated_data['email']).first() is None:
            serializer.save()
            return Response({"message": "ok"}, status=status.HTTP_201_CREATED)
        return Response({"message": "duplicate email"}, status=status.HTTP_409_CONFLICT)

@api_view(['POST'])
@permission_classes([AllowAny])
def checkEmail(request):
    email = request.data['email']
    if User.objects.filter(email=email).exists():
        return Response({"message": "duplicate email","available":False})
    else:
        return Response({"message": "not duplicate email","available":True})

@api_view(['POST'])
@permission_classes([AllowAny])
def checkNickname(request):
    nickname = request.data['nickname']
    if User.objects.filter(nickname=nickname).exists():
        return Response({"message": "duplicate nickname","available":False})
    else:
        return Response({"message": "not duplicate nickname","available":True})
    
@api_view(['POST'])
@permission_classes([AllowAny])
def loginUser(request):
    if request.method == 'POST':
        serializer = UserLoginSerializer(data=request.data)
        if not serializer.is_valid(raise_exception=True):
            return Response({"message": "Request Body Error."}, status=status.HTTP_409_CONFLICT)
        if serializer.validated_data['email'] == "None":
            return Response({'message': 'fail'}, status=status.HTTP_409_CONFLICT)
        token = serializer.data['token']
        
        response = {
            'success': 'True',
            'token':serializer.data['token'],
        }
        return Response(response, status=status.HTTP_200_OK)

@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def logoutUser(request):
    token = Token.objects.filter(user_id=request.user.id) #유저 id로 authtoken_token에 저장된 토큰 찾기
    token.delete() #저장된 토큰 삭제를 통한 로그아웃
    return Response({"message": "logout"}, status=status.HTTP_200_OK)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def deleteUser(request):
    
    user = authenticate(email=request.user.email, password=request.data.get("password"))
    if user is None:
        return Response({'message': 'password does not exists'}, status=status.HTTP_409_CONFLICT)
    request.user.delete()
    
    return Response({"message": "delete user"}, status=status.HTTP_200_OK)



