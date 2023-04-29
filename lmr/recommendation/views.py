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
from pytz import timezone
from datetime import datetime

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

    global menu, nutrient_menu, restaurant

    # user 정보 GET
    user_id = request.session.get('user_id')
    #user_id = request.data['user']
    user_allergy = list(UserAllergy.objects.filter(user_id=5).values())  # 사용자 알러지 정보 불러오기
    user_prefer = PreferredMenu.objects.filter(user_id=5).values()       # 사용자 위시리스트 정보 불러오기
    user_log = MenuRecommendLog.objects.filter(user_id=5).values()       # 사용자 추천 로그 불러오기

    # 예산, 가격, 날씨 정보 받아오기
    # user_price = int(request.data['price'])
    # user_weather = request.data['weather']
    # user_emotion = request.data['emotion']

    ### 메인 기능 : 사용자 선호 메뉴를 중심으로 메뉴 추천 ###

    # 1) 사용자가 좋아하는 메뉴 다 가져오기
    like_menu = user_prefer.values()
    like_menu_list = list() ; dislike_menu_list = list()

    for i in like_menu:
        # 좋아하는 메뉴
        if i['preference'] == 1:
            like_menu_list.append(i['menu_id'])

        # 싫어하는 메뉴
        elif i['preference'] == -1:
            dislike_menu_list.append(i['menu_id'])

    # 2) 특징 데이터를 사용한 내용 기반 필터링

    # 메뉴 특징 데이터 생성 및 menu에 컬럼 추가
    # nutrient_menu 데이터의 ingredient 피처를 menu_id 기준으로 정렬
    nutrient_menu = nutrient_menu.sort_values(by=['menu_id'])
    tmp_df = nutrient_menu['ingredient']
    tmp_df = tmp_df.reset_index(drop=True)

    # 메뉴 데이터 특징 피처 만들어서 저장
    menu['feature'] = menu['category'] + " " + menu['name'] + " " + menu['weather'] + " " + menu['emotion'] + " " + tmp_df
    menu_feature = menu['feature'].tolist()
    
    # TF-IDF Vectorizer Object 구현
    tfidf = text.TfidfVectorizer(input=menu_feature)
    tfidf_matrix = tfidf.fit_transform(menu_feature)

    # 코사인 유사도 계산
    similarity = cosine_similarity(tfidf_matrix)

    # 중복된 추천 제외하고, 특징 데이터 사용
    indices = pd.Series(menu.index, index=menu.feature).drop_duplicates()

    # 사용자 선호 메뉴와 유사한 메뉴 추천 (좋아하는 메뉴만큼 for문 진행)
    rcm_menu = list()

    for i in like_menu_list:
        for j in range(len(menu)):
            if i == j:
                feature = menu.iloc[j]['feature']
        
        index = indices[feature]

        similarity_scores = list(enumerate(similarity[index]))
        similarity_scores = sorted(similarity_scores, key=lambda x: x[1], reverse=True)
        similarity_scores = similarity_scores[1:11]        # 결과는 메뉴 아이디, 유사도 (10개씩 추출)
        menu_indices = [i[0] for i in similarity_scores]   # 메뉴 아이디 리스트 (메뉴 아이디는 1로 시작하므로)

        for k in menu_indices:
            menu_id = menu.iloc[k]['id']
            rcm_menu.append(menu_id)

    # 3) 사용자 알러지가 있는 메뉴 제외
    allergy_menu = rcm_allergy(user_allergy)  # 사용자 알러지가 있는 메뉴만 불러오기 (아이디 값)
    
    # 위에서 추천된 메뉴 중 해당 메뉴가 있을 경우 제외 (위에 코드 완성되면 추가 수정)
    for i in rcm_menu:
        for j in allergy_menu:
            if i == j: rcm_menu.remove(i)
    
    # ---------- 구현 해야 함 ----------

    # 4) 사용자 예산, 기분, 날씨 반영
    # price_menu = rcm_price(user_price)
    # weather_emotion_menu = rcm_weather_emotion(user_weather, user_emotion)

    # # price_weather_emotion = set(price_menu + weather_emotion_menu)
    # price_weather_emotion = set(price_menu) & set(weather_emotion_menu)
    # price_weather_emotion_list = list(price_weather_emotion)

    # rcm_menu_03 = []
    # for i in rcm_menu_02:
    #     for j in price_weather_emotion_list:
    #         if i == j: rcm_menu_03.append(i)

    # return Response(rcm_menu_03)

    # ---------------------------------
    
    # 5) 당일에 추천된 메뉴 제외 (로그 기록 활용)

    # 당일에 추천된 메뉴 아이디 리스트
    rcm_log_list = rcm_log(user_log)

    for i in rcm_menu:
        for j in rcm_log_list:
            if i == j: rcm_menu.remove(i)

    # 6) 최종 추천 메뉴 리스트업
    rcm_list = list(set(rcm_menu))

    choice = random.randrange(0, len(rcm_list)) # 메뉴 리스트 중 한 가지 랜덤 선택 후 추천 (로그 구현 안되면 5개 보내주기)
    choice_id = rcm_list[choice]                # 선택된 메뉴의 메뉴 아이디 출력

    # 7) 추천된 메뉴 요약 및 음식점 정보 추가 후 전달
    for i in range(len(menu)):
        if menu.iloc[i][0] == choice_id: menu_info = menu.iloc[i]
    menu_id = menu_info[0]
    menu_name = menu_info[3]
    menu_price = menu_info[4]
    restaurant_id = menu_info[1]

    for j in range(len(restaurant)):
        if restaurant.iloc[j][0] == restaurant_id: restaurant_info = restaurant.iloc[j]
    restaurant_name = restaurant_info[1]

    # 8) 추천된 메뉴 반환
    personal_menu = { "menu_id" : menu_id,
                     "menu_name" : menu_name,
                     "menu_price" : menu_price,
                     "restaurant_id" : restaurant_id,
                     "restaurant_name" : restaurant_name}

    # ---------- 구현 해야 함 ----------

    # 9) 추천된 메뉴 로그 저장

    # ---------------------------------

    return Response(personal_menu)

# ---------- 수정 해야 함 ----------

# 사용자 정보에서 알러지가 든 메뉴만 리스트업 하는 함수
def rcm_allergy(user_allergy):

    allergy_list = ['달걀', '우유', '밀', '콩', '땅콩', '생선', '고기', '조개', '갑각류']
    user_allergy_list = list(user_allergy[0].values())  # 앞에 id와 user_id 포함
    user_allergy_list_name = []                         # 유저별 알러지 임시로 담아둘 리스트

    # 알러지에 해당하는 음식 리스트 추가
    for i in range(2, len(user_allergy_list)):
        if user_allergy_list[i] == '1':
            user_allergy_list_name.append(allergy_list[i-2])
    
    # nutrient 파일에서 해당 알러지가 있는 메뉴 리스트업 (nutrient_menu는 메뉴 아이디 순서로 정렬되어 있음)
    allergy_menu = []

    for i in range(len(nutrient_menu)):
        allergy = str(nutrient_menu.iloc[i]['allergy'])
        allergy_info = allergy.split(',')

        # 해당 알러지가 있는 메뉴 인덱스 리스트업
        add_menu = ''
        for j in user_allergy_list_name:
            if j in allergy_info: add_menu = 1
        if add_menu == 1: allergy_menu.append(nutrient_menu.iloc[i]['menu_id'])

    return allergy_menu

# ---------------------------------

# 사용자가 선택한 예산 범위의 메뉴 리스트업 하는 함수
def rcm_price(user_price):

    # 메뉴 리스트에서 해당 예산보다 작은 금액의 메뉴 리스트업
    menu_num = len(menu['id'])
    price_menu_index = []

    for i in range(menu_num):
        if menu.iloc[i]['price'] <= user_price:
            price_menu_index.append(i)
    
    return price_menu_index


# 사용자가 선택한 날씨와 감정을 반영한 메뉴를 리스트업 하는 함수
def rcm_weather_emotion(user_weather, user_emotion):
    
    # 메뉴 리스트에서 해당 날씨, 감정에 해당하는 메뉴 리스트업
    menu_num = len(menu['id'])
    menu_weather_list = []
    menu_emotion_list = []

    for menu in range(menu_num):
        weather_list = menu.iloc[menu]['weather'].split(',')
        emotion_list = menu.iloc[menu]['emotion'].split(',')

        add_menu = ""
        for weather in weather_list:
            if weather == user_weather: add_menu = 1
        if add_menu == 1: menu_weather_list.append(menu)

        add_menu2 = ""
        for emotion in emotion_list:
            if emotion == user_emotion: add_menu2 = 1
        if add_menu2 == 1: menu_emotion_list.append(menu)
    
    # 사용자가 선택한 날씨, 감정이 겹치는 메뉴 리스트업
    menu_weather_emotion_list = []
    for weather_menu in menu_weather_list:
        for emotion_menu in menu_emotion_list:
            if weather_menu == emotion_menu: menu_weather_emotion_list.append(weather_menu)

    return menu_weather_emotion_list

# 사용자 로그 기록을 활용하여 당일 추천된 메뉴 재추천 안되게 하는 함수
def rcm_log(user_log):
    
    today = datetime.now(timezone('Asia/Seoul'))
    today_new = today.strftime("%Y-%m-%d")  # 오늘 날짜 (2023-04-30)

    # 기존에 추천 받았던 내역 불러오기 (오늘 날짜만)
    rcm_log = user_log.values()
    rcm_log_list = list()

    for i in rcm_log:
        day = i['datetime'].strftime("%Y-%m-%d")
        if today_new == day:
            rcm_log_list.append(i['menu_id'])
    
    return rcm_log_list