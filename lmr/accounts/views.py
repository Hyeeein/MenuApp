from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate
from rest_framework.views import APIView
from .serializers import UserCreateSerializer
from .serializers import UserLoginSerializer
from jmc.models import User
from rest_framework.permissions import AllowAny
from django.shortcuts import get_object_or_404
    
@api_view(['POST', 'PUT', 'DELETE'])
@permission_classes([AllowAny])
def createUser(request, pk=None):
    user_obj = get_object_or_404(User, pk=pk) if pk else None

    if request.method == 'POST':
        serializer = UserCreateSerializer(data=request.data)
        if not serializer.is_valid(raise_exception=True):
            return Response({"message": "Request Body Error."}, status=status.HTTP_409_CONFLICT)

        if User.objects.filter(email=serializer.validated_data['email']).first() is None:
            serializer.save()
            return Response({"message": "ok"}, status=status.HTTP_201_CREATED)
        return Response({"message": "duplicate email"}, status=status.HTTP_409_CONFLICT)

    elif request.method == 'PUT': # 사용자 정보 수정 로직
        if not user_obj:
            return Response({"message": "User not found."}, status=status.HTTP_404_NOT_FOUND)

        serializer = UserCreateSerializer(instance=user_obj, data=request.data)
        if not serializer.is_valid(raise_exception=True):
            return Response({"message": "Request Body Error."}, status=status.HTTP_409_CONFLICT)

        if User.objects.filter(email=serializer.validated_data['email']).exclude(pk=pk).first() is None:
            serializer.save()
            return Response({"message": "ok"}, status=status.HTTP_200_OK)
        return Response({"message": "duplicate email"}, status=status.HTTP_409_CONFLICT)

    elif request.method == 'DELETE': # 사용자 삭제 로직
        if not user_obj:
            return Response({"message": "User not found."}, status=status.HTTP_404_NOT_FOUND)

        user_obj.delete()
        return Response({"message": "ok"}, status=status.HTTP_204_NO_CONTENT)

    return Response({"message": "Method not allowed."}, status=status.HTTP_405_METHOD_NOT_ALLOWED)


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

