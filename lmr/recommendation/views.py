from django.shortcuts import render
from rest_framework.response import Response
from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from django.db.models import Max

from jmc.models import *

import numpy as np
import pandas as pd
import random

from sklearn.feature_extraction import text
from sklearn.metrics.pairwise import cosine_similarity

import warnings
warnings.filterwarnings('ignore')

# 데이터 불러오기
menu_db = Menu.objects.all()
nutrient_db = Nutrition.objects.all()
restaurant_db = Restaurant.objects.all()

# Dataframe 형태로 변환
menu = pd.DataFrame(list(menu_db.values()))
nutrient_menu = pd.DataFrame(list(nutrient_db.values()))
restaurant = pd.DataFrame(list(restaurant_db.values()))

# 추천시스템 함수 작성
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def rcm(request):

    # user 정보 GET
    user_id = request.session.get('user_id')
    user_allergy = UserAllergy.objects.filter(user_id=user_id).values()  # 사용자 알러지 정보 불러오기
    user_prefer = PreferredMenu.objects.filter(user_id=user_id).values() # 사용자 위시리스트 정보 불러오기
    user_log = MenuRecommendLog.objects.filter(user_id=user_id).values() # 사용자 추천 로그 불러오기

    # 예산, 가격, 날씨 정보 받아오기
    

    # 추천 메뉴 리스트업
    not_allergy_menu = rcm_allergy(user_id, user_allergy)
    # price_menu = rcm_price(want_price)
    # weather_menu = rcm_weather(user_weather)
    # emotion_menu = rcm_emotion(user_emotion)

    menu_list = not_allergy_menu # 뒤에 다른 메뉴들 합치기

    rcm_set = set(menu_list)  # 리스트 내 중복 제거
    rcm_list = list(rcm_set)  # 다시 리스트 형태로 변환

    # 당일에 추천된 메뉴 제외 (로그 기록 활용)


    choice = random.randrange(0, len(rcm_list)) # 메뉴 리스트 중 한 가지 랜덤 선택 후 추천 (로그 구현 안되면 5개 보내주기)
    choice_id = rcm_list[choice]                # 선택된 메뉴의 메뉴 아이디 출력

    # 추천된 메뉴 요약 및 음식점 정보 추가 후 전달
    for i in range(len(menu)):
        if menu.iloc[i][0] == choice_id: menu_info = menu.iloc[i]
    menu_id = menu_info[0]
    menu_name = menu_info[3]
    menu_price = menu_info[4]
    restaurant_id = menu_info[1]

    for j in range(len(restaurant)):
        if restaurant.iloc[j][0] == restaurant_id: restaurant_info = restaurant.iloc[j]
    restaurant_name = restaurant_info[1]

    # 추천된 메뉴 반환
    personal_menu = [menu_id, menu_name, menu_price, restaurant_name]

    # 추천된 메뉴 로그 저장


    return Response(personal_menu)

'''
함수에서는 해당 메뉴 리스트업까지만 진행
각 함수에서 추출된 음식 메뉴들 간 중첩되는 메뉴를 찾고, 그 중에서 choice!
'''


### [기능 1] 사용자 정보에서 알러지 정보 제외하고 추천
def rcm_allergy(user_id, user_allergy):

    allergy_list = list(user_allergy) # 알러리 종류
    personal_allergy_list = []        # 유저별 알러지 임시로 담아둘 리스트

    # 알러지에 해당하는 음식 리스트 추가
    for i in range(len(allergy_list)):
        if user_allergy.iloc[user_id][i] == '1':
            personal_allergy_list.append(allergy_list[i])
    
    # nutrient 파일에서 해당 알러지가 없는 메뉴 리스트업
    not_allergy_menu = []

    for i in range(len(nutrient_menu)):
        allergy = str(nutrient_menu.iloc[i]['allergy'])
        allergy_list = allergy.split(',0')

        # 해당 알러지가 있는 메뉴를 제외한 메뉴 인덱스 리스트업
        add_menu = ''
        for j in allergy_list:
            if j not in personal_allergy_list: add_menu = 1
        if add_menu == 1: not_allergy_menu.append(i)

    return not_allergy_menu


### [기능 2] 사용자가 선택한 예산
def rcm_price(want_price):

    # 메뉴 리스트에서 해당 예산보다 작은 금액의 메뉴 리스트업
    menu_num = len(menu['id'])
    price_menu_index = []

    for i in range(menu_num):
        if menu.iloc[i]['price'] <= want_price:
            price_menu_index.append(i)
    
    return price_menu_index


### [기능 3] 사용자가 선택한 날씨
def rcm_weather(user_weather):
    
    # 메뉴 리스트에서 해당 날씨에 해당하는 메뉴 리스트업
    menu_num = len(menu['id'])
    menu_weather_list = []

    for i in range(menu_num):
        weather_list = menu.iloc[i]['weather'].split(',')

        add_menu = ""
        for j in weather_list:
            if j == user_weather: add_menu = 1
        if add_menu == 1: menu_weather_list.append(i)
    
    return menu_weather_list


### [기능 4] 사용자가 선택한 감정
def rcm_emotion(user_emotion):

    # 메뉴 리스트에서 해당 감정에 해당하는 메뉴 리스트업
    menu_num = len(menu['id'])
    menu_emotion_list = []

    for i in range(menu_num):
        emotion_list = menu.iloc[i]['emotion'].split(',')

        add_menu = ""
        for j in emotion_list:
            if j == user_emotion: add_menu = 1
        if add_menu == 1: menu_emotion_list.append(i)

    return menu_emotion_list


### [기능 5] 사용자 선호 메뉴를 비교하여 추천
def rcm_prefer():
    return 0

### [기능 6] 이미 추천된 메뉴는 제외
def re_rcm(user):
    return 0