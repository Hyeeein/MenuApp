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
menu = pd.read_csv('../data/menu.csv', encoding='utf-8')
nutrient_menu = pd.read_csv('../data/nutrient.csv', encoding='cp949')
restaurant = pd.read_csv('../data/restaurant.csv', encoding='cp949')


# 추천시스템 함수 작성
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def rcm(request):

    # user 정보 GET
    user_id = request.session.get('user_id')
    user_allergy = UserAllergy.objects.filter(user_id=user_id).values()  # 사용자 알러지 정보 불러오기
    user_prefer = PreferredMenu.objects.filter(user_id=user_id).values() # 사용자 위시리스트 정보 불러오기
    
    # 예산, 가격, 날씨 정보 받아오기

    # 추천 메뉴 리스트업
    not_allergy_menu = rcm_allergy(user_id, user_allergy)
    # price_menu = rcm_price(want_price)
    # weather_menu = rcm_weather(user_weather)
    # emotion_menu = rcm_emotion(user_emotion)

    menu_list = not_allergy_menu

    # 리스트 내 중복 제거 (set 사용)
    rcm_set = set(menu_list)
    rcm_list = list(rcm_set)

    # 메뉴 리스트 중 한 가지 랜덤 선택 후 추천
    choice = random.randrange(0, len(rcm_list))
    personal_menu = menu.iloc[not_allergy_menu[choice]]

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
        if user_allergy.iloc[user_id][i] == 1:
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

