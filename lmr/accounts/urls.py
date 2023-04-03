from django.urls import path
from . import views

urlpatterns = [
    path('signup', views.createUser), # 회원가입 post 요청
    path('signup/<int:pk>', views.createUser), # 회원 delete, put 요청 시 pk가 있어야함
    path('login', views.loginUser),
]
